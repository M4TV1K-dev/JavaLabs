package org.mbesch.lab4.repository;

import org.mbesch.lab4.entity.Enclosure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EnclosureRepository extends JpaRepository<Enclosure, Long> {
}
