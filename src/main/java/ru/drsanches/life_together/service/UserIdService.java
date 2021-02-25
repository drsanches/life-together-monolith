package ru.drsanches.life_together.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Service;
import ru.drsanches.life_together.data.auth.user.UserAuth;
import ru.drsanches.life_together.repository.UserAuthRepository;
import ru.drsanches.life_together.exception.NoUserException;
import java.util.Optional;

@Service
public class UserIdService {

    @Autowired
    UserAuthRepository userAuthRepository;

    public String getUserIdFromDB(String username) {
        Optional<UserAuth> user = userAuthRepository.findByUsername(username);
        if (user.isEmpty() || !user.get().isEnabled()) {
            throw new NoUserException(username);
        }
        return user.get().getId();
    }

    public String getUserIdFromAuth(OAuth2Authentication authentication) {
        return ((UserAuth) authentication.getUserAuthentication().getPrincipal()).getId();
    }
}