package ru.drsanches.life_together.exception.application;

import java.util.List;

public class WrongRecipientsException extends ApplicationException {

    private final List<String> wrongIds;

    public WrongRecipientsException(List<String> wrongIds) {
        super("You can't send money to this user(s)");
        this.wrongIds = wrongIds;
    }

    @Override
    public String getMessage() {
        return "{\"uuid\":\"" + uuid + "\",\"message\":\"" + message + ",\"wrongIds\":\"" + wrongIds + "\"}";
    }
}