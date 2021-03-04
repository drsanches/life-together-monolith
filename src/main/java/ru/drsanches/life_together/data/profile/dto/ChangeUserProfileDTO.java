package ru.drsanches.life_together.data.profile.dto;

import io.swagger.annotations.ApiModelProperty;

public class ChangeUserProfileDTO {

    @ApiModelProperty
    private String firstName;

    @ApiModelProperty
    private String lastName;

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    @Override
    public String toString() {
        return "ChangeUserProfileDTO{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                '}';
    }
}