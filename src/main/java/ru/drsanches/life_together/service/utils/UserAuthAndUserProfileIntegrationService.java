package ru.drsanches.life_together.service.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import ru.drsanches.life_together.data.auth.user.UserAuth;
import ru.drsanches.life_together.data.profile.user.UserProfile;
import ru.drsanches.life_together.exception.UserAlreadyExistsException;
import ru.drsanches.life_together.repository.UserAuthRepository;
import ru.drsanches.life_together.repository.UserProfileRepository;

@Service
public class UserAuthAndUserProfileIntegrationService {

    private final Logger LOG = LoggerFactory.getLogger(UserAuthAndUserProfileIntegrationService.class);

    @Autowired
    UserAuthRepository userAuthRepository;

    @Autowired
    UserProfileRepository userProfileRepository;

    //TODO: Transaction
    /**
     * Saves UserAuth and UserProfile objects
     * @param userAuth UserAuth object
     * @param userProfile UserProfile object
     */
    public void saveUserAuthAndUserProfile(UserAuth userAuth, UserProfile userProfile) {
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
     * Updates UserAuth object and removes UserProfile object
     * @param userAuth UserAuth object
     * @param userProfileId id of UserProfile object
     */
    public void updateUserAuthAndRemoveUserProfile(UserAuth userAuth, String userProfileId) {
        try {
            userAuthRepository.save(userAuth);
            LOG.info("User auth has been updated in db: {}", userAuth.toString());
        } catch(EmptyResultDataAccessException e) {
            LOG.error("User auth does not exist: " + userAuth.toString(), e);
        }
        try {
            userProfileRepository.deleteById(userProfileId);
            LOG.info("User profile with id '{}' has been deleted from db", userProfileId);
        } catch(EmptyResultDataAccessException e) {
            LOG.error("User profile with id'" + userProfileId + "' does not exist", e);
        }
    }
}