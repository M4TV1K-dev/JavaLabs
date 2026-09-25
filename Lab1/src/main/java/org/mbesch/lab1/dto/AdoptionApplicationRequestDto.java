package org.mbesch.lab1.dto;

public class AdoptionApplicationRequestDto {
    private Long animalId;
    private Long userId;
    private String status;

    public AdoptionApplicationRequestDto() {
    }

    public AdoptionApplicationRequestDto(Long animalId, Long userId, String status) {
        this.animalId = animalId;
        this.userId = userId;
        this.status = status;
    }

    public Long getAnimalId() {
        return animalId;
    }

    public void setAnimalId(Long animalId) {
        this.animalId = animalId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
