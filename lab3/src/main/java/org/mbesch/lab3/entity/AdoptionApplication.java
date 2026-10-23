package org.mbesch.lab3.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "adoption_applications")
public class AdoptionApplication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "animal_id", nullable = false)
    private Long animalId;

    @Column(name = "applicant_id", nullable = false)
    private Long applicantId;

    @Column(nullable = false, length = 50)
    private String status;

    @Column(name = "created_at")
    private Instant createdAt;

    public AdoptionApplication() {}

    public AdoptionApplication(Long id, Long animalId, Long applicantId, String status, Instant createdAt) {
        this.id = id;
        this.animalId = animalId;
        this.applicantId = applicantId;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getAnimalId() { return animalId; }
    public void setAnimalId(Long animalId) { this.animalId = animalId; }
    public Long getApplicantId() { return applicantId; }
    public void setApplicantId(Long applicantId) { this.applicantId = applicantId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
