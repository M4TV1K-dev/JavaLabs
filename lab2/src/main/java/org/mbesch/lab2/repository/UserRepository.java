package org.mbesch.lab2.repository;

import org.mbesch.lab2.entity.User;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepository extends InMemoryRepository<User> {
  public UserRepository() {
    super(User::id);
  }
}
