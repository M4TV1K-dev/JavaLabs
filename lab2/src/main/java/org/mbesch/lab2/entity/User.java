package org.mbesch.lab2.entity;

import java.time.Instant;

public record User(Long id, String name, String email, String phone, String role, Instant createdAt) {}
