package ru.drsanches.life_together.auth.data.dto;

import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.NotEmpty;

public class ChangePasswordDTO {

    @NotEmpty
    @ApiModelProperty(notes = "current user password hash", required = true)
    private String oldPassword;

    @NotEmpty
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