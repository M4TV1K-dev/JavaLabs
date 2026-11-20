package org.mbesch.lab5.service;

import org.mbesch.lab5.dto.Requests.ApplicationRequest;
import org.mbesch.lab5.entity.AdoptionApplication;
import org.mbesch.lab5.entity.User;
import org.mbesch.lab5.repository.AdoptionApplicationRepository;
import org.mbesch.lab5.repository.AnimalRepository;
import org.mbesch.lab5.repository.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth != null && auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
      return repository.findAll();
    }
    if (auth != null) {
      User current = users.findByEmail(auth.getName()).orElse(null);
      if (current != null) {
        return repository.findByApplicantId(current.getId());
      }
    }
    return List.of();
  }

  public AdoptionApplication find(long id) {
    AdoptionApplication app = repository.findById(id).orElseThrow(() -> new NotFoundException("AdoptionApplication", id));
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth != null && !auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
      User current = users.findByEmail(auth.getName()).orElse(null);
      if (current != null && !current.getId().equals(app.getApplicantId())) {
        throw new AccessDeniedException("Доступ к чужой заявке запрещен");
      }
    }
    return app;
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
