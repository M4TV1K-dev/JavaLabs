package org.mbesch.lab1.repository;

import org.mbesch.lab1.model.Enclosure;

import java.util.List;
import java.util.Optional;

public interface EnclosureRepository {
    List<Enclosure> findAll();
    Optional<Enclosure> findById(Long id);
    Enclosure save(Enclosure enclosure);
    boolean existsById(Long id);
    void deleteById(Long id);
    void clear();
}
