package ru.drsanches.life_together.auth.data.dto;

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