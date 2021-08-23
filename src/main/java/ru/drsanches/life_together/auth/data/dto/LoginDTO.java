package ru.drsanches.life_together.auth.data.dto;

import io.swagger.annotations.ApiModelProperty;

public class LoginDTO {

    @ApiModelProperty(required = true)
    private String username;

    @ApiModelProperty(notes = "password hash", required = true)
    private String password;

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String toString() {
        return "LoginDTO{" +
                "username='" + username + '\'' +
                '}';
    }
}