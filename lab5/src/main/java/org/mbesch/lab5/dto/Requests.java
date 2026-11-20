package org.mbesch.lab5.dto;

public final class Requests {
  private Requests() {}

  public record UserRequest(String name, String email, String phone, String role, String password, String status) {}

  public record RegisterRequest(String name, String email, String phone, String password, String deviceId) {}

  public record LoginRequest(String username, String email, String password, String deviceId) {
    public String resolvedUsername() {
      if (username != null && !username.isBlank()) {
        return username;
      }
      return email;
    }
  }

  public record RefreshRequest(String refreshToken) {}

  public record TokenPairResponse(String accessToken, String refreshToken) {}

  public record EnclosureRequest(String name, String allowedAnimalTypes, Integer capacity) {}

  public record AnimalRequest(String name, String species, Integer age, String placementStatus, Long enclosureId) {}

  public record ApplicationRequest(Long animalId, Long applicantId, String status) {}

  public record HandoverRecordRequest(Long applicationId, String status) {}
}
