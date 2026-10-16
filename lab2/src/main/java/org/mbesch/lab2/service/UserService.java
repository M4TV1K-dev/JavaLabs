package org.mbesch.lab2.service;

import org.mbesch.lab2.dto.Requests.UserRequest;
import org.mbesch.lab2.entity.User;
import org.mbesch.lab2.repository.UserRepository;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class UserService {
  private final UserRepository repository;
  private final ShelterCleanupService cleanup;

  public UserService(UserRepository repository, ShelterCleanupService cleanup) {
    this.repository = repository;
    this.cleanup = cleanup;
  }

  public List<User> findAll() {
    return repository.findAll();
  }

  public User find(long id) {
    return repository.findById(id).orElseThrow(() -> new NotFoundException("User", id));
  }

  public User create(UserRequest r) {
    validate(r.name(), r.email());
    String role = r.role() == null || r.role().isBlank() ? "ADOPTER" : r.role();
    return repository.save(new User(repository.nextId(), r.name(), r.email(), r.phone(), role, Instant.now()));
  }

  public User patch(long id, UserRequest r) {
    User old = find(id);
    String name = r.name() == null ? old.name() : r.name();
    String email = r.email() == null ? old.email() : r.email();
    String phone = r.phone() == null ? old.phone() : r.phone();
    String role = r.role() == null ? old.role() : r.role();
    validate(name, email);
    return repository.save(new User(id, name, email, phone, role, old.createdAt()));
  }

  public void delete(long id) {
    find(id);
    cleanup.deleteUser(id);
  }

  private void validate(String name, String email) {
    if (name == null || name.isBlank() || email == null || email.isBlank()) {
      throw new IllegalArgumentException("name and email are required");
    }
  }
}
