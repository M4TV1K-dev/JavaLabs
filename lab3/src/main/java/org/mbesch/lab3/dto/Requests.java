package org.mbesch.lab3.dto;

public final class Requests {
  private Requests() {}

  public record UserRequest(String name, String email, String phone, String role) {}

  public record EnclosureRequest(String name, String allowedAnimalTypes, Integer capacity) {}

  public record AnimalRequest(String name, String species, Integer age, String placementStatus, Long enclosureId) {}

  public record ApplicationRequest(Long animalId, Long applicantId, String status) {}

  public record HandoverRecordRequest(Long applicationId, String status) {}
}
