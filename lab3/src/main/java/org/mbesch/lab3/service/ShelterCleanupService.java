package org.mbesch.lab3.service;

import org.mbesch.lab3.entity.Animal;
import org.mbesch.lab3.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

  @Transactional
  public void deleteUser(long id) {
    applications.findByApplicantId(id).forEach(a -> deleteApplication(a.getId()));
    users.deleteById(id);
  }

  @Transactional
  public void deleteAnimal(long id) {
    applications.findByAnimalId(id).forEach(a -> deleteApplication(a.getId()));
    animals.deleteById(id);
  }

  @Transactional
  public void deleteEnclosure(long id) {
    List<Animal> enclosureAnimals = animals.findByEnclosureId(id);
    for (Animal animal : enclosureAnimals) {
      animal.setEnclosureId(null);
      animals.save(animal);
    }
    enclosures.deleteById(id);
  }

  @Transactional
  public void deleteApplication(long id) {
    records.deleteByApplicationId(id);
    applications.deleteById(id);
  }

  @Transactional
  public void deleteRecord(long id) {
    records.deleteById(id);
  }
}
