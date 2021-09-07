package ru.drsanches.life_together.auth.data.dto;

import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.NotEmpty;

public class LoginDTO {

    @NotEmpty
    @ApiModelProperty(required = true)
    private String username;

    @NotEmpty
    @ApiModelProperty(required = true, notes = "password hash")
    private String password;

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "LoginDTO{" +
                "username='" + username + '\'' +
                '}';
    }
}