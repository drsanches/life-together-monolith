package ru.drsanches.life_together.app.data.debts.dto;

import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.NotEmpty;
import ru.drsanches.life_together.app.service.validation.annotation.ExistsId;
import ru.drsanches.life_together.app.service.validation.annotation.NotCurrentId;

public class CancelDTO {

    @NotEmpty
    @ExistsId
    @NotCurrentId
    @ApiModelProperty(required = true)
    private String userId;

    @ApiModelProperty()
    private String message;

    public String getUserId() {
        return userId;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "CancelDTO{" +
                "userId='" + userId + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}