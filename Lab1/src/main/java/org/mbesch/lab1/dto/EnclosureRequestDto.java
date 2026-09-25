package org.mbesch.lab1.dto;

import jakarta.validation.constraints.Min;
import java.util.List;

public class EnclosureRequestDto {
    private String name;
    private List<String> allowedSpecies;

    @Min(value = 1, message = "Capacity must be at least 1")
    private Integer capacity;

    public EnclosureRequestDto() {
    }

    public EnclosureRequestDto(String name, List<String> allowedSpecies, Integer capacity) {
        this.name = name;
        this.allowedSpecies = allowedSpecies;
        this.capacity = capacity;
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
        this.allowedSpecies = allowedSpecies;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }
}
