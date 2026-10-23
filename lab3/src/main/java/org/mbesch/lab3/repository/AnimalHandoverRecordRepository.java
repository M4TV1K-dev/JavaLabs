package org.mbesch.lab3.repository;

import org.mbesch.lab3.entity.AnimalHandoverRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AnimalHandoverRecordRepository extends JpaRepository<AnimalHandoverRecord, Long> {
    List<AnimalHandoverRecord> findByApplicationId(Long applicationId);
    Optional<AnimalHandoverRecord> findFirstByApplicationId(Long applicationId);
    void deleteByApplicationId(Long applicationId);
}
