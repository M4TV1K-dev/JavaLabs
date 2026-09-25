package org.mbesch.lab1.repository;

import org.mbesch.lab1.model.AnimalHandoverRecord;
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
public class InMemoryAnimalHandoverRecordRepository implements AnimalHandoverRecordRepository {
    private final Map<Long, AnimalHandoverRecord> storage = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public List<AnimalHandoverRecord> findAll() {
        return storage.values().stream()
                .sorted(Comparator.comparing(AnimalHandoverRecord::getId))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<AnimalHandoverRecord> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public AnimalHandoverRecord save(AnimalHandoverRecord record) {
        if (record.getId() == null) {
            record.setId(idGenerator.getAndIncrement());
        }
        storage.put(record.getId(), record);
        return record;
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
    public List<AnimalHandoverRecord> findByApplicationId(Long applicationId) {
        if (applicationId == null) {
            return new ArrayList<>();
        }
        return storage.values().stream()
                .filter(rec -> Objects.equals(applicationId, rec.getApplicationId()))
                .sorted(Comparator.comparing(AnimalHandoverRecord::getId))
                .collect(Collectors.toList());
    }

    @Override
    public void clear() {
        storage.clear();
        idGenerator.set(1);
    }
}
