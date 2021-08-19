package ru.drsanches.life_together.exception;

public class AuthException extends RuntimeException {

    public AuthException() {
        super("Wrong token");
    }

    public AuthException(String message) {
        super(message);
    }
}