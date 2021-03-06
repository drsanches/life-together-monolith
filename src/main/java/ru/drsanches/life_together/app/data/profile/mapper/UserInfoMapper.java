package ru.drsanches.life_together.app.data.profile.mapper;

import org.springframework.stereotype.Component;
import ru.drsanches.life_together.app.data.profile.dto.UserInfoDTO;
import ru.drsanches.life_together.app.data.profile.model.UserProfile;

@Component
public class UserInfoMapper {

    private static final String PATH = "/api/v1/image/";

    private static final String DEFAULT_ID = "default";

    public UserInfoDTO convert(UserProfile userProfile) {
        UserInfoDTO userInfoDTO = new UserInfoDTO();
        userInfoDTO.setId(userProfile.getId());
        if (userProfile.isEnabled()) {
            userInfoDTO.setUsername(userProfile.getUsername());
            userInfoDTO.setFirstName(userProfile.getFirstName());
            userInfoDTO.setLastName(userProfile.getLastName());
            userInfoDTO.setImagePath(userProfile.getImageId() == null ?
                    PATH + DEFAULT_ID : PATH + userProfile.getImageId());
        }
        return userInfoDTO;
    }
}