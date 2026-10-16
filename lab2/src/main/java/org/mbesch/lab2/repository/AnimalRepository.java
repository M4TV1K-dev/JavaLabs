package org.mbesch.lab2.repository;

import org.mbesch.lab2.entity.Animal;
import org.springframework.stereotype.Repository;

@Repository
public class AnimalRepository extends InMemoryRepository<Animal> {
  public AnimalRepository() {
    super(Animal::id);
  }
}
