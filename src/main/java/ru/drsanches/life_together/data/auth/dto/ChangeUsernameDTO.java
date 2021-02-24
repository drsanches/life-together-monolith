package ru.drsanches.life_together.data.auth.dto;

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