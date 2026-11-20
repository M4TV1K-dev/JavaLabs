package org.mbesch.lab5.service;

public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }
}
