package org.mbesch.lab5.service;

public class JwtAuthException extends RuntimeException {
    public JwtAuthException(String message) {
        super(message);
    }
}
