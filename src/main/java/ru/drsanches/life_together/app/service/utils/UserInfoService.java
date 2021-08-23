package ru.drsanches.life_together.app.service.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.drsanches.life_together.app.data.profile.dto.UserInfoDTO;
import ru.drsanches.life_together.app.data.profile.mapper.UserInfoMapper;
import ru.drsanches.life_together.app.data.profile.model.UserProfile;
import ru.drsanches.life_together.app.data.profile.repository.UserProfileRepository;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class UserInfoService {

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private UserInfoMapper userInfoMapper;

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
        return userInfoMapper.convert(userProfile.get());
    }

    /**
     * @param userIds collection of user id
     * @return Set of UserInfoDTO objects. If the user was deleted, then only id will be filled.
     */
    public Set<UserInfoDTO> getUserInfoSet(Collection<String> userIds) {
        Set<UserInfoDTO> result = new HashSet<>();
        userProfileRepository.findAllById(userIds).forEach(userProfile -> result.add(userInfoMapper.convert(userProfile)));
        return result;
    }
}