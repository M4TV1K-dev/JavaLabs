package org.mbesch.lab5.service;

import io.jsonwebtoken.Claims;
import org.mbesch.lab5.dto.Requests.RefreshRequest;
import org.mbesch.lab5.dto.Requests.TokenPairResponse;
import org.mbesch.lab5.entity.SessionStatus;
import org.mbesch.lab5.entity.User;
import org.mbesch.lab5.entity.UserSession;
import org.mbesch.lab5.repository.UserRepository;
import org.mbesch.lab5.repository.UserSessionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class TokenService {
    private final JwtService jwtService;
    private final UserSessionRepository sessionRepository;
    private final UserRepository userRepository;

    public TokenService(
            JwtService jwtService,
            UserSessionRepository sessionRepository,
            UserRepository userRepository) {
        this.jwtService = jwtService;
        this.sessionRepository = sessionRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public TokenPairResponse issueTokens(User user, String deviceId) {
        if (user == null || "BLOCKED".equalsIgnoreCase(user.getStatus())) {
            throw new JwtAuthException("User is unavailable or blocked");
        }
        JwtService.GeneratedToken access = jwtService.generateAccessToken(user);
        JwtService.GeneratedToken refresh = jwtService.generateRefreshToken(user);

        UserSession session = new UserSession(
                null,
                user.getEmail(),
                deviceId,
                access.token(),
                refresh.token(),
                access.expiresAt(),
                refresh.expiresAt(),
                SessionStatus.ACTIVE
        );
        sessionRepository.saveAndFlush(session);
        return new TokenPairResponse(access.token(), refresh.token());
    }

    @Transactional(noRollbackFor = JwtAuthException.class)
    public TokenPairResponse refreshTokens(RefreshRequest request) {
        if (request == null || request.refreshToken() == null || request.refreshToken().isBlank()) {
            throw new IllegalArgumentException("refreshToken is required");
        }

        Claims claims = jwtService.validateRefreshToken(request.refreshToken());
        String subject = claims.getSubject();

        UserSession session = sessionRepository.findByRefreshToken(request.refreshToken())
                .orElseThrow(() -> new JwtAuthException("Session not found"));

        if (!subject.equals(session.getUsername())) {
            throw new JwtAuthException("Token subject mismatch");
        }

        User user = userRepository.findByEmail(session.getUsername())
                .orElseThrow(() -> new JwtAuthException("User not found"));

        if ("BLOCKED".equalsIgnoreCase(user.getStatus())) {
            throw new JwtAuthException("User is blocked");
        }

        Instant now = Instant.now();

        if (session.getStatus() == SessionStatus.USED) {
            if (session.getRefreshTokenExpiry().isAfter(now)) {
                session.setStatus(SessionStatus.REVOKED);
                sessionRepository.saveAndFlush(session);
            }
            throw new JwtAuthException("Refresh token has already been used");
        }

        if (session.getStatus() == SessionStatus.REVOKED) {
            throw new JwtAuthException("Refresh token is revoked");
        }

        if (!session.getRefreshTokenExpiry().isAfter(now)) {
            throw new JwtAuthException("Refresh token expired");
        }

        session.setStatus(SessionStatus.USED);
        sessionRepository.saveAndFlush(session);

        JwtService.GeneratedToken newAccess = jwtService.generateAccessToken(user);
        JwtService.GeneratedToken newRefresh = jwtService.generateRefreshToken(user);

        UserSession newSession = new UserSession(
                null,
                user.getEmail(),
                session.getDeviceId(),
                newAccess.token(),
                newRefresh.token(),
                newAccess.expiresAt(),
                newRefresh.expiresAt(),
                SessionStatus.ACTIVE
        );
        sessionRepository.saveAndFlush(newSession);

        return new TokenPairResponse(newAccess.token(), newRefresh.token());
    }
}
