package org.mbesch.lab3.service;

import org.mbesch.lab3.dto.Requests.UserRequest;
import org.mbesch.lab3.entity.User;
import org.mbesch.lab3.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

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

  @Transactional
  public User create(UserRequest r) {
    validate(r.name(), r.email());
    String role = r.role() == null || r.role().isBlank() ? "ADOPTER" : r.role();
    User user = new User(null, r.name(), r.email(), r.phone(), role, Instant.now());
    return repository.save(user);
  }

  @Transactional
  public User patch(long id, UserRequest r) {
    User old = find(id);
    if (r.name() != null) old.setName(r.name());
    if (r.email() != null) old.setEmail(r.email());
    if (r.phone() != null) old.setPhone(r.phone());
    if (r.role() != null) old.setRole(r.role());
    validate(old.getName(), old.getEmail());
    return repository.save(old);
  }

  @Transactional
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
