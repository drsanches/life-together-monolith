package ru.drsanches.life_together.exception.auth;

public class WrongTokenException extends AuthException {

    public WrongTokenException() {
        super("Wrong token");
    }

    public WrongTokenException(Exception cause) {
        super("Wrong token", cause);
    }
}