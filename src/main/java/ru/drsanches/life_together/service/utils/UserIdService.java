package ru.drsanches.life_together.service.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Service;
import ru.drsanches.life_together.data.auth.user.UserAuth;
import ru.drsanches.life_together.repository.UserAuthRepository;
import java.util.Optional;

@Service
public class UserIdService {

    @Autowired
    UserAuthRepository userAuthRepository;

    /**
     * Gets user id from DB
     * @param username username to search for id
     * @return user id or null if user does not exist
     */
    public String getUserIdFromDB(String username) {
        Optional<UserAuth> user = userAuthRepository.findByUsername(username);
        if (user.isEmpty() || !user.get().isEnabled()) {
            return null;
        }
        return user.get().getId();
    }

    public String getUserIdFromAuth(OAuth2Authentication authentication) {
        return ((UserAuth) authentication.getUserAuthentication().getPrincipal()).getId();
    }
}