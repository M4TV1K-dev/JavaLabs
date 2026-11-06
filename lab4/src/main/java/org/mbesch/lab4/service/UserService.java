package org.mbesch.lab4.service;

import org.mbesch.lab4.dto.Requests.RegisterRequest;
import org.mbesch.lab4.dto.Requests.UserRequest;
import org.mbesch.lab4.entity.User;
import org.mbesch.lab4.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class UserService {
  private final UserRepository repository;
  private final ShelterCleanupService cleanup;
  private final PasswordEncoder passwordEncoder;

  public UserService(UserRepository repository, ShelterCleanupService cleanup, PasswordEncoder passwordEncoder) {
    this.repository = repository;
    this.cleanup = cleanup;
    this.passwordEncoder = passwordEncoder;
  }

  public List<User> findAll() {
    return repository.findAll();
  }

  public User find(long id) {
    return repository.findById(id).orElseThrow(() -> new NotFoundException("User", id));
  }

  @Transactional
  public User register(RegisterRequest r) {
    if (r.email() == null || r.email().isBlank()) {
      throw new IllegalArgumentException("Email обязателен для регистрации");
    }
    if (repository.findByEmail(r.email()).isPresent()) {
      throw new IllegalArgumentException("Пользователь с email '" + r.email() + "' уже зарегистрирован");
    }
    validatePasswordStrength(r.password());

    String name = r.name() == null || r.name().isBlank() ? "Пользователь" : r.name();
    // Самостоятельная регистрация всегда выдает только роль ROLE_USER
    User user = new User(
            null,
            name,
            r.email(),
            r.phone(),
            "ROLE_USER",
            passwordEncoder.encode(r.password()),
            "ACTIVE",
            Instant.now()
    );
    return repository.save(user);
  }

  @Transactional
  public User create(UserRequest r) {
    validate(r.name(), r.email());
    if (repository.findByEmail(r.email()).isPresent()) {
      throw new IllegalArgumentException("Пользователь с email '" + r.email() + "' уже существует");
    }
    String rawPassword = r.password() != null && !r.password().isBlank() ? r.password() : "user123";
    validatePasswordStrength(rawPassword);

    String role = r.role() == null || r.role().isBlank() ? "ROLE_USER" : r.role();
    if (!role.startsWith("ROLE_")) {
      role = "ROLE_" + role;
    }
    String status = r.status() == null || r.status().isBlank() ? "ACTIVE" : r.status();

    User user = new User(
            null,
            r.name(),
            r.email(),
            r.phone(),
            role,
            passwordEncoder.encode(rawPassword),
            status,
            Instant.now()
    );
    return repository.save(user);
  }

  @Transactional
  public User patch(long id, UserRequest r) {
    User old = find(id);
    if (r.name() != null) old.setName(r.name());
    if (r.email() != null && !r.email().equalsIgnoreCase(old.getEmail())) {
      if (repository.findByEmail(r.email()).isPresent()) {
        throw new IllegalArgumentException("Email '" + r.email() + "' уже занят другим пользователем");
      }
      old.setEmail(r.email());
    }
    if (r.phone() != null) old.setPhone(r.phone());
    if (r.role() != null) {
      String role = r.role().startsWith("ROLE_") ? r.role() : "ROLE_" + r.role();
      old.setRole(role);
    }
    if (r.status() != null) {
      old.setStatus(r.status());
    }
    if (r.password() != null && !r.password().isBlank()) {
      validatePasswordStrength(r.password());
      old.setPassword(passwordEncoder.encode(r.password()));
    }
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

  private void validatePasswordStrength(String password) {
    if (password == null || password.length() < 6 || !password.matches(".*[A-Za-zА-Яа-я].*") || !password.matches(".*\\d.*")) {
      throw new IllegalArgumentException("Пароль должен быть длиной не менее 6 символов и содержать как минимум одну букву и одну цифру");
    }
  }
}
