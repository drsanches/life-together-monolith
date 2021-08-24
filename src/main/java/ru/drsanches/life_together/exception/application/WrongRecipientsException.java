package ru.drsanches.life_together.exception.application;

import java.util.Set;

public class WrongRecipientsException extends ApplicationException {

    private final Set<String> wrongIds;

    public WrongRecipientsException(Set<String> wrongIds) {
        super("You can't send money to this user(s)");
        this.wrongIds = wrongIds;
    }

    @Override
    public String getMessage() {
        return "{\"uuid\":\"" + uuid + "\",\"message\":\"" + message + ",\"wrongIds\":\"" + wrongIds + "\"}";
    }
}