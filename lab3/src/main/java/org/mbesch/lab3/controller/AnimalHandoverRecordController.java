package org.mbesch.lab3.controller;

import org.mbesch.lab3.dto.Requests.HandoverRecordRequest;
import org.mbesch.lab3.entity.AnimalHandoverRecord;
import org.mbesch.lab3.service.AnimalHandoverRecordService;
import java.util.List;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/animal-handover-records")
public class AnimalHandoverRecordController {
  private final AnimalHandoverRecordService service;

  public AnimalHandoverRecordController(AnimalHandoverRecordService service) {
    this.service = service;
  }

  @GetMapping
  public List<AnimalHandoverRecord> all() {
    return service.findAll();
  }

  @GetMapping("/{id}")
  public AnimalHandoverRecord one(@PathVariable long id) {
    return service.find(id);
  }

  @PostMapping
  public ResponseEntity<AnimalHandoverRecord> create(@RequestBody HandoverRecordRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
  }

  @PatchMapping("/{id}")
  public AnimalHandoverRecord patch(@PathVariable long id, @RequestBody HandoverRecordRequest request) {
    return service.patch(id, request);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable long id) {
    service.delete(id);
  }
}
