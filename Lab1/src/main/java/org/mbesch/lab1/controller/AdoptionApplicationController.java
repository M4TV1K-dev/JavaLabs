package org.mbesch.lab1.controller;

import jakarta.validation.Valid;
import org.mbesch.lab1.dto.AdoptionApplicationRequestDto;
import org.mbesch.lab1.dto.AdoptionApplicationResponseDto;
import org.mbesch.lab1.service.AdoptionApplicationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/adoption-applications")
public class AdoptionApplicationController {
    private final AdoptionApplicationService adoptionApplicationService;

    public AdoptionApplicationController(AdoptionApplicationService adoptionApplicationService) {
        this.adoptionApplicationService = adoptionApplicationService;
    }

    @GetMapping
    public ResponseEntity<List<AdoptionApplicationResponseDto>> getAllApplications() {
        return ResponseEntity.ok(adoptionApplicationService.getAllApplications());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdoptionApplicationResponseDto> getApplicationById(@PathVariable Long id) {
        return ResponseEntity.ok(adoptionApplicationService.getApplicationById(id));
    }

    @PostMapping
    public ResponseEntity<AdoptionApplicationResponseDto> createApplication(@Valid @RequestBody AdoptionApplicationRequestDto dto) {
        AdoptionApplicationResponseDto created = adoptionApplicationService.createApplication(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<AdoptionApplicationResponseDto> updateApplication(@PathVariable Long id, @RequestBody AdoptionApplicationRequestDto dto) {
        return ResponseEntity.ok(adoptionApplicationService.updateApplication(id, dto));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteApplication(@PathVariable Long id) {
        adoptionApplicationService.deleteApplication(id);
        return ResponseEntity.noContent().build();
    }
}
