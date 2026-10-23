package org.mbesch.lab3.controller;

import org.mbesch.lab3.dto.Requests.ApplicationRequest;
import org.mbesch.lab3.entity.AdoptionApplication;
import org.mbesch.lab3.service.AdoptionApplicationService;
import java.util.List;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/adoption-applications")
public class AdoptionApplicationController {
  private final AdoptionApplicationService service;

  public AdoptionApplicationController(AdoptionApplicationService service) {
    this.service = service;
  }

  @GetMapping
  public List<AdoptionApplication> all() {
    return service.findAll();
  }

  @GetMapping("/{id}")
  public AdoptionApplication one(@PathVariable long id) {
    return service.find(id);
  }

  @PostMapping
  public ResponseEntity<AdoptionApplication> create(@RequestBody ApplicationRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
  }

  @PatchMapping("/{id}")
  public AdoptionApplication patch(@PathVariable long id, @RequestBody ApplicationRequest request) {
    return service.patch(id, request);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable long id) {
    service.delete(id);
  }
}
