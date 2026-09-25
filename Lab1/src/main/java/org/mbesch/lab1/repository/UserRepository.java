package org.mbesch.lab1.repository;

import org.mbesch.lab1.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    List<User> findAll();
    Optional<User> findById(Long id);
    User save(User user);
    boolean existsById(Long id);
    void deleteById(Long id);
    void clear();
}
