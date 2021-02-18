package ru.drsanches.life_together.auth.data.dto;

public class DeleteUserDTO {

    private String password;

    public String getPassword() {
        return password;
    }

    @Override
    public String toString() {
        return "DeleteUserDTO{}";
    }
}