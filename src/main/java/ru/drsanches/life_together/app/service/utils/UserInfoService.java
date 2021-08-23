package ru.drsanches.life_together.app.service.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.drsanches.life_together.app.data.profile.dto.UserInfoDTO;
import ru.drsanches.life_together.app.data.profile.model.UserProfile;
import ru.drsanches.life_together.app.data.repository.UserProfileRepository;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class UserInfoService {

    @Autowired
    UserProfileRepository userProfileRepository;

    public boolean userProfileExists(String userId) {
        return userProfileRepository.existsById(userId);
    }

    public boolean userProfileEnabled(String userId) {
        Optional<UserProfile> userProfile = userProfileRepository.findById(userId);
        return userProfile.isPresent() && userProfile.get().isEnabled();
    }

    public UserInfoDTO getUserInfo(String userId) {
        Optional<UserProfile> userProfile = userProfileRepository.findById(userId);
        if (userProfile.isEmpty() || !userProfile.get().isEnabled()) {
            return null;
        }
        UserInfoDTO userInfoDTO = new UserInfoDTO();
        userInfoDTO.setId(userId);
        userInfoDTO.setUsername(userProfile.get().getUsername());
        userInfoDTO.setFirstName(userProfile.get().getFirstName());
        userInfoDTO.setLastName(userProfile.get().getLastName());
        return userInfoDTO;
    }

    /**
     * @param userIds collection of user id
     * @return Set of UserInfoDTO objects. If the user was deleted, then only id will be filled.
     */
    public Set<UserInfoDTO> getUserInfoSet(Collection<String> userIds) {
        Set<UserInfoDTO> result = new HashSet<>();
        userProfileRepository.findAllById(userIds).forEach(userProfile -> {
            UserInfoDTO userInfoDTO = new UserInfoDTO();
            userInfoDTO.setId(userProfile.getId());
            if (userProfile.isEnabled()) {
                userInfoDTO.setUsername(userProfile.getUsername());
                userInfoDTO.setFirstName(userProfile.getFirstName());
                userInfoDTO.setLastName(userProfile.getLastName());
            }
            result.add(userInfoDTO);
        });
        return result;
    }
}