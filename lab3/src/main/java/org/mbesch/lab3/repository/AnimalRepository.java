package org.mbesch.lab3.repository;

import org.mbesch.lab3.entity.Animal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnimalRepository extends JpaRepository<Animal, Long> {
    List<Animal> findByEnclosureId(Long enclosureId);
    long countByEnclosureId(Long enclosureId);
}
