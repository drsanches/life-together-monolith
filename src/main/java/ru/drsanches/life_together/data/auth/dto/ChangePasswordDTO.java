package ru.drsanches.life_together.data.auth.dto;

import io.swagger.annotations.ApiModelProperty;

public class ChangePasswordDTO {

    @ApiModelProperty(notes = "current user password hash", required = true)
    private String oldPassword;

    @ApiModelProperty(notes = "new password hash", required = true)
    private String newPassword;

    public String getOldPassword() {
        return oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    @Override
    public String toString() {
        return "ChangePasswordDTO{}";
    }
}