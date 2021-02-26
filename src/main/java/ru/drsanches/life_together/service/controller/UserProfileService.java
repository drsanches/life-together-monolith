package ru.drsanches.life_together.service.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Service;
import ru.drsanches.life_together.data.profile.dto.ChangeUserProfileDTO;
import ru.drsanches.life_together.data.profile.dto.UserInfoDTO;
import ru.drsanches.life_together.data.profile.user.UserProfile;
import ru.drsanches.life_together.exception.NoUserException;
import ru.drsanches.life_together.repository.UserProfileRepository;
import ru.drsanches.life_together.exception.ServerError;
import ru.drsanches.life_together.service.utils.UserIdService;
import java.util.Optional;

@Service
public class UserProfileService {

    private final Logger LOG = LoggerFactory.getLogger(UserProfileService.class);

    @Autowired
    UserProfileRepository userProfileRepository;

    @Autowired
    UserIdService userIdService;

    public UserInfoDTO getProfile(OAuth2Authentication authentication) {
        String userId = userIdService.getUserIdFromAuth(authentication);
        return getUserInfo(userId, authentication.getName());
    }

    public UserInfoDTO getProfile(String username) {
        String userId = userIdService.getUserIdFromDB(username);
        if (userId == null) {
            throw new NoUserException(username);
        }
        return getUserInfo(userId, username);
    }

    public void changeCurrentProfile(OAuth2Authentication authentication, ChangeUserProfileDTO changeUserProfileDTO) {
        String userId = userIdService.getUserIdFromAuth(authentication);
        UserProfile userProfile = getUserByIdIfExists(userId);
        userProfile.setFirstName(changeUserProfileDTO.getFirstName());
        userProfile.setLastName(changeUserProfileDTO.getLastName());
        userProfileRepository.save(userProfile);
        LOG.info("User profile has been changed: {}", userProfile.toString());
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