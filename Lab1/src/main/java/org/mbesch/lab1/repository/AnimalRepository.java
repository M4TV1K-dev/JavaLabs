package org.mbesch.lab1.repository;

import org.mbesch.lab1.model.Animal;

import java.util.List;
import java.util.Optional;

public interface AnimalRepository {
    List<Animal> findAll();
    Optional<Animal> findById(Long id);
    Animal save(Animal animal);
    boolean existsById(Long id);
    void deleteById(Long id);
    List<Animal> findByEnclosureId(Long enclosureId);
    void clear();
}
