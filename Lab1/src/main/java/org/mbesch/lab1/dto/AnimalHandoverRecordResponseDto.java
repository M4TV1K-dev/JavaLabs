package org.mbesch.lab1.dto;

import java.time.Instant;

public class AnimalHandoverRecordResponseDto {
    private Long id;
    private Long applicationId;
    private Instant preparationDate;
    private Instant confirmationDate;
    private String status;

    public AnimalHandoverRecordResponseDto() {
    }

    public AnimalHandoverRecordResponseDto(Long id, Long applicationId, Instant preparationDate, Instant confirmationDate, String status) {
        this.id = id;
        this.applicationId = applicationId;
        this.preparationDate = preparationDate;
        this.confirmationDate = confirmationDate;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Long applicationId) {
        this.applicationId = applicationId;
    }

    public Instant getPreparationDate() {
        return preparationDate;
    }

    public void setPreparationDate(Instant preparationDate) {
        this.preparationDate = preparationDate;
    }

    public Instant getConfirmationDate() {
        return confirmationDate;
    }

    public void setConfirmationDate(Instant confirmationDate) {
        this.confirmationDate = confirmationDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
