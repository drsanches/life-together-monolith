package ru.drsanches.life_together.integration.token;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import ru.drsanches.life_together.auth.data.model.UserAuth;
import ru.drsanches.life_together.auth.service.UserAuthDomainService;
import ru.drsanches.life_together.exception.application.NoUsernameException;
import ru.drsanches.life_together.exception.auth.WrongTokenException;
import ru.drsanches.life_together.exception.auth.WrongUsernamePasswordException;

@Component
public class CredentialsHelper {

    private static final BCryptPasswordEncoder ENCODER = new BCryptPasswordEncoder();

    @Autowired
    private UserAuthDomainService userAuthDomainService;

    public void checkUser(String username, String password) {
        try {
            UserAuth user = userAuthDomainService.getEnabledByUsername(username);
            if (!ENCODER.matches(password, user.getPassword())) {
                throw new WrongUsernamePasswordException();
            }
        } catch (NoUsernameException e) {
            throw new WrongUsernamePasswordException(e);
        }
    }

    public void checkPassword(String rawPassword, String encodedPassword) {
        if (!ENCODER.matches(rawPassword, encodedPassword)) {
            throw new WrongTokenException();
        }
    }

    public String encodePassword(String password) {
        return ENCODER.encode(password);
    }
}