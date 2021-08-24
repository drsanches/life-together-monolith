package ru.drsanches.life_together.app.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.drsanches.life_together.app.data.profile.dto.ChangeUserProfileDTO;
import ru.drsanches.life_together.app.data.profile.dto.UserInfoDTO;
import ru.drsanches.life_together.app.data.profile.model.UserProfile;
import ru.drsanches.life_together.exception.application.NoUserIdException;
import ru.drsanches.life_together.exception.application.NoUsernameException;
import ru.drsanches.life_together.app.data.profile.repository.UserProfileRepository;
import ru.drsanches.life_together.exception.server.ServerError;
import ru.drsanches.life_together.app.service.utils.UserInfoService;
import ru.drsanches.life_together.integration.token.TokenService;
import java.util.Optional;

@Service
public class UserProfileService {

    private final Logger LOG = LoggerFactory.getLogger(UserProfileService.class);

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private TokenService tokenService;

    public UserInfoDTO getCurrentProfile(String token) {
        String userId = tokenService.getUserId(token);
        return userInfoService.getUserInfo(userId);
    }

    public UserInfoDTO searchProfile(String username) {
        String userId = getUserId(username);
        if (userId == null) {
            throw new NoUsernameException(username);
        }
        return userInfoService.getUserInfo(userId);
    }

    public UserInfoDTO getProfile(String userId) {
        UserInfoDTO userInfoDTO = userInfoService.getUserInfo(userId);
        if (userInfoDTO == null) {
            throw new NoUserIdException(userId);
        }
        return userInfoDTO;
    }

    public void changeCurrentProfile(String token, ChangeUserProfileDTO changeUserProfileDTO) {
        String userId = tokenService.getUserId(token);
        UserProfile userProfile = getUserByIdIfExists(userId);
        userProfile.setFirstName(changeUserProfileDTO.getFirstName());
        userProfile.setLastName(changeUserProfileDTO.getLastName());
        userProfileRepository.save(userProfile);
        LOG.info("User profile has been changed: {}", userProfile.toString());
    }

    private UserProfile getUserByIdIfExists(String userId) {
        Optional<UserProfile> user = userProfileRepository.findById(userId);
        if (user.isEmpty()) {
            throw new ServerError("An error occurred while getting the user with id'" + userId + "'");
        }
        return user.get();
    }

    private String getUserId(String username) {
        Optional<UserProfile> userProfile = userProfileRepository.findByUsername(username);
        if (userProfile.isEmpty() || !userProfile.get().isEnabled()) {
            LOG.warn("No user with username '{}'", username);
            return null;
        }
        return userProfile.get().getId();
    }
}