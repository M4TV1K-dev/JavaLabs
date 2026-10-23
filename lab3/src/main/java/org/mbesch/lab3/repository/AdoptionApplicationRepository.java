package org.mbesch.lab3.repository;

import org.mbesch.lab3.entity.AdoptionApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface AdoptionApplicationRepository extends JpaRepository<AdoptionApplication, Long> {
    List<AdoptionApplication> findByAnimalId(Long animalId);
    List<AdoptionApplication> findByApplicantId(Long applicantId);
    boolean existsByAnimalIdAndApplicantIdAndStatusIn(Long animalId, Long applicantId, Collection<String> statuses);
    void deleteByAnimalId(Long animalId);
    void deleteByApplicantId(Long applicantId);
}
