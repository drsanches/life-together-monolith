package ru.drsanches.life_together.app.data.profile.dto;

import io.swagger.annotations.ApiModelProperty;

public class UserInfoDTO {

    @ApiModelProperty(required = true)
    private String id;

    @ApiModelProperty(notes = "null for deleted user")
    private String username;

    @ApiModelProperty(notes = "null for deleted user")
    private String firstName;

    @ApiModelProperty(notes = "null for deleted user")
    private String lastName;

    @ApiModelProperty(notes = "null for deleted user")
    private String imagePath;

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

    public String getImagePath() {
        return imagePath;
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

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    @Override
    public String toString() {
        return "UserInfoDTO{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", imagePath='" + imagePath + '\'' +
                '}';
    }
}