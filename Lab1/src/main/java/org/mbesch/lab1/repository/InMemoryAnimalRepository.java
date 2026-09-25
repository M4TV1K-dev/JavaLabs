package org.mbesch.lab1.repository;

import org.mbesch.lab1.model.Animal;
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
public class InMemoryAnimalRepository implements AnimalRepository {
    private final Map<Long, Animal> storage = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public List<Animal> findAll() {
        return storage.values().stream()
                .sorted(Comparator.comparing(Animal::getId))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Animal> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public Animal save(Animal animal) {
        if (animal.getId() == null) {
            animal.setId(idGenerator.getAndIncrement());
        }
        storage.put(animal.getId(), animal);
        return animal;
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
    public List<Animal> findByEnclosureId(Long enclosureId) {
        if (enclosureId == null) {
            return new ArrayList<>();
        }
        return storage.values().stream()
                .filter(a -> Objects.equals(enclosureId, a.getEnclosureId()))
                .sorted(Comparator.comparing(Animal::getId))
                .collect(Collectors.toList());
    }

    @Override
    public void clear() {
        storage.clear();
        idGenerator.set(1);
    }
}
