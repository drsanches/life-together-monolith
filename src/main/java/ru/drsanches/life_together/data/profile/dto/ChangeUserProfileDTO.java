package ru.drsanches.life_together.data.profile.dto;

import io.swagger.annotations.ApiModelProperty;

public class ChangeUserProfileDTO {

    @ApiModelProperty(required = false)
    private String firstName;

    @ApiModelProperty(required = false)
    private String lastName;

    public ChangeUserProfileDTO() {}

    public ChangeUserProfileDTO(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Override
    public String toString() {
        return "ChangeUserProfileDTO{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                '}';
    }
}