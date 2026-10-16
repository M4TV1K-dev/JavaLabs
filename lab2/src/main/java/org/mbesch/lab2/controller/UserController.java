package org.mbesch.lab2.controller;

import org.mbesch.lab2.dto.Requests.UserRequest;
import org.mbesch.lab2.entity.User;
import org.mbesch.lab2.service.UserService;
import java.util.List;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {
  private final UserService service;

  public UserController(UserService service) {
    this.service = service;
  }

  @GetMapping
  public List<User> all() {
    return service.findAll();
  }

  @GetMapping("/{id}")
  public User one(@PathVariable long id) {
    return service.find(id);
  }

  @PostMapping
  public ResponseEntity<User> create(@RequestBody UserRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
  }

  @PatchMapping("/{id}")
  public User patch(@PathVariable long id, @RequestBody UserRequest request) {
    return service.patch(id, request);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable long id) {
    service.delete(id);
  }
}
