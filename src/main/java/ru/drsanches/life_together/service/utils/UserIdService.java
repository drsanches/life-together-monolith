package ru.drsanches.life_together.service.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.drsanches.life_together.data.auth.model.UserAuth;
import ru.drsanches.life_together.repository.UserAuthRepository;
import java.util.Optional;

@Service
public class UserIdService {

    private final Logger LOG = LoggerFactory.getLogger(UserIdService.class);

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
            LOG.warn("No user with username '{}'", username);
            return null;
        }
        return user.get().getId();
    }
}