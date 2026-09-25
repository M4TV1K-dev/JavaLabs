package org.mbesch.lab1.service;

import org.mbesch.lab1.dto.AnimalRequestDto;
import org.mbesch.lab1.dto.AnimalResponseDto;
import org.mbesch.lab1.exception.BadRequestException;
import org.mbesch.lab1.exception.ResourceNotFoundException;
import org.mbesch.lab1.model.AdoptionApplication;
import org.mbesch.lab1.model.Animal;
import org.mbesch.lab1.model.AnimalHandoverRecord;
import org.mbesch.lab1.model.Enclosure;
import org.mbesch.lab1.repository.AdoptionApplicationRepository;
import org.mbesch.lab1.repository.AnimalHandoverRecordRepository;
import org.mbesch.lab1.repository.AnimalRepository;
import org.mbesch.lab1.repository.EnclosureRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class AnimalService {
    private final AnimalRepository animalRepository;
    private final EnclosureRepository enclosureRepository;
    private final AdoptionApplicationRepository adoptionApplicationRepository;
    private final AnimalHandoverRecordRepository animalHandoverRecordRepository;

    public AnimalService(AnimalRepository animalRepository,
                         EnclosureRepository enclosureRepository,
                         AdoptionApplicationRepository adoptionApplicationRepository,
                         AnimalHandoverRecordRepository animalHandoverRecordRepository) {
        this.animalRepository = animalRepository;
        this.enclosureRepository = enclosureRepository;
        this.adoptionApplicationRepository = adoptionApplicationRepository;
        this.animalHandoverRecordRepository = animalHandoverRecordRepository;
    }

    public List<AnimalResponseDto> getAllAnimals() {
        return animalRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public AnimalResponseDto getAnimalById(Long id) {
        Animal animal = animalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Animal not found with id: " + id));
        return mapToDto(animal);
    }

    public AnimalResponseDto createAnimal(AnimalRequestDto dto) {
        if (dto.getName() == null || dto.getName().isBlank()) {
            throw new BadRequestException("Animal name must not be blank");
        }
        if (dto.getSpecies() == null || dto.getSpecies().isBlank()) {
            throw new BadRequestException("Animal species must not be blank");
        }
        if (dto.getAge() == null || dto.getAge() < 0) {
            throw new BadRequestException("Animal age must be a non-negative number");
        }

        Long encId = dto.getEnclosureId();
        if (encId != null) {
            Enclosure enclosure = enclosureRepository.findById(encId)
                    .orElseThrow(() -> new BadRequestException("Enclosure not found with id: " + encId));
            validateEnclosureForAnimal(enclosure, dto.getSpecies(), null);
        }

        String placementStatus = dto.getPlacementStatus();
        if (placementStatus == null || placementStatus.isBlank()) {
            placementStatus = encId != null ? "IN_ENCLOSURE" : "IN_SHELTER";
        }

        Animal animal = new Animal();
        animal.setName(dto.getName().trim());
        animal.setSpecies(dto.getSpecies().trim());
        animal.setAge(dto.getAge());
        animal.setPlacementStatus(placementStatus.trim());
        animal.setEnclosureId(encId);

        Animal saved = animalRepository.save(animal);

        if (encId != null) {
            Enclosure enclosure = enclosureRepository.findById(encId).orElse(null);
            if (enclosure != null) {
                if (!enclosure.getAnimalIds().contains(saved.getId())) {
                    enclosure.getAnimalIds().add(saved.getId());
                    enclosureRepository.save(enclosure);
                }
            }
        }

        return mapToDto(saved);
    }

    public AnimalResponseDto updateAnimal(Long id, AnimalRequestDto dto) {
        Animal animal = animalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Animal not found with id: " + id));

        if (dto.getName() != null) {
            if (dto.getName().isBlank()) {
                throw new BadRequestException("Animal name must not be blank");
            }
            animal.setName(dto.getName().trim());
        }

        String speciesToValidate = animal.getSpecies();
        if (dto.getSpecies() != null) {
            if (dto.getSpecies().isBlank()) {
                throw new BadRequestException("Animal species must not be blank");
            }
            animal.setSpecies(dto.getSpecies().trim());
            speciesToValidate = animal.getSpecies();
        }

        if (dto.getAge() != null) {
            if (dto.getAge() < 0) {
                throw new BadRequestException("Animal age must be a non-negative number");
            }
            animal.setAge(dto.getAge());
        }

        if (dto.getPlacementStatus() != null) {
            if (dto.getPlacementStatus().isBlank()) {
                throw new BadRequestException("Placement status must not be blank");
            }
            animal.setPlacementStatus(dto.getPlacementStatus().trim());
        }

        if (dto.getEnclosureId() != null) {
            Long newEncId = dto.getEnclosureId();
            if (!Objects.equals(animal.getEnclosureId(), newEncId)) {
                Enclosure newEnclosure = enclosureRepository.findById(newEncId)
                        .orElseThrow(() -> new BadRequestException("Enclosure not found with id: " + newEncId));
                validateEnclosureForAnimal(newEnclosure, speciesToValidate, animal.getId());

                // Remove from old enclosure
                if (animal.getEnclosureId() != null) {
                    enclosureRepository.findById(animal.getEnclosureId()).ifPresent(oldEnc -> {
                        oldEnc.getAnimalIds().remove(animal.getId());
                        enclosureRepository.save(oldEnc);
                    });
                }

                // Add to new enclosure
                if (!newEnclosure.getAnimalIds().contains(animal.getId())) {
                    newEnclosure.getAnimalIds().add(animal.getId());
                    enclosureRepository.save(newEnclosure);
                }
                animal.setEnclosureId(newEncId);
            }
        }

        Animal updated = animalRepository.save(animal);
        return mapToDto(updated);
    }

    public void deleteAnimal(Long id) {
        Animal animal = animalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Animal not found with id: " + id));

        // Cascade remove from enclosure
        if (animal.getEnclosureId() != null) {
            enclosureRepository.findById(animal.getEnclosureId()).ifPresent(enc -> {
                enc.getAnimalIds().remove(id);
                enclosureRepository.save(enc);
            });
        }

        // Cascade cleanup: delete all adoption applications referencing this animal
        List<AdoptionApplication> applications = adoptionApplicationRepository.findByAnimalId(id);
        for (AdoptionApplication application : applications) {
            // Delete all handover records referencing this application
            List<AnimalHandoverRecord> records = animalHandoverRecordRepository.findByApplicationId(application.getId());
            for (AnimalHandoverRecord record : records) {
                animalHandoverRecordRepository.deleteById(record.getId());
            }
            adoptionApplicationRepository.deleteById(application.getId());
        }

        animalRepository.deleteById(id);
    }

    private void validateEnclosureForAnimal(Enclosure enclosure, String species, Long currentAnimalId) {
        if (enclosure.getAllowedSpecies() != null && !enclosure.getAllowedSpecies().isEmpty()) {
            boolean allowed = enclosure.getAllowedSpecies().stream()
                    .anyMatch(s -> s.equalsIgnoreCase(species));
            if (!allowed) {
                throw new BadRequestException("Enclosure " + enclosure.getId() + " does not allow species: " + species);
            }
        }
        int currentOccupancy = enclosure.getAnimalIds().size();
        if (currentAnimalId != null && enclosure.getAnimalIds().contains(currentAnimalId)) {
            // already counts as part of occupancy
        } else if (enclosure.getCapacity() != null && currentOccupancy >= enclosure.getCapacity()) {
            throw new BadRequestException("Enclosure " + enclosure.getId() + " is already at maximum capacity (" + enclosure.getCapacity() + ")");
        }
    }

    private AnimalResponseDto mapToDto(Animal animal) {
        return new AnimalResponseDto(
                animal.getId(),
                animal.getName(),
                animal.getSpecies(),
                animal.getAge(),
                animal.getPlacementStatus(),
                animal.getEnclosureId()
        );
    }
}
