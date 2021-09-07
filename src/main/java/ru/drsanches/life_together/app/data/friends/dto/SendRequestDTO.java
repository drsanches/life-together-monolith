package ru.drsanches.life_together.app.data.friends.dto;

import io.swagger.annotations.ApiModelProperty;
import ru.drsanches.life_together.app.service.validation.annotation.EnabledId;
import ru.drsanches.life_together.app.service.validation.annotation.NotCurrentId;

public class SendRequestDTO {

    @EnabledId
    @NotCurrentId
    @ApiModelProperty(required = true)
    private String userId;

    public String getUserId() {
        return userId;
    }

    @Override
    public String toString() {
        return "SendRequestDTO{" +
                "userId='" + userId + '\'' +
                '}';
    }
}