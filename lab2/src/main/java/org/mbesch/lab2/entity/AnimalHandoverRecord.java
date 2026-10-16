package org.mbesch.lab2.entity;

import java.time.Instant;

public record AnimalHandoverRecord(Long id, Long applicationId, Instant preparationDate, Instant confirmationDate, String status) {}
