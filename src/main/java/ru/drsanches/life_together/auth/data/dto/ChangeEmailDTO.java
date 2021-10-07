package ru.drsanches.life_together.auth.data.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.hibernate.validator.constraints.NotEmpty;

public class ChangeEmailDTO {

    @Schema(required = true)
    private String newEmail;

    @NotEmpty
    @Schema(required = true, description = "current user password hash")
    private String password;

    public String getNewEmail() {
        return newEmail;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String toString() {
        return "ChangeEmailDTO{" +
                "newEmail='" + newEmail + '\'' +
                '}';
    }
}