package ru.drsanches.life_together.auth.data.dto;

public class LoginDTO {

    private String username;

    private String password;

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String toString() {
        return "LoginDTO{" +
                "username='" + username + '\'' +
                '}';
    }
}