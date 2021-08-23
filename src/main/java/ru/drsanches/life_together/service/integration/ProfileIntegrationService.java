package ru.drsanches.life_together.service.integration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import ru.drsanches.life_together.data.auth.model.UserAuth;
import ru.drsanches.life_together.data.profile.model.UserProfile;
import ru.drsanches.life_together.exception.UserAlreadyExistsException;
import ru.drsanches.life_together.repository.UserAuthRepository;
import ru.drsanches.life_together.repository.UserProfileRepository;
import java.util.Optional;

//TODO: Refactor logic
@Service
public class ProfileIntegrationService {

    private final Logger LOG = LoggerFactory.getLogger(ProfileIntegrationService.class);

    @Autowired
    UserAuthRepository userAuthRepository;

    @Autowired
    UserProfileRepository userProfileRepository;

    //TODO: Transaction
    /**
     * Saves UserAuth and UserProfile objects
     * @param userAuth UserAuth object
     */
    public void createUser(UserAuth userAuth) {
        UserProfile userProfile = new UserProfile();
        userProfile.setId(userAuth.getId());
        userProfile.setUsername(userAuth.getUsername());
        userProfile.setEnabled(true);
        try {
            userAuthRepository.save(userAuth);
            LOG.info("User auth has been saved to db: {}", userAuth.toString());
        } catch(DataIntegrityViolationException e) {
            throw new UserAlreadyExistsException(userAuth.getUsername(), e);
        }
        try {
            userProfileRepository.save(userProfile);
            LOG.info("New user profile has been saved to db: {}", userProfile.toString());
        } catch(DataIntegrityViolationException e) {
            LOG.error("User profile with id'" + userProfile.getId() + "' already exists", e);
        }
    }

    //TODO: Transaction
    /**
     * Disables UserAuth and UserProfile objects
     * @param userAuth UserAuth object
     */
    public void disableUser(UserAuth userAuth) {
        try {
            userAuthRepository.save(userAuth);
            LOG.info("User auth has been updated in db: {}", userAuth.toString());
        } catch(EmptyResultDataAccessException e) {
            LOG.error("User auth does not exist: " + userAuth.toString(), e);
        }
        Optional<UserProfile> optionalUserProfile = userProfileRepository.findById(userAuth.getId());
        if (optionalUserProfile.isEmpty()) {
            LOG.error("User profile with id'" + userAuth.getId() + "' does not exist");
            return;
        }
        UserProfile userProfile = optionalUserProfile.get();
        userProfile.setEnabled(false);
        userProfile.setUsername(userAuth.getUsername());
        userProfileRepository.save(userProfile);
        LOG.info("User profile with id '{}' has been updated: {}", userAuth.getId(), userProfile);
    }
}