package org.mbesch.lab2.repository;

import org.mbesch.lab2.entity.AdoptionApplication;
import org.springframework.stereotype.Repository;

@Repository
public class AdoptionApplicationRepository extends InMemoryRepository<AdoptionApplication> {
  public AdoptionApplicationRepository() {
    super(AdoptionApplication::id);
  }
}
