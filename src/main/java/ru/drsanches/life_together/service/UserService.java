package ru.drsanches.life_together.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Service;
import ru.drsanches.life_together.data.user.dto.ChangeUserProfileDTO;
import ru.drsanches.life_together.data.user.dto.UserInfoDTO;
import ru.drsanches.life_together.data.user.profile.UserProfile;
import ru.drsanches.life_together.repository.UserProfileRepository;
import ru.drsanches.life_together.exception.ServerError;
import java.util.Optional;

@Service
public class UserService {

    private final Logger LOG = LoggerFactory.getLogger(UserService.class);

    @Autowired
    UserProfileRepository userProfileRepository;

    @Autowired
    UserIdService userIdService;

    public void createProfile(String userId) {
        UserProfile userProfile = new UserProfile(userId);
        try {
            userProfileRepository.save(userProfile);
            LOG.info("New user profile has been created: {}", userProfile.toString());
        } catch(DataIntegrityViolationException e) {
            LOG.error("User profile with id'" + userId + "' already exists", e);
        }
    }

    public UserInfoDTO getUser(OAuth2Authentication authentication) {
        String userId = userIdService.getUserIdFromAuth(authentication);
        return getUserInfo(userId, authentication.getName());
    }

    public UserInfoDTO getUser(String username) {
        String userId = userIdService.getUserIdFromDB(username);
        return getUserInfo(userId, username);
    }

    public void changeCurrentUser(OAuth2Authentication authentication, ChangeUserProfileDTO changeUserProfileDTO) {
        String userId = userIdService.getUserIdFromAuth(authentication);
        UserProfile userProfile = getUserByIdIfExists(userId);
        userProfile.setFirstName(changeUserProfileDTO.getFirstName());
        userProfile.setLastName(changeUserProfileDTO.getLastName());
        userProfileRepository.save(userProfile);
        LOG.info("User profile has been changed: {}", userProfile.toString());
    }

    public void deleteProfile(String userId) {
        try {
            userProfileRepository.deleteById(userId);
            LOG.info("User profile with id '{}' has been deleted", userId);
        } catch(EmptyResultDataAccessException e) {
            LOG.error("User profile with id'" + userId + "' does not exist", e);
        }
    }

    private UserInfoDTO getUserInfo(String userId, String username) {
        UserProfile userProfile = getUserByIdIfExists(userId);
        UserInfoDTO userInfoDTO = new UserInfoDTO();
        userInfoDTO.setId(userId);
        userInfoDTO.setUsername(username);
        userInfoDTO.setFirstName(userProfile.getFirstName());
        userInfoDTO.setLastName(userProfile.getLastName());
        return userInfoDTO;
    }

    private UserProfile getUserByIdIfExists(String userId) {
        Optional<UserProfile> user = userProfileRepository.findById(userId);
        if (user.isEmpty()) {
            throw new ServerError("An error occurred while getting the user with id'" + userId + "'");
        }
        return user.get();
    }
}