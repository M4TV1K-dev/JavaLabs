package org.mbesch.lab2.service;

import org.mbesch.lab2.dto.Requests.EnclosureRequest;
import org.mbesch.lab2.entity.Enclosure;
import org.mbesch.lab2.repository.EnclosureRepository;
import java.util.List;
import org.springframework.stereotype.Service;

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

  public Enclosure create(EnclosureRequest r) {
    if (r.name() == null || r.name().isBlank()) {
      throw new IllegalArgumentException("name is required");
    }
    int cap = r.capacity() == null || r.capacity() <= 0 ? 1 : r.capacity();
    List<String> types = r.allowedAnimalTypes() == null ? List.of() : r.allowedAnimalTypes();
    return repository.save(new Enclosure(repository.nextId(), r.name(), types, cap));
  }

  public Enclosure patch(long id, EnclosureRequest r) {
    Enclosure old = find(id);
    String name = r.name() == null ? old.name() : r.name();
    List<String> types = r.allowedAnimalTypes() == null ? old.allowedAnimalTypes() : r.allowedAnimalTypes();
    Integer cap = r.capacity() == null ? old.capacity() : r.capacity();
    return repository.save(new Enclosure(id, name, types, cap));
  }

  public void delete(long id) {
    find(id);
    cleanup.deleteEnclosure(id);
  }
}
