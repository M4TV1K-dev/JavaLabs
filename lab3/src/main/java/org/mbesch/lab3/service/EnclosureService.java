package org.mbesch.lab3.service;

import org.mbesch.lab3.dto.Requests.EnclosureRequest;
import org.mbesch.lab3.entity.Enclosure;
import org.mbesch.lab3.repository.EnclosureRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EnclosureService {
  private final EnclosureRepository repository;
  private final ShelterCleanupService cleanup;

  public EnclosureService(EnclosureRepository repository, ShelterCleanupService cleanup) {
    this.repository = repository;
    this.cleanup = cleanup;
  }

  public List<Enclosure> findAll() {
    return repository.findAll();
  }

  public Enclosure find(long id) {
    return repository.findById(id).orElseThrow(() -> new NotFoundException("Enclosure", id));
  }

  @Transactional
  public Enclosure create(EnclosureRequest r) {
    if (r.name() == null || r.name().isBlank()) {
      throw new IllegalArgumentException("name is required");
    }
    int cap = r.capacity() == null || r.capacity() <= 0 ? 1 : r.capacity();
    String types = r.allowedAnimalTypes() == null ? "" : r.allowedAnimalTypes();
    Enclosure enclosure = new Enclosure(null, r.name(), types, cap);
    return repository.save(enclosure);
  }

  @Transactional
  public Enclosure patch(long id, EnclosureRequest r) {
    Enclosure old = find(id);
    if (r.name() != null) old.setName(r.name());
    if (r.allowedAnimalTypes() != null) old.setAllowedAnimalTypes(r.allowedAnimalTypes());
    if (r.capacity() != null) {
      if (r.capacity() <= 0) throw new IllegalArgumentException("capacity must be positive");
      old.setCapacity(r.capacity());
    }
    return repository.save(old);
  }

  @Transactional
  public void delete(long id) {
    find(id);
    cleanup.deleteEnclosure(id);
  }
}
