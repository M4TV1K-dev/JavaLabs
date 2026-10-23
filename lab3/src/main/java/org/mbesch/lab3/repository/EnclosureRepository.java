package org.mbesch.lab3.repository;

import org.mbesch.lab3.entity.Enclosure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EnclosureRepository extends JpaRepository<Enclosure, Long> {
}
