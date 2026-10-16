package org.mbesch.lab2.entity;

import java.util.List;

public record Enclosure(Long id, String name, List<String> allowedAnimalTypes, Integer capacity) {}
