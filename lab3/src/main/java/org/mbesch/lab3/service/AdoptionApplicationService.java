package org.mbesch.lab3.service;

import org.mbesch.lab3.dto.Requests.ApplicationRequest;
import org.mbesch.lab3.entity.AdoptionApplication;
import org.mbesch.lab3.repository.AdoptionApplicationRepository;
import org.mbesch.lab3.repository.AnimalRepository;
import org.mbesch.lab3.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

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

  @Transactional
  public AdoptionApplication create(ApplicationRequest r) {
    if (r.animalId() == null || animals.findById(r.animalId()).isEmpty()) {
      throw new NotFoundException("Animal", r.animalId() == null ? 0 : r.animalId());
    }
    if (r.applicantId() == null || users.findById(r.applicantId()).isEmpty()) {
      throw new NotFoundException("User", r.applicantId() == null ? 0 : r.applicantId());
    }
    String status = r.status() == null || r.status().isBlank() ? "SUBMITTED" : r.status();
    AdoptionApplication app = new AdoptionApplication(null, r.animalId(), r.applicantId(), status, Instant.now());
    return repository.save(app);
  }

  @Transactional
  public AdoptionApplication patch(long id, ApplicationRequest r) {
    AdoptionApplication old = find(id);
    if (r.animalId() != null) {
      if (animals.findById(r.animalId()).isEmpty()) throw new NotFoundException("Animal", r.animalId());
      old.setAnimalId(r.animalId());
    }
    if (r.applicantId() != null) {
      if (users.findById(r.applicantId()).isEmpty()) throw new NotFoundException("User", r.applicantId());
      old.setApplicantId(r.applicantId());
    }
    if (r.status() != null) old.setStatus(r.status());
    return repository.save(old);
  }

  @Transactional
  public void delete(long id) {
    find(id);
    cleanup.deleteApplication(id);
  }
}
