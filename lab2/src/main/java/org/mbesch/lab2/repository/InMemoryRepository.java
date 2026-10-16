package org.mbesch.lab2.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

public class InMemoryRepository<T> {
  private final ConcurrentHashMap<Long, T> storage = new ConcurrentHashMap<>();
  private final AtomicLong sequence = new AtomicLong();
  private final Function<T, Long> idExtractor;

  public InMemoryRepository(Function<T, Long> idExtractor) {
    this.idExtractor = idExtractor;
  }

  public List<T> findAll() {
    return new ArrayList<>(storage.values());
  }

  public Optional<T> findById(long id) {
    return Optional.ofNullable(storage.get(id));
  }

  public long nextId() {
    return sequence.incrementAndGet();
  }

  public T save(T item) {
    storage.put(idExtractor.apply(item), item);
    return item;
  }

  public void deleteById(long id) {
    storage.remove(id);
  }
}
