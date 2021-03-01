package ru.drsanches.life_together.data.auth.dto;

import io.swagger.annotations.ApiModelProperty;

public class UserAuthInfoDTO {

    @ApiModelProperty(required = true)
    private String id;

    @ApiModelProperty(required = true)
    private String username;

    @ApiModelProperty(required = true)
    private String email;

    public UserAuthInfoDTO() {}

    public UserAuthInfoDTO(String id, String username, String email) {
        this.id = id;
        this.username = username;
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "UserAuthInfoDTO{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}