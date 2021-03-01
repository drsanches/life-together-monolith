package ru.drsanches.life_together.data.auth.dto;

import io.swagger.annotations.ApiModelProperty;

public class DeleteUserDTO {

    @ApiModelProperty(notes = "current user password hash", required = true)
    private String password;

    public String getPassword() {
        return password;
    }

    @Override
    public String toString() {
        return "DeleteUserDTO{}";
    }
}