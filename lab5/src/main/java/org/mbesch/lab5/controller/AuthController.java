package org.mbesch.lab5.controller;

import org.mbesch.lab5.dto.Requests.LoginRequest;
import org.mbesch.lab5.dto.Requests.RefreshRequest;
import org.mbesch.lab5.dto.Requests.RegisterRequest;
import org.mbesch.lab5.dto.Requests.TokenPairResponse;
import org.mbesch.lab5.service.AuthService;
import org.mbesch.lab5.service.TokenService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;
    private final TokenService tokenService;

    public AuthController(AuthService authService, TokenService tokenService) {
        this.authService = authService;
        this.tokenService = tokenService;
    }

    @PostMapping("/login")
    public ResponseEntity<TokenPairResponse> login(@RequestBody(required = false) LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/register")
    public ResponseEntity<TokenPairResponse> register(@RequestBody(required = false) RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenPairResponse> refresh(@RequestBody(required = false) RefreshRequest request) {
        return ResponseEntity.ok(tokenService.refreshTokens(request));
    }
}
