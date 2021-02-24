package ru.drsanches.life_together.data.auth.dto;

public class ChangeEmailDTO {

    private String newEmail;

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