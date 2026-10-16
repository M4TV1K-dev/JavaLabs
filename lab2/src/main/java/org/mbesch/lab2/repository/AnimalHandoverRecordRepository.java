package org.mbesch.lab2.repository;

import org.mbesch.lab2.entity.AnimalHandoverRecord;
import org.springframework.stereotype.Repository;

@Repository
public class AnimalHandoverRecordRepository extends InMemoryRepository<AnimalHandoverRecord> {
  public AnimalHandoverRecordRepository() {
    super(AnimalHandoverRecord::id);
  }
}
