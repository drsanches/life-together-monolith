package ru.drsanches.life_together.data.auth.dto;

import io.swagger.annotations.ApiModelProperty;

public class ChangeUsernameDTO {

    @ApiModelProperty(required = true)
    private String newUsername;

    @ApiModelProperty(notes = "current user password hash", required = true)
    private String password;

    public String getNewUsername() {
        return newUsername;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String toString() {
        return "ChangeUsernameDTO{" +
                ", newUsername='" + newUsername + '\'' +
                '}';
    }
}