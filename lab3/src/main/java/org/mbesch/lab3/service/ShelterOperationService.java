package org.mbesch.lab3.service;

import org.mbesch.lab3.entity.*;
import org.mbesch.lab3.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class ShelterOperationService {
    private final AnimalRepository animals;
    private final EnclosureRepository enclosures;
    private final UserRepository users;
    private final AdoptionApplicationRepository applications;
    private final AnimalHandoverRecordRepository records;

    public ShelterOperationService(
            AnimalRepository animals,
            EnclosureRepository enclosures,
            UserRepository users,
            AdoptionApplicationRepository applications,
            AnimalHandoverRecordRepository records) {
        this.animals = animals;
        this.enclosures = enclosures;
        this.users = users;
        this.applications = applications;
        this.records = records;
    }

    /**
     * Бизнес-операция 1: Заселение животного в вольер.
     * Затрагивает: Animal + Enclosure.
     * Проверяет вместимость вольера, допустимые виды и текущее состояние животного.
     */
    @Transactional
    public Animal assignEnclosure(Long animalId, Long enclosureId) {
        Animal animal = animals.findById(animalId)
                .orElseThrow(() -> new NotFoundException("Animal", animalId));
        Enclosure enclosure = enclosures.findById(enclosureId)
                .orElseThrow(() -> new NotFoundException("Enclosure", enclosureId));

        if ("ADOPTED".equalsIgnoreCase(animal.getPlacementStatus())) {
            throw new IllegalArgumentException("Нельзя заселить в вольер уже усыновленное животное");
        }
        if ("QUARANTINE".equalsIgnoreCase(animal.getPlacementStatus())) {
            throw new IllegalArgumentException("Животное находится на карантине и не может быть переведено в общий вольер");
        }

        // Проверка допустимости вида
        String allowed = enclosure.getAllowedAnimalTypes().toLowerCase();
        if (!allowed.contains(animal.getSpecies().toLowerCase())) {
            throw new IllegalArgumentException("Вольер '" + enclosure.getName() + "' не предназначен для вида: " + animal.getSpecies());
        }

        // Проверка вместимости
        long currentOccupants = animals.countByEnclosureId(enclosureId);
        if (currentOccupants >= enclosure.getCapacity()) {
            throw new IllegalArgumentException("Вольер заполнен: вместимость " + enclosure.getCapacity() + " исчерпана");
        }

        animal.setEnclosureId(enclosureId);
        animal.setPlacementStatus("IN_SHELTER");
        return animals.save(animal);
    }

    /**
     * Бизнес-операция 2: Подача заявки на усыновление.
     * Затрагивает: AdoptionApplication + Animal + User.
     * Бронирует животное, проверяет доступность животного и отсутствие дублирующих заявок.
     */
    @Transactional
    public AdoptionApplication submitApplication(Long animalId, Long applicantId) {
        User user = users.findById(applicantId)
                .orElseThrow(() -> new NotFoundException("User", applicantId));
        Animal animal = animals.findById(animalId)
                .orElseThrow(() -> new NotFoundException("Animal", animalId));

        if (!"IN_SHELTER".equalsIgnoreCase(animal.getPlacementStatus())) {
            throw new IllegalArgumentException("Животное недоступно для усыновления (текущий статус: " + animal.getPlacementStatus() + ")");
        }

        boolean hasActiveApp = applications.existsByAnimalIdAndApplicantIdAndStatusIn(
                animalId, applicantId, List.of("SUBMITTED", "APPROVED"));
        if (hasActiveApp) {
            throw new IllegalArgumentException("У заявителя уже есть активная заявка на это животное");
        }

        animal.setPlacementStatus("ADOPTION_IN_PROGRESS");
        animals.save(animal);

        AdoptionApplication application = new AdoptionApplication(null, animalId, applicantId, "SUBMITTED", Instant.now());
        return applications.save(application);
    }

    /**
     * Бизнес-операция 3: Одобрение заявки и автоматическое формирование акта передачи.
     * Затрагивает: AdoptionApplication + AnimalHandoverRecord.
     * Проверяет статус заявки и генерирует подготовленный акт передачи.
     */
    @Transactional
    public AnimalHandoverRecord approveApplication(Long applicationId) {
        AdoptionApplication app = applications.findById(applicationId)
                .orElseThrow(() -> new NotFoundException("AdoptionApplication", applicationId));

        if (!"SUBMITTED".equalsIgnoreCase(app.getStatus())) {
            throw new IllegalArgumentException("Одобрить можно только заявку в статусе SUBMITTED. Текущий статус: " + app.getStatus());
        }

        app.setStatus("APPROVED");
        applications.save(app);

        AnimalHandoverRecord record = new AnimalHandoverRecord(null, applicationId, Instant.now(), null, "PREPARED");
        return records.save(record);
    }

    /**
     * Бизнес-операция 4: Завершение передачи животного по акту.
     * Затрагивает: AnimalHandoverRecord + AdoptionApplication + Animal + Enclosure.
     * Фиксирует передачу, закрывает заявку, меняет статус животного на ADOPTED и освобождает место в вольере.
     */
    @Transactional
    public AnimalHandoverRecord confirmHandover(Long recordId) {
        AnimalHandoverRecord record = records.findById(recordId)
                .orElseThrow(() -> new NotFoundException("AnimalHandoverRecord", recordId));

        if (!"PREPARED".equalsIgnoreCase(record.getStatus())) {
            throw new IllegalArgumentException("Подтвердить можно только акт в статусе PREPARED. Текущий статус: " + record.getStatus());
        }

        AdoptionApplication app = applications.findById(record.getApplicationId())
                .orElseThrow(() -> new NotFoundException("AdoptionApplication", record.getApplicationId()));

        if (!"APPROVED".equalsIgnoreCase(app.getStatus())) {
            throw new IllegalArgumentException("Связанная заявка должна быть в статусе APPROVED");
        }

        Animal animal = animals.findById(app.getAnimalId())
                .orElseThrow(() -> new NotFoundException("Animal", app.getAnimalId()));

        // Фиксация передачи
        record.setStatus("CONFIRMED");
        record.setConfirmationDate(Instant.now());
        records.save(record);

        app.setStatus("COMPLETED");
        applications.save(app);

        animal.setPlacementStatus("ADOPTED");
        animal.setEnclosureId(null); // Освобождаем место в вольере
        animals.save(animal);

        return record;
    }

    /**
     * Бизнес-операция 5: Экстренный перевод животного на карантин / лечение.
     * Затрагивает: Animal + AdoptionApplication.
     * Выселяет животное из вольера и автоматически отменяет все ожидающие заявки на усыновление.
     */
    @Transactional
    public Animal quarantineAnimal(Long animalId) {
        Animal animal = animals.findById(animalId)
                .orElseThrow(() -> new NotFoundException("Animal", animalId));

        if ("ADOPTED".equalsIgnoreCase(animal.getPlacementStatus())) {
            throw new IllegalArgumentException("Усыновленное животное больше не находится в приюте");
        }
        if ("QUARANTINE".equalsIgnoreCase(animal.getPlacementStatus())) {
            throw new IllegalArgumentException("Животное уже находится на карантине");
        }

        animal.setPlacementStatus("QUARANTINE");
        animal.setEnclosureId(null);
        animals.save(animal);

        // Отклоняем все незавершенные заявки на это животное
        List<AdoptionApplication> pendingApps = applications.findByAnimalId(animalId);
        for (AdoptionApplication app : pendingApps) {
            if ("SUBMITTED".equalsIgnoreCase(app.getStatus()) || "ADOPTION_IN_PROGRESS".equalsIgnoreCase(app.getStatus())) {
                app.setStatus("REJECTED_QUARANTINE");
                applications.save(app);
            }
        }

        return animal;
    }
}
