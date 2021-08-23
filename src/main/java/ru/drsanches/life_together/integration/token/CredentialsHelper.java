package ru.drsanches.life_together.integration.token;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import ru.drsanches.life_together.auth.data.model.UserAuth;
import ru.drsanches.life_together.exception.AuthException;
import ru.drsanches.life_together.auth.data.repository.UserAuthRepository;
import java.util.Optional;

@Component
public class CredentialsHelper {

    private static final BCryptPasswordEncoder ENCODER = new BCryptPasswordEncoder();

    @Autowired
    private UserAuthRepository userAuthRepository;

    public void checkUser(String username, String password) {
        Optional<UserAuth> user = userAuthRepository.findByUsername(username);
        if (user.isEmpty() || !user.get().isEnabled() || !ENCODER.matches(password, user.get().getPassword())) {
            throw new AuthException("Wrong username or password");
        }
    }

    public void checkPassword(String rawPassword, String encodedPassword) {
        if (!ENCODER.matches(rawPassword, encodedPassword)) {
            throw new AuthException("Wrong password");
        }
    }

    public String encodePassword(String password) {
        return ENCODER.encode(password);
    }
}
