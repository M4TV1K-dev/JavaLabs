package org.mbesch.lab3.controller;

import org.mbesch.lab3.entity.*;
import org.mbesch.lab3.service.ShelterOperationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/operations")
public class ShelterOperationController {
    private final ShelterOperationService operations;

    public ShelterOperationController(ShelterOperationService operations) {
        this.operations = operations;
    }

    /**
     * Бизнес-операция 1: Заселение животного в вольер.
     * Затрагивает сущности: Animal + Enclosure.
     */
    @PostMapping("/assign-enclosure")
    public ResponseEntity<Animal> assignEnclosure(
            @RequestParam Long animalId,
            @RequestParam Long enclosureId) {
        return ResponseEntity.ok(operations.assignEnclosure(animalId, enclosureId));
    }

    /**
     * Бизнес-операция 2: Подача заявки на усыновление.
     * Затрагивает сущности: AdoptionApplication + Animal + User.
     */
    @PostMapping("/submit-application")
    public ResponseEntity<AdoptionApplication> submitApplication(
            @RequestParam Long animalId,
            @RequestParam Long applicantId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(operations.submitApplication(animalId, applicantId));
    }

    /**
     * Бизнес-операция 3: Одобрение заявки и автоматическое формирование акта передачи.
     * Затрагивает сущности: AdoptionApplication + AnimalHandoverRecord.
     */
    @PostMapping("/approve-application/{applicationId}")
    public ResponseEntity<AnimalHandoverRecord> approveApplication(
            @PathVariable Long applicationId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(operations.approveApplication(applicationId));
    }

    /**
     * Бизнес-операция 4: Завершение передачи животного по акту.
     * Затрагивает сущности: AnimalHandoverRecord + AdoptionApplication + Animal + Enclosure.
     */
    @PostMapping("/confirm-handover/{recordId}")
    public ResponseEntity<AnimalHandoverRecord> confirmHandover(
            @PathVariable Long recordId) {
        return ResponseEntity.ok(operations.confirmHandover(recordId));
    }

    /**
     * Бизнес-операция 5: Экстренный перевод животного на карантин / лечение.
     * Затрагивает сущности: Animal + AdoptionApplication.
     */
    @PostMapping("/quarantine-animal/{animalId}")
    public ResponseEntity<Animal> quarantineAnimal(
            @PathVariable Long animalId) {
        return ResponseEntity.ok(operations.quarantineAnimal(animalId));
    }
}
