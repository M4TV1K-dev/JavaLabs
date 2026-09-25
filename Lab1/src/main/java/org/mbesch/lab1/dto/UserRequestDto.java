package org.mbesch.lab1.dto;

import jakarta.validation.constraints.Email;

public class UserRequestDto {
    private String username;
    private String fullName;

    @Email(message = "Invalid email format")
    private String email;

    private String phoneNumber;
    private String role;

    public UserRequestDto() {
    }

    public UserRequestDto(String username, String fullName, String email, String phoneNumber, String role) {
        this.username = username;
        this.fullName = fullName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
