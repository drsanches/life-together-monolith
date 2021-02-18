package ru.drsanches.life_together.auth.data.dto;

public class ChangeUsernameDTO {

    private String newUsername;

    private String password;

    public String getNewUsername() {
        return newUsername;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String toString() {
        return "ChangeUsernameDTO{" +
                ", newUsername='" + newUsername + '\'' +
                '}';
    }
}