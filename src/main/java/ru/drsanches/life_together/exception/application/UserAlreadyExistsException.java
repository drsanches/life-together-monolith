package ru.drsanches.life_together.exception.application;

public class UserAlreadyExistsException extends ApplicationException {

    private final static String FORMAT = "Username '%s' already exists";

    public UserAlreadyExistsException(String username) {
        super(String.format(FORMAT, username));
    }

    public UserAlreadyExistsException(String username, Exception cause) {
        super(String.format(FORMAT, username), cause);
    }
}