package ru.drsanches.life_together.auth.data.mapper;

import org.springframework.stereotype.Component;
import ru.drsanches.life_together.auth.data.dto.UserAuthInfoDTO;
import ru.drsanches.life_together.auth.data.model.UserAuth;

@Component
public class UserAuthInfoMapper {

    public UserAuthInfoDTO convert(UserAuth userAuth) {
        UserAuthInfoDTO userAuthInfoDTO = new UserAuthInfoDTO();
        userAuthInfoDTO.setId(userAuth.getId());
        userAuthInfoDTO.setUsername(userAuth.getUsername());
        userAuthInfoDTO.setEmail(userAuth.getEmail());
        return userAuthInfoDTO;
    }
}