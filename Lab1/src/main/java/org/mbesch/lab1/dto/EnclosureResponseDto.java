package org.mbesch.lab1.dto;

import java.util.ArrayList;
import java.util.List;

public class EnclosureResponseDto {
    private Long id;
    private String name;
    private List<String> allowedSpecies = new ArrayList<>();
    private Integer capacity;
    private List<Long> animalIds = new ArrayList<>();
    private int currentOccupancy;

    public EnclosureResponseDto() {
    }

    public EnclosureResponseDto(Long id, String name, List<String> allowedSpecies, Integer capacity, List<Long> animalIds) {
        this.id = id;
        this.name = name;
        if (allowedSpecies != null) {
            this.allowedSpecies = new ArrayList<>(allowedSpecies);
        }
        this.capacity = capacity;
        if (animalIds != null) {
            this.animalIds = new ArrayList<>(animalIds);
        }
        this.currentOccupancy = this.animalIds.size();
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
        this.currentOccupancy = this.animalIds.size();
    }

    public int getCurrentOccupancy() {
        return currentOccupancy;
    }

    public void setCurrentOccupancy(int currentOccupancy) {
        this.currentOccupancy = currentOccupancy;
    }
}
