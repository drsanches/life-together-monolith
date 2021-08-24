package ru.drsanches.life_together.app.service.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.drsanches.life_together.app.data.profile.dto.ChangeUserProfileDTO;
import ru.drsanches.life_together.app.data.profile.dto.UserInfoDTO;
import ru.drsanches.life_together.app.data.profile.mapper.UserInfoMapper;
import ru.drsanches.life_together.app.data.profile.model.UserProfile;
import ru.drsanches.life_together.app.service.domain.UserProfileDomainService;
import ru.drsanches.life_together.exception.application.NoUserIdException;
import ru.drsanches.life_together.exception.server.ServerError;
import ru.drsanches.life_together.integration.token.TokenService;

@Service
public class UserProfileWebService {

    private final Logger LOG = LoggerFactory.getLogger(UserProfileWebService.class);

    @Autowired
    private UserProfileDomainService userProfileDomainService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UserInfoMapper userInfoMapper;

    public UserInfoDTO getCurrentProfile(String token) {
        String userId = tokenService.getUserIdByAccessToken(token);
        UserProfile userProfile = userProfileDomainService.getEnabledById(userId);
        return userInfoMapper.convert(userProfile);
    }

    public UserInfoDTO searchProfile(String username) {
        UserProfile userProfile = userProfileDomainService.getEnabledByUsername(username);
        return userInfoMapper.convert(userProfile);
    }

    public UserInfoDTO getProfile(String userId) {
        UserProfile userProfile = userProfileDomainService.getEnabledById(userId);
        return userInfoMapper.convert(userProfile);
    }

    public void changeCurrentProfile(String token, ChangeUserProfileDTO changeUserProfileDTO) {
        String userId = tokenService.getUserIdByAccessToken(token);
        try {
            UserProfile userProfile = userProfileDomainService.getEnabledById(userId);
            userProfile.setFirstName(changeUserProfileDTO.getFirstName());
            userProfile.setLastName(changeUserProfileDTO.getLastName());
            userProfileDomainService.save(userProfile);
            LOG.info("User profile has been changed: {}", userProfile.toString());
        } catch (NoUserIdException e) {
            throw new ServerError("No user profile for userId " + userId, e);
        }
    }
}