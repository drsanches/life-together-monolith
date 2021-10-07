package ru.drsanches.life_together.app.data.debts.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.hibernate.validator.constraints.NotEmpty;
import ru.drsanches.life_together.app.service.validation.annotation.EnabledId;
import ru.drsanches.life_together.app.service.validation.annotation.FriendId;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class SendTransactionDTO {

    @NotEmpty
    @FriendId
    @EnabledId
    @Schema(required = true)
    private String toUserId;

    @Min(value = 1, message = "must be positive")
    @NotNull
    @Schema(required = true)
    private Integer money;

    @Schema
    private String message;

    public String getToUserId() {
        return toUserId;
    }

    public Integer getMoney() {
        return money;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "SendTransactionDTO{" +
                "toUserId='" + toUserId + '\'' +
                ", money=" + money +
                ", message='" + message + '\'' +
                '}';
    }
}