package ru.drsanches.life_together.data.debts.dto;

import java.util.Set;

public class SendMoneyDTO {

    private Set<String> toUserIds;

    private Integer money;

    private String message;

    public Set<String> getToUserIds() {
        return toUserIds;
    }

    public Integer getMoney() {
        return money;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "SendMoneyDTO{" +
                "toUsernames=" + toUserIds +
                ", money=" + money +
                ", message='" + message + '\'' +
                '}';
    }
}