package org.mbesch.lab1.repository;

import org.mbesch.lab1.model.AnimalHandoverRecord;

import java.util.List;
import java.util.Optional;

public interface AnimalHandoverRecordRepository {
    List<AnimalHandoverRecord> findAll();
    Optional<AnimalHandoverRecord> findById(Long id);
    AnimalHandoverRecord save(AnimalHandoverRecord record);
    boolean existsById(Long id);
    void deleteById(Long id);
    List<AnimalHandoverRecord> findByApplicationId(Long applicationId);
    void clear();
}
