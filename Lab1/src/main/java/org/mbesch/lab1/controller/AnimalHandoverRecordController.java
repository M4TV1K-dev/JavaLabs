package org.mbesch.lab1.controller;

import jakarta.validation.Valid;
import org.mbesch.lab1.dto.AnimalHandoverRecordRequestDto;
import org.mbesch.lab1.dto.AnimalHandoverRecordResponseDto;
import org.mbesch.lab1.service.AnimalHandoverRecordService;
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
@RequestMapping("/api/animal-handover-records")
public class AnimalHandoverRecordController {
    private final AnimalHandoverRecordService animalHandoverRecordService;

    public AnimalHandoverRecordController(AnimalHandoverRecordService animalHandoverRecordService) {
        this.animalHandoverRecordService = animalHandoverRecordService;
    }

    @GetMapping
    public ResponseEntity<List<AnimalHandoverRecordResponseDto>> getAllRecords() {
        return ResponseEntity.ok(animalHandoverRecordService.getAllRecords());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AnimalHandoverRecordResponseDto> getRecordById(@PathVariable Long id) {
        return ResponseEntity.ok(animalHandoverRecordService.getRecordById(id));
    }

    @PostMapping
    public ResponseEntity<AnimalHandoverRecordResponseDto> createRecord(@Valid @RequestBody AnimalHandoverRecordRequestDto dto) {
        AnimalHandoverRecordResponseDto created = animalHandoverRecordService.createRecord(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<AnimalHandoverRecordResponseDto> updateRecord(@PathVariable Long id, @RequestBody AnimalHandoverRecordRequestDto dto) {
        return ResponseEntity.ok(animalHandoverRecordService.updateRecord(id, dto));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteRecord(@PathVariable Long id) {
        animalHandoverRecordService.deleteRecord(id);
        return ResponseEntity.noContent().build();
    }
}
