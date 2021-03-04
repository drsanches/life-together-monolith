package ru.drsanches.life_together.exception;

import java.util.UUID;

public class ApplicationException extends RuntimeException {

    private final String message;

    private final String uuid = UUID.randomUUID().toString();

    public ApplicationException(String message) {
        this.message = message;
    }

    public ApplicationException(String message, Exception cause) {
        super(cause);
        this.message = message;
    }

    @Override
    public String getMessage() {
        return "{\"uuid\":\"" + uuid + "\",\"message\":\"" + message + "\"}";
    }
}