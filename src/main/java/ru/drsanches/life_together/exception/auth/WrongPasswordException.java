package ru.drsanches.life_together.exception.auth;

public class WrongPasswordException extends AuthException {

    public WrongPasswordException() {
        super("Wrong password");
    }

    public WrongPasswordException(Exception cause) {
        super("Wrong password", cause);
    }
}