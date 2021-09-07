package ru.drsanches.life_together.app.data.debts.dto;

import io.swagger.annotations.ApiModelProperty;
import ru.drsanches.life_together.app.service.validation.annotation.ExistsId;
import ru.drsanches.life_together.app.service.validation.annotation.NotCurrentId;

public class CancelDTO {

    @ApiModelProperty(required = true)
    @ExistsId
    @NotCurrentId
    private String userId;

    public String getUserId() {
        return userId;
    }

    @Override
    public String toString() {
        return "CancelDTO{" +
                "userId='" + userId + '\'' +
                '}';
    }
}