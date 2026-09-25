package org.mbesch.lab1.dto;

import java.time.Instant;

public class AdoptionApplicationResponseDto {
    private Long id;
    private Long animalId;
    private Long userId;
    private String status;
    private Instant createdAt;

    public AdoptionApplicationResponseDto() {
    }

    public AdoptionApplicationResponseDto(Long id, Long animalId, Long userId, String status, Instant createdAt) {
        this.id = id;
        this.animalId = animalId;
        this.userId = userId;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
