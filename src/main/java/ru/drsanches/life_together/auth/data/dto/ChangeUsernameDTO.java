package ru.drsanches.life_together.auth.data.dto;

import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.NotEmpty;

public class ChangeUsernameDTO {

    @NotEmpty
    @ApiModelProperty(required = true)
    private String newUsername;

    @NotEmpty
    @ApiModelProperty(required = true, notes = "current user password hash")
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