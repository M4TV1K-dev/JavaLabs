package org.mbesch.lab2.entity;

import java.time.Instant;

public record AdoptionApplication(Long id, Long animalId, Long applicantId, String status, Instant createdAt) {}
