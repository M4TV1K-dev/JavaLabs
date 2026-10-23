package org.mbesch.lab3.service;

public class NotFoundException extends RuntimeException {
  public NotFoundException(String resource, long id) {
    super(resource + " with id=" + id + " was not found");
  }
}
