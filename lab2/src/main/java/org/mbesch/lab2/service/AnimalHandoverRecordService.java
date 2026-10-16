package org.mbesch.lab2.service;

import org.mbesch.lab2.dto.Requests.HandoverRecordRequest;
import org.mbesch.lab2.entity.AnimalHandoverRecord;
import org.mbesch.lab2.repository.AdoptionApplicationRepository;
import org.mbesch.lab2.repository.AnimalHandoverRecordRepository;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Service;

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

  public AnimalHandoverRecord create(HandoverRecordRequest r) {
    if (r.applicationId() == null || applications.findById(r.applicationId()).isEmpty()) {
      throw new NotFoundException("AdoptionApplication", r.applicationId() == null ? 0 : r.applicationId());
    }
    String status = r.status() == null || r.status().isBlank() ? "PREPARED" : r.status();
    return repository.save(new AnimalHandoverRecord(repository.nextId(), r.applicationId(), Instant.now(), null, status));
  }

  public AnimalHandoverRecord patch(long id, HandoverRecordRequest r) {
    AnimalHandoverRecord old = find(id);
    Long appId = r.applicationId() == null ? old.applicationId() : r.applicationId();
    if (r.applicationId() != null && applications.findById(appId).isEmpty()) {
      throw new NotFoundException("AdoptionApplication", appId);
    }
    String status = r.status() == null ? old.status() : r.status();
    Instant confirmationDate = old.confirmationDate();
    if ("CONFIRMED".equalsIgnoreCase(status) && confirmationDate == null) {
      confirmationDate = Instant.now();
    }
    return repository.save(new AnimalHandoverRecord(id, appId, old.preparationDate(), confirmationDate, status));
  }

  public void delete(long id) {
    find(id);
    cleanup.deleteRecord(id);
  }
}
