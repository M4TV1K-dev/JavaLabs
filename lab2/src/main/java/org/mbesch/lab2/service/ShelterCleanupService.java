package org.mbesch.lab2.service;

import org.mbesch.lab2.entity.Animal;
import org.mbesch.lab2.repository.*;
import org.springframework.stereotype.Service;

@Service
public class ShelterCleanupService {
  private final UserRepository users;
  private final EnclosureRepository enclosures;
  private final AnimalRepository animals;
  private final AdoptionApplicationRepository applications;
  private final AnimalHandoverRecordRepository records;

  public ShelterCleanupService(
      UserRepository users,
      EnclosureRepository enclosures,
      AnimalRepository animals,
      AdoptionApplicationRepository applications,
      AnimalHandoverRecordRepository records) {
    this.users = users;
    this.enclosures = enclosures;
    this.animals = animals;
    this.applications = applications;
    this.records = records;
  }

  public void deleteUser(long id) {
    applications.findAll().stream()
        .filter(a -> a.applicantId().equals(id))
        .map(a -> a.id())
        .toList()
        .forEach(this::deleteApplication);
    users.deleteById(id);
  }

  public void deleteAnimal(long id) {
    applications.findAll().stream()
        .filter(a -> a.animalId().equals(id))
        .map(a -> a.id())
        .toList()
        .forEach(this::deleteApplication);
    animals.deleteById(id);
  }

  public void deleteEnclosure(long id) {
    animals.findAll().stream()
        .filter(a -> a.enclosureId() != null && a.enclosureId().equals(id))
        .forEach(a -> animals.save(new Animal(a.id(), a.name(), a.species(), a.age(), a.placementStatus(), null)));
    enclosures.deleteById(id);
  }

  public void deleteApplication(long id) {
    records.findAll().stream()
        .filter(r -> r.applicationId().equals(id))
        .map(r -> r.id())
        .toList()
        .forEach(records::deleteById);
    applications.deleteById(id);
  }

  public void deleteRecord(long id) {
    records.deleteById(id);
  }
}
