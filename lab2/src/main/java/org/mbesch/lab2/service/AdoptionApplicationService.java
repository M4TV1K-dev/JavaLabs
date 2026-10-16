package org.mbesch.lab2.service;

import org.mbesch.lab2.dto.Requests.ApplicationRequest;
import org.mbesch.lab2.entity.AdoptionApplication;
import org.mbesch.lab2.repository.AdoptionApplicationRepository;
import org.mbesch.lab2.repository.AnimalRepository;
import org.mbesch.lab2.repository.UserRepository;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class AdoptionApplicationService {
  private final AdoptionApplicationRepository repository;
  private final AnimalRepository animals;
  private final UserRepository users;
  private final ShelterCleanupService cleanup;

  public AdoptionApplicationService(
      AdoptionApplicationRepository repository,
      AnimalRepository animals,
      UserRepository users,
      ShelterCleanupService cleanup) {
    this.repository = repository;
    this.animals = animals;
    this.users = users;
    this.cleanup = cleanup;
  }

  public List<AdoptionApplication> findAll() {
    return repository.findAll();
  }

  public AdoptionApplication find(long id) {
    return repository.findById(id).orElseThrow(() -> new NotFoundException("AdoptionApplication", id));
  }

  public AdoptionApplication create(ApplicationRequest r) {
    if (r.animalId() == null || animals.findById(r.animalId()).isEmpty()) {
      throw new NotFoundException("Animal", r.animalId() == null ? 0 : r.animalId());
    }
    if (r.applicantId() == null || users.findById(r.applicantId()).isEmpty()) {
      throw new NotFoundException("User", r.applicantId() == null ? 0 : r.applicantId());
    }
    String status = r.status() == null || r.status().isBlank() ? "SUBMITTED" : r.status();
    return repository.save(new AdoptionApplication(repository.nextId(), r.animalId(), r.applicantId(), status, Instant.now()));
  }

  public AdoptionApplication patch(long id, ApplicationRequest r) {
    AdoptionApplication old = find(id);
    Long animalId = r.animalId() == null ? old.animalId() : r.animalId();
    Long applicantId = r.applicantId() == null ? old.applicantId() : r.applicantId();
    String status = r.status() == null ? old.status() : r.status();

    if (r.animalId() != null && animals.findById(animalId).isEmpty()) {
      throw new NotFoundException("Animal", animalId);
    }
    if (r.applicantId() != null && users.findById(applicantId).isEmpty()) {
      throw new NotFoundException("User", applicantId);
    }
    return repository.save(new AdoptionApplication(id, animalId, applicantId, status, old.createdAt()));
  }

  public void delete(long id) {
    find(id);
    cleanup.deleteApplication(id);
  }
}
