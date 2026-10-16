package org.mbesch.lab2.controller;

import org.mbesch.lab2.dto.Requests.AnimalRequest;
import org.mbesch.lab2.entity.Animal;
import org.mbesch.lab2.service.AnimalService;
import java.util.List;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/animals")
public class AnimalController {
  private final AnimalService service;

  public AnimalController(AnimalService service) {
    this.service = service;
  }

  @GetMapping
  public List<Animal> all() {
    return service.findAll();
  }

  @GetMapping("/{id}")
  public Animal one(@PathVariable long id) {
    return service.find(id);
  }

  @PostMapping
  public ResponseEntity<Animal> create(@RequestBody AnimalRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
  }

  @PatchMapping("/{id}")
  public Animal patch(@PathVariable long id, @RequestBody AnimalRequest request) {
    return service.patch(id, request);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable long id) {
    service.delete(id);
  }
}
