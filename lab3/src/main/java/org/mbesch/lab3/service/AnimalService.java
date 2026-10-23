package org.mbesch.lab3.service;

import org.mbesch.lab3.dto.Requests.AnimalRequest;
import org.mbesch.lab3.entity.Animal;
import org.mbesch.lab3.repository.AnimalRepository;
import org.mbesch.lab3.repository.EnclosureRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AnimalService {
  private final AnimalRepository repository;
  private final EnclosureRepository enclosures;
  private final ShelterCleanupService cleanup;

  public AnimalService(AnimalRepository repository, EnclosureRepository enclosures, ShelterCleanupService cleanup) {
    this.repository = repository;
    this.enclosures = enclosures;
    this.cleanup = cleanup;
  }

  public List<Animal> findAll() {
    return repository.findAll();
  }

  public Animal find(long id) {
    return repository.findById(id).orElseThrow(() -> new NotFoundException("Animal", id));
  }

  @Transactional
  public Animal create(AnimalRequest r) {
    if (r.name() == null || r.name().isBlank() || r.species() == null || r.species().isBlank()) {
      throw new IllegalArgumentException("name and species are required");
    }
    if (r.enclosureId() != null && enclosures.findById(r.enclosureId()).isEmpty()) {
      throw new NotFoundException("Enclosure", r.enclosureId());
    }
    int age = r.age() == null ? 0 : r.age();
    if (age < 0) throw new IllegalArgumentException("age must be non-negative");
    String status = r.placementStatus() == null || r.placementStatus().isBlank() ? "IN_SHELTER" : r.placementStatus();
    Animal animal = new Animal(null, r.name(), r.species(), age, status, r.enclosureId());
    return repository.save(animal);
  }

  @Transactional
  public Animal patch(long id, AnimalRequest r) {
    Animal old = find(id);
    if (r.name() != null) old.setName(r.name());
    if (r.species() != null) old.setSpecies(r.species());
    if (r.age() != null) {
      if (r.age() < 0) throw new IllegalArgumentException("age must be non-negative");
      old.setAge(r.age());
    }
    if (r.placementStatus() != null) old.setPlacementStatus(r.placementStatus());
    if (r.enclosureId() != null) {
      if (enclosures.findById(r.enclosureId()).isEmpty()) {
        throw new NotFoundException("Enclosure", r.enclosureId());
      }
      old.setEnclosureId(r.enclosureId());
    }
    return repository.save(old);
  }

  @Transactional
  public void delete(long id) {
    find(id);
    cleanup.deleteAnimal(id);
  }
}
