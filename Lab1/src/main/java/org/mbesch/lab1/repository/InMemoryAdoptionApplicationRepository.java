package org.mbesch.lab1.repository;

import org.mbesch.lab1.model.AdoptionApplication;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class InMemoryAdoptionApplicationRepository implements AdoptionApplicationRepository {
    private final Map<Long, AdoptionApplication> storage = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public List<AdoptionApplication> findAll() {
        return storage.values().stream()
                .sorted(Comparator.comparing(AdoptionApplication::getId))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<AdoptionApplication> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public AdoptionApplication save(AdoptionApplication application) {
        if (application.getId() == null) {
            application.setId(idGenerator.getAndIncrement());
        }
        storage.put(application.getId(), application);
        return application;
    }

    @Override
    public boolean existsById(Long id) {
        return storage.containsKey(id);
    }

    @Override
    public void deleteById(Long id) {
        storage.remove(id);
    }

    @Override
    public List<AdoptionApplication> findByAnimalId(Long animalId) {
        if (animalId == null) {
            return new ArrayList<>();
        }
        return storage.values().stream()
                .filter(app -> Objects.equals(animalId, app.getAnimalId()))
                .sorted(Comparator.comparing(AdoptionApplication::getId))
                .collect(Collectors.toList());
    }

    @Override
    public List<AdoptionApplication> findByUserId(Long userId) {
        if (userId == null) {
            return new ArrayList<>();
        }
        return storage.values().stream()
                .filter(app -> Objects.equals(userId, app.getUserId()))
                .sorted(Comparator.comparing(AdoptionApplication::getId))
                .collect(Collectors.toList());
    }

    @Override
    public void clear() {
        storage.clear();
        idGenerator.set(1);
    }
}
