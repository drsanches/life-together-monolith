package ru.drsanches.life_together.auth.data.dto;

import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.NotEmpty;

public class RegistrationDTO {

    @NotEmpty
    @ApiModelProperty(required = true)
    private String username;

    @NotEmpty
    @ApiModelProperty(required = true, notes = "password hash")
    private String password;

    @ApiModelProperty
    private String email;

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "UserAuthDTO{" +
                "username='" + username + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}