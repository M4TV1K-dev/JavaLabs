package org.mbesch.lab1.controller;

import jakarta.validation.Valid;
import org.mbesch.lab1.dto.EnclosureRequestDto;
import org.mbesch.lab1.dto.EnclosureResponseDto;
import org.mbesch.lab1.service.EnclosureService;
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
@RequestMapping("/api/enclosures")
public class EnclosureController {
    private final EnclosureService enclosureService;

    public EnclosureController(EnclosureService enclosureService) {
        this.enclosureService = enclosureService;
    }

    @GetMapping
    public ResponseEntity<List<EnclosureResponseDto>> getAllEnclosures() {
        return ResponseEntity.ok(enclosureService.getAllEnclosures());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EnclosureResponseDto> getEnclosureById(@PathVariable Long id) {
        return ResponseEntity.ok(enclosureService.getEnclosureById(id));
    }

    @PostMapping
    public ResponseEntity<EnclosureResponseDto> createEnclosure(@Valid @RequestBody EnclosureRequestDto dto) {
        EnclosureResponseDto created = enclosureService.createEnclosure(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<EnclosureResponseDto> updateEnclosure(@PathVariable Long id, @RequestBody EnclosureRequestDto dto) {
        return ResponseEntity.ok(enclosureService.updateEnclosure(id, dto));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteEnclosure(@PathVariable Long id) {
        enclosureService.deleteEnclosure(id);
        return ResponseEntity.noContent().build();
    }
}
