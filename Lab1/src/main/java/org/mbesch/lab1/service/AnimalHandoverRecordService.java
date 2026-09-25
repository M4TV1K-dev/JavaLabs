package org.mbesch.lab1.service;

import org.mbesch.lab1.dto.AnimalHandoverRecordRequestDto;
import org.mbesch.lab1.dto.AnimalHandoverRecordResponseDto;
import org.mbesch.lab1.exception.BadRequestException;
import org.mbesch.lab1.exception.ResourceNotFoundException;
import org.mbesch.lab1.model.AnimalHandoverRecord;
import org.mbesch.lab1.repository.AdoptionApplicationRepository;
import org.mbesch.lab1.repository.AnimalHandoverRecordRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AnimalHandoverRecordService {
    private final AnimalHandoverRecordRepository animalHandoverRecordRepository;
    private final AdoptionApplicationRepository adoptionApplicationRepository;

    public AnimalHandoverRecordService(AnimalHandoverRecordRepository animalHandoverRecordRepository,
                                       AdoptionApplicationRepository adoptionApplicationRepository) {
        this.animalHandoverRecordRepository = animalHandoverRecordRepository;
        this.adoptionApplicationRepository = adoptionApplicationRepository;
    }

    public List<AnimalHandoverRecordResponseDto> getAllRecords() {
        return animalHandoverRecordRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public AnimalHandoverRecordResponseDto getRecordById(Long id) {
        AnimalHandoverRecord record = animalHandoverRecordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Animal handover record not found with id: " + id));
        return mapToDto(record);
    }

    public AnimalHandoverRecordResponseDto createRecord(AnimalHandoverRecordRequestDto dto) {
        if (dto.getApplicationId() == null) {
            throw new BadRequestException("Application ID must not be null");
        }
        if (!adoptionApplicationRepository.existsById(dto.getApplicationId())) {
            throw new BadRequestException("Adoption application not found with id: " + dto.getApplicationId());
        }

        String status = dto.getStatus();
        if (status == null || status.isBlank()) {
            status = "DRAFT";
        }

        AnimalHandoverRecord record = new AnimalHandoverRecord();
        record.setApplicationId(dto.getApplicationId());
        record.setStatus(status.trim());

        // Server-side generation of timestamps
        Instant now = Instant.now();
        record.setPreparationDate(now);

        if (status.trim().equalsIgnoreCase("CONFIRMED") || status.trim().equalsIgnoreCase("ПОДТВЕРЖДЕНО")) {
            record.setConfirmationDate(now);
        } else {
            record.setConfirmationDate(null);
        }

        AnimalHandoverRecord saved = animalHandoverRecordRepository.save(record);
        return mapToDto(saved);
    }

    public AnimalHandoverRecordResponseDto updateRecord(Long id, AnimalHandoverRecordRequestDto dto) {
        AnimalHandoverRecord record = animalHandoverRecordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Animal handover record not found with id: " + id));

        if (dto.getApplicationId() != null) {
            if (!adoptionApplicationRepository.existsById(dto.getApplicationId())) {
                throw new BadRequestException("Adoption application not found with id: " + dto.getApplicationId());
            }
            record.setApplicationId(dto.getApplicationId());
        }

        if (dto.getStatus() != null) {
            if (dto.getStatus().isBlank()) {
                throw new BadRequestException("Status must not be blank");
            }
            String newStatus = dto.getStatus().trim();
            record.setStatus(newStatus);

            // Server-side timestamp update on confirmation
            if (newStatus.equalsIgnoreCase("CONFIRMED") || newStatus.equalsIgnoreCase("ПОДТВЕРЖДЕНО")) {
                if (record.getConfirmationDate() == null) {
                    record.setConfirmationDate(Instant.now());
                }
            } else if (newStatus.equalsIgnoreCase("DRAFT") || newStatus.equalsIgnoreCase("CANCELLED")) {
                record.setConfirmationDate(null);
            }
        }

        // preparationDate is never modified from client
        AnimalHandoverRecord updated = animalHandoverRecordRepository.save(record);
        return mapToDto(updated);
    }

    public void deleteRecord(Long id) {
        if (!animalHandoverRecordRepository.existsById(id)) {
            throw new ResourceNotFoundException("Animal handover record not found with id: " + id);
        }
        animalHandoverRecordRepository.deleteById(id);
    }

    private AnimalHandoverRecordResponseDto mapToDto(AnimalHandoverRecord record) {
        return new AnimalHandoverRecordResponseDto(
                record.getId(),
                record.getApplicationId(),
                record.getPreparationDate(),
                record.getConfirmationDate(),
                record.getStatus()
        );
    }
}
