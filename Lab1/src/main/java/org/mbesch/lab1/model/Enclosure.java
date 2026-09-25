package org.mbesch.lab1.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Enclosure {
    private Long id;
    private String name;
    private List<String> allowedSpecies = new ArrayList<>();
    private Integer capacity;
    private List<Long> animalIds = new ArrayList<>();

    public Enclosure() {
    }

    public Enclosure(Long id, String name, List<String> allowedSpecies, Integer capacity, List<Long> animalIds) {
        this.id = id;
        this.name = name;
        if (allowedSpecies != null) {
            this.allowedSpecies = new ArrayList<>(allowedSpecies);
        }
        this.capacity = capacity;
        if (animalIds != null) {
            this.animalIds = new ArrayList<>(animalIds);
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getAllowedSpecies() {
        return allowedSpecies;
    }

    public void setAllowedSpecies(List<String> allowedSpecies) {
        this.allowedSpecies = allowedSpecies != null ? new ArrayList<>(allowedSpecies) : new ArrayList<>();
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public List<Long> getAnimalIds() {
        return animalIds;
    }

    public void setAnimalIds(List<Long> animalIds) {
        this.animalIds = animalIds != null ? new ArrayList<>(animalIds) : new ArrayList<>();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Enclosure enclosure = (Enclosure) o;
        return Objects.equals(id, enclosure.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
