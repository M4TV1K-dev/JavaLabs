package org.mbesch.lab5.service;

import org.mbesch.lab5.dto.Requests.LoginRequest;
import org.mbesch.lab5.dto.Requests.RegisterRequest;
import org.mbesch.lab5.dto.Requests.TokenPairResponse;
import org.mbesch.lab5.entity.User;
import org.mbesch.lab5.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final UserService userService;
    private final TokenService tokenService;

    public AuthService(
            AuthenticationManager authenticationManager,
            UserRepository userRepository,
            UserService userService,
            TokenService tokenService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.userService = userService;
        this.tokenService = tokenService;
    }

    public TokenPairResponse login(LoginRequest request) {
        if (request == null
                || request.resolvedUsername() == null || request.resolvedUsername().isBlank()
                || request.password() == null || request.password().isBlank()) {
            throw new IllegalArgumentException("Username and password are required");
        }

        String username = request.resolvedUsername();
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, request.password())
        );

        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new JwtAuthException("User not found"));

        return tokenService.issueTokens(user, request.deviceId());
    }

    @Transactional
    public TokenPairResponse register(RegisterRequest request) {
        User user = userService.register(request);
        return tokenService.issueTokens(user, request.deviceId());
    }
}
