package org.mbesch.lab1.service;

import org.mbesch.lab1.dto.UserRequestDto;
import org.mbesch.lab1.dto.UserResponseDto;
import org.mbesch.lab1.exception.BadRequestException;
import org.mbesch.lab1.exception.ResourceNotFoundException;
import org.mbesch.lab1.model.AdoptionApplication;
import org.mbesch.lab1.model.AnimalHandoverRecord;
import org.mbesch.lab1.model.User;
import org.mbesch.lab1.repository.AdoptionApplicationRepository;
import org.mbesch.lab1.repository.AnimalHandoverRecordRepository;
import org.mbesch.lab1.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final AdoptionApplicationRepository adoptionApplicationRepository;
    private final AnimalHandoverRecordRepository animalHandoverRecordRepository;

    public UserService(UserRepository userRepository,
                       AdoptionApplicationRepository adoptionApplicationRepository,
                       AnimalHandoverRecordRepository animalHandoverRecordRepository) {
        this.userRepository = userRepository;
        this.adoptionApplicationRepository = adoptionApplicationRepository;
        this.animalHandoverRecordRepository = animalHandoverRecordRepository;
    }

    public List<UserResponseDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public UserResponseDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return mapToDto(user);
    }

    public UserResponseDto createUser(UserRequestDto dto) {
        if (dto.getUsername() == null || dto.getUsername().isBlank()) {
            throw new BadRequestException("Username must not be blank");
        }
        if (dto.getFullName() == null || dto.getFullName().isBlank()) {
            throw new BadRequestException("Full name must not be blank");
        }

        User user = new User();
        user.setUsername(dto.getUsername().trim());
        user.setFullName(dto.getFullName().trim());
        user.setEmail(dto.getEmail() != null ? dto.getEmail().trim() : null);
        user.setPhoneNumber(dto.getPhoneNumber() != null ? dto.getPhoneNumber().trim() : null);
        user.setRole(dto.getRole() != null ? dto.getRole().trim() : "ADOPTER");

        User saved = userRepository.save(user);
        return mapToDto(saved);
    }

    public UserResponseDto updateUser(Long id, UserRequestDto dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        if (dto.getUsername() != null) {
            if (dto.getUsername().isBlank()) {
                throw new BadRequestException("Username must not be blank");
            }
            user.setUsername(dto.getUsername().trim());
        }

        if (dto.getFullName() != null) {
            if (dto.getFullName().isBlank()) {
                throw new BadRequestException("Full name must not be blank");
            }
            user.setFullName(dto.getFullName().trim());
        }

        if (dto.getEmail() != null) {
            user.setEmail(dto.getEmail().trim());
        }

        if (dto.getPhoneNumber() != null) {
            user.setPhoneNumber(dto.getPhoneNumber().trim());
        }

        if (dto.getRole() != null) {
            user.setRole(dto.getRole().trim());
        }

        User updated = userRepository.save(user);
        return mapToDto(updated);
    }

    public void deleteUser(Long id) {
        userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        // Cascade cleanup: delete applications by this user and their handover records
        List<AdoptionApplication> applications = adoptionApplicationRepository.findByUserId(id);
        for (AdoptionApplication application : applications) {
            List<AnimalHandoverRecord> records = animalHandoverRecordRepository.findByApplicationId(application.getId());
            for (AnimalHandoverRecord record : records) {
                animalHandoverRecordRepository.deleteById(record.getId());
            }
            adoptionApplicationRepository.deleteById(application.getId());
        }

        userRepository.deleteById(id);
    }

    private UserResponseDto mapToDto(User user) {
        return new UserResponseDto(
                user.getId(),
                user.getUsername(),
                user.getFullName(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getRole()
        );
    }
}
