package ru.drsanches.life_together.exception;

import java.util.UUID;

public class ServerError extends RuntimeException {

    private Exception cause;

    private final String message = "An internal error has occurred, try again later or contact support";

    private String info;

    private final String uuid = UUID.randomUUID().toString();

    public ServerError() {}

    public ServerError(String info) {
        this.info = info;
    }

    public ServerError(Exception cause) {
        this.cause = cause;
    }

    public ServerError(String info, Exception cause) {
        this.info = info;
        this.cause = cause;
    }

    public String getInfo() {
        return "{\"uuid\":\"" + uuid + "\",\"info\":\"" + info + "\"}";
    }

    @Override
    public String getMessage() {
        return "{\"uuid\":\"" + uuid + "\",\"message\":\"" + message + "\"}";
    }
}