package ru.drsanches.life_together.auth.service.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import ru.drsanches.life_together.exception.auth.WrongPasswordException;

@Component
public class CredentialsHelper {

    private static final BCryptPasswordEncoder ENCODER = new BCryptPasswordEncoder();

    public void checkPassword(String rawPassword, String encodedPassword) {
        if (!ENCODER.matches(rawPassword, encodedPassword)) {
            throw new WrongPasswordException();
        }
    }

    public String encodePassword(String password) {
        return ENCODER.encode(password);
    }
}