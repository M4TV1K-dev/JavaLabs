package org.mbesch.lab1.model;

import java.time.Instant;
import java.util.Objects;

public class AnimalHandoverRecord {
    private Long id;
    private Long applicationId;
    private Instant preparationDate;
    private Instant confirmationDate;
    private String status;

    public AnimalHandoverRecord() {
    }

    public AnimalHandoverRecord(Long id, Long applicationId, Instant preparationDate, Instant confirmationDate, String status) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AnimalHandoverRecord that = (AnimalHandoverRecord) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
