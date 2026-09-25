package org.mbesch.lab1.repository;

import org.mbesch.lab1.model.AdoptionApplication;

import java.util.List;
import java.util.Optional;

public interface AdoptionApplicationRepository {
    List<AdoptionApplication> findAll();
    Optional<AdoptionApplication> findById(Long id);
    AdoptionApplication save(AdoptionApplication application);
    boolean existsById(Long id);
    void deleteById(Long id);
    List<AdoptionApplication> findByAnimalId(Long animalId);
    List<AdoptionApplication> findByUserId(Long userId);
    void clear();
}
