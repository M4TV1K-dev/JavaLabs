package org.mbesch.lab2.controller;

import org.mbesch.lab2.dto.Requests.EnclosureRequest;
import org.mbesch.lab2.entity.Enclosure;
import org.mbesch.lab2.service.EnclosureService;
import java.util.List;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/enclosures")
public class EnclosureController {
  private final EnclosureService service;

  public EnclosureController(EnclosureService service) {
    this.service = service;
  }

  @GetMapping
  public List<Enclosure> all() {
    return service.findAll();
  }

  @GetMapping("/{id}")
  public Enclosure one(@PathVariable long id) {
    return service.find(id);
  }

  @PostMapping
  public ResponseEntity<Enclosure> create(@RequestBody EnclosureRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
  }

  @PatchMapping("/{id}")
  public Enclosure patch(@PathVariable long id, @RequestBody EnclosureRequest request) {
    return service.patch(id, request);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable long id) {
    service.delete(id);
  }
}
