package org.mbesch.lab3.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "enclosures")
public class Enclosure {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "allowed_animal_types", nullable = false, length = 500)
    private String allowedAnimalTypes;

    @Column(nullable = false)
    private Integer capacity;

    public Enclosure() {}

    public Enclosure(Long id, String name, String allowedAnimalTypes, Integer capacity) {
        this.id = id;
        this.name = name;
        this.allowedAnimalTypes = allowedAnimalTypes;
        this.capacity = capacity;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getAllowedAnimalTypes() { return allowedAnimalTypes; }
    public void setAllowedAnimalTypes(String allowedAnimalTypes) { this.allowedAnimalTypes = allowedAnimalTypes; }
    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }
}
