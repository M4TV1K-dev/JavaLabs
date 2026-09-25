package org.mbesch.lab1.service;

import org.mbesch.lab1.dto.AdoptionApplicationRequestDto;
import org.mbesch.lab1.dto.AdoptionApplicationResponseDto;
import org.mbesch.lab1.exception.BadRequestException;
import org.mbesch.lab1.exception.ResourceNotFoundException;
import org.mbesch.lab1.model.AdoptionApplication;
import org.mbesch.lab1.model.AnimalHandoverRecord;
import org.mbesch.lab1.repository.AdoptionApplicationRepository;
import org.mbesch.lab1.repository.AnimalHandoverRecordRepository;
import org.mbesch.lab1.repository.AnimalRepository;
import org.mbesch.lab1.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdoptionApplicationService {
    private final AdoptionApplicationRepository adoptionApplicationRepository;
    private final AnimalRepository animalRepository;
    private final UserRepository userRepository;
    private final AnimalHandoverRecordRepository animalHandoverRecordRepository;

    public AdoptionApplicationService(AdoptionApplicationRepository adoptionApplicationRepository,
                                      AnimalRepository animalRepository,
                                      UserRepository userRepository,
                                      AnimalHandoverRecordRepository animalHandoverRecordRepository) {
        this.adoptionApplicationRepository = adoptionApplicationRepository;
        this.animalRepository = animalRepository;
        this.userRepository = userRepository;
        this.animalHandoverRecordRepository = animalHandoverRecordRepository;
    }

    public List<AdoptionApplicationResponseDto> getAllApplications() {
        return adoptionApplicationRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public AdoptionApplicationResponseDto getApplicationById(Long id) {
        AdoptionApplication application = adoptionApplicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Adoption application not found with id: " + id));
        return mapToDto(application);
    }

    public AdoptionApplicationResponseDto createApplication(AdoptionApplicationRequestDto dto) {
        if (dto.getAnimalId() == null) {
            throw new BadRequestException("Animal ID must not be null");
        }
        if (dto.getUserId() == null) {
            throw new BadRequestException("User ID must not be null");
        }

        if (!animalRepository.existsById(dto.getAnimalId())) {
            throw new BadRequestException("Animal not found with id: " + dto.getAnimalId());
        }
        if (!userRepository.existsById(dto.getUserId())) {
            throw new BadRequestException("User not found with id: " + dto.getUserId());
        }

        String status = dto.getStatus();
        if (status == null || status.isBlank()) {
            status = "PENDING";
        }

        AdoptionApplication application = new AdoptionApplication();
        application.setAnimalId(dto.getAnimalId());
        application.setUserId(dto.getUserId());
        application.setStatus(status.trim());
        // Server-side timestamp generation strictly
        application.setCreatedAt(Instant.now());

        AdoptionApplication saved = adoptionApplicationRepository.save(application);
        return mapToDto(saved);
    }

    public AdoptionApplicationResponseDto updateApplication(Long id, AdoptionApplicationRequestDto dto) {
        AdoptionApplication application = adoptionApplicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Adoption application not found with id: " + id));

        if (dto.getAnimalId() != null) {
            if (!animalRepository.existsById(dto.getAnimalId())) {
                throw new BadRequestException("Animal not found with id: " + dto.getAnimalId());
            }
            application.setAnimalId(dto.getAnimalId());
        }

        if (dto.getUserId() != null) {
            if (!userRepository.existsById(dto.getUserId())) {
                throw new BadRequestException("User not found with id: " + dto.getUserId());
            }
            application.setUserId(dto.getUserId());
        }

        if (dto.getStatus() != null) {
            if (dto.getStatus().isBlank()) {
                throw new BadRequestException("Status must not be blank");
            }
            application.setStatus(dto.getStatus().trim());
        }

        // createdAt is never overwritten from client
        AdoptionApplication updated = adoptionApplicationRepository.save(application);
        return mapToDto(updated);
    }

    public void deleteApplication(Long id) {
        adoptionApplicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Adoption application not found with id: " + id));

        // Cascade cleanup: delete all handover records associated with this application
        List<AnimalHandoverRecord> records = animalHandoverRecordRepository.findByApplicationId(id);
        for (AnimalHandoverRecord record : records) {
            animalHandoverRecordRepository.deleteById(record.getId());
        }

        adoptionApplicationRepository.deleteById(id);
    }

    private AdoptionApplicationResponseDto mapToDto(AdoptionApplication application) {
        return new AdoptionApplicationResponseDto(
                application.getId(),
                application.getAnimalId(),
                application.getUserId(),
                application.getStatus(),
                application.getCreatedAt()
        );
    }
}
