package ru.drsanches.life_together.data.auth.dto;

public class ChangePasswordDTO {

    private String oldPassword;

    private String newPassword;

    public String getOldPassword() {
        return oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    @Override
    public String toString() {
        return "ChangePasswordDTO{}";
    }
}