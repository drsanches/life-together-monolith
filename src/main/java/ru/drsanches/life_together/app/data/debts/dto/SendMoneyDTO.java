package ru.drsanches.life_together.app.data.debts.dto;

import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.NotEmpty;
import ru.drsanches.life_together.app.service.validation.annotation.EnabledIdList;
import ru.drsanches.life_together.app.service.validation.annotation.FriendIdList;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Set;

public class SendMoneyDTO {

    @NotEmpty
    @FriendIdList(mayContainCurrent = true)
    @EnabledIdList
    @ApiModelProperty(required = true)
    private Set<String> toUserIds;

    @Min(value = 1, message = "must be positive")
    @NotNull
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