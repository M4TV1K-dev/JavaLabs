package org.mbesch.lab3.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "animals")
public class Animal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, length = 100)
    private String species;

    @Column(nullable = false)
    private Integer age;

    @Column(name = "placement_status", nullable = false, length = 50)
    private String placementStatus;

    @Column(name = "enclosure_id")
    private Long enclosureId;

    public Animal() {}

    public Animal(Long id, String name, String species, Integer age, String placementStatus, Long enclosureId) {
        this.id = id;
        this.name = name;
        this.species = species;
        this.age = age;
        this.placementStatus = placementStatus;
        this.enclosureId = enclosureId;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getSpecies() { return species; }
    public void setSpecies(String species) { this.species = species; }
    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }
    public String getPlacementStatus() { return placementStatus; }
    public void setPlacementStatus(String placementStatus) { this.placementStatus = placementStatus; }
    public Long getEnclosureId() { return enclosureId; }
    public void setEnclosureId(Long enclosureId) { this.enclosureId = enclosureId; }
}
