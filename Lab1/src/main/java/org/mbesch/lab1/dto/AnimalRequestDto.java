package org.mbesch.lab1.dto;

import jakarta.validation.constraints.Min;

public class AnimalRequestDto {
    private String name;
    private String species;

    @Min(value = 0, message = "Age must be non-negative")
    private Integer age;

    private String placementStatus;
    private Long enclosureId;

    public AnimalRequestDto() {
    }

    public AnimalRequestDto(String name, String species, Integer age, String placementStatus, Long enclosureId) {
        this.name = name;
        this.species = species;
        this.age = age;
        this.placementStatus = placementStatus;
        this.enclosureId = enclosureId;
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
}
