package ru.drsanches.life_together.auth.exception;

import java.util.UUID;

public class ApplicationException extends RuntimeException {

    private Exception cause;

    private final String message;

    private final String uuid;

    public ApplicationException(String message) {
        this.message = message;
        this.uuid = UUID.randomUUID().toString();
    }

    public ApplicationException(String message, Exception cause) {
        this.message = message;
        this.uuid = UUID.randomUUID().toString();
        this.cause = cause;
    }

    @Override
    public String getMessage() {
        return "{\"uuid\":\"" + uuid + "\",\"message\":\"" + message + "\"}";
    }
}