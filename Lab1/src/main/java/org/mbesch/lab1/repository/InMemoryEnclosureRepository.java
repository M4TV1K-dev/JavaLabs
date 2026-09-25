package org.mbesch.lab1.repository;

import org.mbesch.lab1.model.Enclosure;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class InMemoryEnclosureRepository implements EnclosureRepository {
    private final Map<Long, Enclosure> storage = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public List<Enclosure> findAll() {
        return storage.values().stream()
                .sorted(Comparator.comparing(Enclosure::getId))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Enclosure> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public Enclosure save(Enclosure enclosure) {
        if (enclosure.getId() == null) {
            enclosure.setId(idGenerator.getAndIncrement());
        }
        storage.put(enclosure.getId(), enclosure);
        return enclosure;
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
    public void clear() {
        storage.clear();
        idGenerator.set(1);
    }
}
