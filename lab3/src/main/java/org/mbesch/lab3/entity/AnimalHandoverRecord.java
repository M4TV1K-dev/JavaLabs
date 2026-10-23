package org.mbesch.lab3.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "animal_handover_records")
public class AnimalHandoverRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "application_id", nullable = false)
    private Long applicationId;

    @Column(name = "preparation_date")
    private Instant preparationDate;

    @Column(name = "confirmation_date")
    private Instant confirmationDate;

    @Column(nullable = false, length = 50)
    private String status;

    public AnimalHandoverRecord() {}

    public AnimalHandoverRecord(Long id, Long applicationId, Instant preparationDate, Instant confirmationDate, String status) {
        this.id = id;
        this.applicationId = applicationId;
        this.preparationDate = preparationDate;
        this.confirmationDate = confirmationDate;
        this.status = status;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getApplicationId() { return applicationId; }
    public void setApplicationId(Long applicationId) { this.applicationId = applicationId; }
    public Instant getPreparationDate() { return preparationDate; }
    public void setPreparationDate(Instant preparationDate) { this.preparationDate = preparationDate; }
    public Instant getConfirmationDate() { return confirmationDate; }
    public void setConfirmationDate(Instant confirmationDate) { this.confirmationDate = confirmationDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
