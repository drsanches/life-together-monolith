package ru.drsanches.life_together.auth.data.dto;

public class UserAuthInfoDTO {

    private String username;

    private String email;

    public UserAuthInfoDTO() {}

    public UserAuthInfoDTO(String username, String email) {
        this.username = username;
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "UserAuthInfoDTO{" +
                "username='" + username + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}