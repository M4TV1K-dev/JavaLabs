package org.mbesch.lab1.model;

import java.util.Objects;

public class Animal {
    private Long id;
    private String name;
    private String species;
    private Integer age;
    private String placementStatus;
    private Long enclosureId;

    public Animal() {
    }

    public Animal(Long id, String name, String species, Integer age, String placementStatus, Long enclosureId) {
        this.id = id;
        this.name = name;
        this.species = species;
        this.age = age;
        this.placementStatus = placementStatus;
        this.enclosureId = enclosureId;
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

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getPlacementStatus() {
        return placementStatus;
    }

    public void setPlacementStatus(String placementStatus) {
        this.placementStatus = placementStatus;
    }

    public Long getEnclosureId() {
        return enclosureId;
    }

    public void setEnclosureId(Long enclosureId) {
        this.enclosureId = enclosureId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Animal animal = (Animal) o;
        return Objects.equals(id, animal.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
