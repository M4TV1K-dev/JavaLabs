package org.mbesch.lab4.controller;

import org.mbesch.lab4.dto.Requests.RegisterRequest;
import org.mbesch.lab4.entity.User;
import org.mbesch.lab4.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class AuthController {
    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Получение актуального CSRF-токена для выполнения модифицирующих запросов (POST, PATCH, DELETE).
     */
    @GetMapping("/csrf")
    public Map<String, String> getCsrf(CsrfToken token) {
        return Map.of(
                "token", token.getToken(),
                "headerName", token.getHeaderName(),
                "parameterName", token.getParameterName()
        );
    }

    /**
     * Регистрация нового пользователя (всегда назначается роль ROLE_USER, повышение привилегий заблокировано).
     */
    @PostMapping("/auth/register")
    public ResponseEntity<User> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.register(request));
    }
}
