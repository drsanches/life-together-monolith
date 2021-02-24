package ru.drsanches.life_together.data.auth.dto;

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