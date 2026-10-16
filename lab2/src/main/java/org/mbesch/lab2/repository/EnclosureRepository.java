package org.mbesch.lab2.repository;

import org.mbesch.lab2.entity.Enclosure;
import org.springframework.stereotype.Repository;

@Repository
public class EnclosureRepository extends InMemoryRepository<Enclosure> {
  public EnclosureRepository() {
    super(Enclosure::id);
  }
}
