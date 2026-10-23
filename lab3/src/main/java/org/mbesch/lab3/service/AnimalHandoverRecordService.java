package org.mbesch.lab3.service;

import org.mbesch.lab3.dto.Requests.HandoverRecordRequest;
import org.mbesch.lab3.entity.AnimalHandoverRecord;
import org.mbesch.lab3.repository.AdoptionApplicationRepository;
import org.mbesch.lab3.repository.AnimalHandoverRecordRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class AnimalHandoverRecordService {
  private final AnimalHandoverRecordRepository repository;
  private final AdoptionApplicationRepository applications;
  private final ShelterCleanupService cleanup;

  public AnimalHandoverRecordService(
      AnimalHandoverRecordRepository repository,
      AdoptionApplicationRepository applications,
      ShelterCleanupService cleanup) {
    this.repository = repository;
    this.applications = applications;
    this.cleanup = cleanup;
  }

  public List<AnimalHandoverRecord> findAll() {
    return repository.findAll();
  }

  public AnimalHandoverRecord find(long id) {
    return repository.findById(id).orElseThrow(() -> new NotFoundException("AnimalHandoverRecord", id));
  }

  @Transactional
  public AnimalHandoverRecord create(HandoverRecordRequest r) {
    if (r.applicationId() == null || applications.findById(r.applicationId()).isEmpty()) {
      throw new NotFoundException("AdoptionApplication", r.applicationId() == null ? 0 : r.applicationId());
    }
    String status = r.status() == null || r.status().isBlank() ? "PREPARED" : r.status();
    AnimalHandoverRecord record = new AnimalHandoverRecord(null, r.applicationId(), Instant.now(), null, status);
    return repository.save(record);
  }

  @Transactional
  public AnimalHandoverRecord patch(long id, HandoverRecordRequest r) {
    AnimalHandoverRecord old = find(id);
    if (r.applicationId() != null) {
      if (applications.findById(r.applicationId()).isEmpty()) throw new NotFoundException("AdoptionApplication", r.applicationId());
      old.setApplicationId(r.applicationId());
    }
    if (r.status() != null) {
      old.setStatus(r.status());
      if ("CONFIRMED".equalsIgnoreCase(r.status()) && old.getConfirmationDate() == null) {
        old.setConfirmationDate(Instant.now());
      }
    }
    return repository.save(old);
  }

  @Transactional
  public void delete(long id) {
    find(id);
    cleanup.deleteRecord(id);
  }
}
