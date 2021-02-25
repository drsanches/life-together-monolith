package ru.drsanches.life_together.exception;

public class NoUserException extends ApplicationException {

    private final static String FORMAT = "There is no user with username '%s'";

    public NoUserException(String username) {
        super(String.format(FORMAT, username));
    }

    public NoUserException(String username, Exception cause) {
        super(String.format(FORMAT, username), cause);
    }
}