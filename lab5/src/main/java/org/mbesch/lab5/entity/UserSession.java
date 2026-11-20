package org.mbesch.lab5.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "user_sessions")
public class UserSession {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String username;

    @Column(name = "device_id")
    private String deviceId;

    @Column(name = "access_token", nullable = false, columnDefinition = "TEXT")
    private String accessToken;

    @Column(name = "refresh_token", nullable = false, unique = true, columnDefinition = "TEXT")
    private String refreshToken;

    @Column(name = "access_token_expiry", nullable = false)
    private Instant accessTokenExpiry;

    @Column(name = "refresh_token_expiry", nullable = false)
    private Instant refreshTokenExpiry;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SessionStatus status;

    public UserSession() {}

    public UserSession(
            UUID id,
            String username,
            String deviceId,
            String accessToken,
            String refreshToken,
            Instant accessTokenExpiry,
            Instant refreshTokenExpiry,
            SessionStatus status) {
        this.id = id;
        this.username = username;
        this.deviceId = deviceId;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.accessTokenExpiry = accessTokenExpiry;
        this.refreshTokenExpiry = refreshTokenExpiry;
        this.status = status;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }
    public String getAccessToken() { return accessToken; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }
    public String getRefreshToken() { return refreshToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
    public Instant getAccessTokenExpiry() { return accessTokenExpiry; }
    public void setAccessTokenExpiry(Instant accessTokenExpiry) { this.accessTokenExpiry = accessTokenExpiry; }
    public Instant getRefreshTokenExpiry() { return refreshTokenExpiry; }
    public void setRefreshTokenExpiry(Instant refreshTokenExpiry) { this.refreshTokenExpiry = refreshTokenExpiry; }
    public SessionStatus getStatus() { return status; }
    public void setStatus(SessionStatus status) { this.status = status; }
}
