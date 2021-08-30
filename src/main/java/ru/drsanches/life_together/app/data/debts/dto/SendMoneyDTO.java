package ru.drsanches.life_together.app.data.debts.dto;

import io.swagger.annotations.ApiModelProperty;
import java.util.Set;

public class SendMoneyDTO {

    @ApiModelProperty(required = true)
    private Set<String> toUserIds;

    @ApiModelProperty(required = true)
    private Integer money;

    @ApiModelProperty
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