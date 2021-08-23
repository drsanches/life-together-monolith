package ru.drsanches.life_together.app.data.profile.mapper;

import org.springframework.stereotype.Component;
import ru.drsanches.life_together.app.data.profile.dto.UserInfoDTO;
import ru.drsanches.life_together.app.data.profile.model.UserProfile;

@Component
public class UserInfoMapper {

    public UserInfoDTO convert(UserProfile userProfile) {
        UserInfoDTO userInfoDTO = new UserInfoDTO();
        userInfoDTO.setId(userProfile.getId());
        if (userProfile.isEnabled()) {
            userInfoDTO.setUsername(userProfile.getUsername());
            userInfoDTO.setFirstName(userProfile.getFirstName());
            userInfoDTO.setLastName(userProfile.getLastName());
        }
        return userInfoDTO;
    }
}