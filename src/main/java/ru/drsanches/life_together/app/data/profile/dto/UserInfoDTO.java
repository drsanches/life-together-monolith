package ru.drsanches.life_together.app.data.profile.dto;

import io.swagger.annotations.ApiModelProperty;

public class UserInfoDTO {

    @ApiModelProperty(required = true)
    private String id;

    @ApiModelProperty(required = true, notes = "null for deleted user")
    private String username;

    @ApiModelProperty(required = true, notes = "null for deleted user")
    private String firstName;

    @ApiModelProperty(required = true, notes = "null for deleted user")
    private String lastName;

    public UserInfoDTO() {}

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Override
    public String toString() {
        return "UserInfoDTO{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                '}';
    }
}