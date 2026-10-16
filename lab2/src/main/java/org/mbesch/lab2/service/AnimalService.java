package org.mbesch.lab2.service;

import org.mbesch.lab2.dto.Requests.AnimalRequest;
import org.mbesch.lab2.entity.Animal;
import org.mbesch.lab2.repository.AnimalRepository;
import org.mbesch.lab2.repository.EnclosureRepository;
import java.util.List;
import org.springframework.stereotype.Service;

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

  public Animal create(AnimalRequest r) {
    if (r.name() == null || r.name().isBlank() || r.species() == null || r.species().isBlank()) {
      throw new IllegalArgumentException("name and species are required");
    }
    if (r.enclosureId() != null && enclosures.findById(r.enclosureId()).isEmpty()) {
      throw new NotFoundException("Enclosure", r.enclosureId());
    }
    int age = r.age() == null ? 0 : r.age();
    String status = r.placementStatus() == null || r.placementStatus().isBlank() ? "IN_SHELTER" : r.placementStatus();
    return repository.save(new Animal(repository.nextId(), r.name(), r.species(), age, status, r.enclosureId()));
  }

  public Animal patch(long id, AnimalRequest r) {
    Animal old = find(id);
    String name = r.name() == null ? old.name() : r.name();
    String species = r.species() == null ? old.species() : r.species();
    Integer age = r.age() == null ? old.age() : r.age();
    String status = r.placementStatus() == null ? old.placementStatus() : r.placementStatus();
    Long encId = r.enclosureId() == null ? old.enclosureId() : r.enclosureId();

    if (encId != null && enclosures.findById(encId).isEmpty()) {
      throw new NotFoundException("Enclosure", encId);
    }
    return repository.save(new Animal(id, name, species, age, status, encId));
  }

  public void delete(long id) {
    find(id);
    cleanup.deleteAnimal(id);
  }
}
