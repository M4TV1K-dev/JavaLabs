package org.mbesch.lab1.service;

import org.mbesch.lab1.dto.EnclosureRequestDto;
import org.mbesch.lab1.dto.EnclosureResponseDto;
import org.mbesch.lab1.exception.BadRequestException;
import org.mbesch.lab1.exception.ResourceNotFoundException;
import org.mbesch.lab1.model.Animal;
import org.mbesch.lab1.model.Enclosure;
import org.mbesch.lab1.repository.AnimalRepository;
import org.mbesch.lab1.repository.EnclosureRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EnclosureService {
    private final EnclosureRepository enclosureRepository;
    private final AnimalRepository animalRepository;

    public EnclosureService(EnclosureRepository enclosureRepository, AnimalRepository animalRepository) {
        this.enclosureRepository = enclosureRepository;
        this.animalRepository = animalRepository;
    }

    public List<EnclosureResponseDto> getAllEnclosures() {
        return enclosureRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public EnclosureResponseDto getEnclosureById(Long id) {
        Enclosure enclosure = enclosureRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Enclosure not found with id: " + id));
        return mapToDto(enclosure);
    }

    public EnclosureResponseDto createEnclosure(EnclosureRequestDto dto) {
        if (dto.getCapacity() == null || dto.getCapacity() <= 0) {
            throw new BadRequestException("Enclosure capacity must be greater than 0");
        }

        Enclosure enclosure = new Enclosure();
        enclosure.setName(dto.getName() != null ? dto.getName().trim() : null);
        enclosure.setCapacity(dto.getCapacity());
        if (dto.getAllowedSpecies() != null) {
            enclosure.setAllowedSpecies(dto.getAllowedSpecies().stream()
                    .filter(s -> s != null && !s.isBlank())
                    .map(String::trim)
                    .collect(Collectors.toList()));
        } else {
            enclosure.setAllowedSpecies(new ArrayList<>());
        }
        enclosure.setAnimalIds(new ArrayList<>());

        Enclosure saved = enclosureRepository.save(enclosure);
        return mapToDto(saved);
    }

    public EnclosureResponseDto updateEnclosure(Long id, EnclosureRequestDto dto) {
        Enclosure enclosure = enclosureRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Enclosure not found with id: " + id));

        if (dto.getName() != null) {
            enclosure.setName(dto.getName().trim());
        }

        if (dto.getCapacity() != null) {
            if (dto.getCapacity() <= 0) {
                throw new BadRequestException("Enclosure capacity must be greater than 0");
            }
            if (dto.getCapacity() < enclosure.getAnimalIds().size()) {
                throw new BadRequestException("New capacity (" + dto.getCapacity() +
                        ") cannot be less than current occupancy (" + enclosure.getAnimalIds().size() + ")");
            }
            enclosure.setCapacity(dto.getCapacity());
        }

        if (dto.getAllowedSpecies() != null) {
            List<String> newAllowed = dto.getAllowedSpecies().stream()
                    .filter(s -> s != null && !s.isBlank())
                    .map(String::trim)
                    .collect(Collectors.toList());

            // Check if current animals match new allowed species
            List<Animal> currentAnimals = animalRepository.findByEnclosureId(id);
            for (Animal animal : currentAnimals) {
                boolean matches = newAllowed.stream().anyMatch(s -> s.equalsIgnoreCase(animal.getSpecies()));
                if (!matches && !newAllowed.isEmpty()) {
                    throw new BadRequestException("Cannot update allowed species: current animal " +
                            animal.getName() + " (" + animal.getSpecies() + ") would violate new allowed species list");
                }
            }
            enclosure.setAllowedSpecies(newAllowed);
        }

        Enclosure updated = enclosureRepository.save(enclosure);
        return mapToDto(updated);
    }

    public void deleteEnclosure(Long id) {
        Enclosure enclosure = enclosureRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Enclosure not found with id: " + id));

        // Cascade cleanup: animals in this enclosure get unassigned without broken references
        List<Animal> animals = animalRepository.findByEnclosureId(id);
        for (Animal animal : animals) {
            animal.setEnclosureId(null);
            animal.setPlacementStatus("IN_SHELTER");
            animalRepository.save(animal);
        }

        enclosureRepository.deleteById(id);
    }

    private EnclosureResponseDto mapToDto(Enclosure enclosure) {
        return new EnclosureResponseDto(
                enclosure.getId(),
                enclosure.getName(),
                enclosure.getAllowedSpecies(),
                enclosure.getCapacity(),
                enclosure.getAnimalIds()
        );
    }
}
