package ru.drsanches.life_together.integration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.drsanches.life_together.auth.data.model.UserAuth;
import ru.drsanches.life_together.app.data.profile.dto.UserInfoDTO;
import ru.drsanches.life_together.app.data.profile.model.UserProfile;
import ru.drsanches.life_together.auth.data.repository.UserAuthRepository;
import ru.drsanches.life_together.app.data.repository.UserProfileRepository;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
public class UserInfoService {

    private final Logger LOG = LoggerFactory.getLogger(UserInfoService.class);

    @Autowired
    UserAuthRepository userAuthRepository;

    @Autowired
    UserProfileRepository userProfileRepository;

    public boolean userAuthExists(String userId) {
        return userAuthRepository.existsById(userId);
    }

    public boolean userProfileEnabled(String userId) {
        Optional<UserProfile> userProfile = userProfileRepository.findById(userId);
        return userProfile.isPresent() && userProfile.get().isEnabled();
    }

    public UserInfoDTO getUserInfo(String userId) {
        Optional<UserAuth> userAuth = userAuthRepository.findById(userId);
        if (userAuth.isEmpty() || !userAuth.get().isEnabled()) {
            return null;
        }
        Optional<UserProfile> userProfile = userProfileRepository.findById(userId);
        if (userProfile.isEmpty() || !userProfile.get().isEnabled()) {
            return null;
        }
        UserInfoDTO userInfoDTO = new UserInfoDTO();
        userInfoDTO.setId(userId);
        userInfoDTO.setUsername(userAuth.get().getUsername());
        userInfoDTO.setFirstName(userProfile.get().getFirstName());
        userInfoDTO.setLastName(userProfile.get().getLastName());
        return userInfoDTO;
    }

    /**
     * @param userIds collection of user id
     * @return Set of UserInfoDTO objects. If the user was deleted, then only id will be filled.
     */
    public Set<UserInfoDTO> getUserInfoSet(Collection<String> userIds) {
        Map<String, String> usernameMap = getUsernameMap(userIds);
        Map<String, UserProfile> userProfileMap = getUserProfileMap(userIds);

        Set<UserInfoDTO> result = new HashSet<>();
        for (String id: usernameMap.keySet()) {
            UserInfoDTO userInfoDTO = new UserInfoDTO();
            userInfoDTO.setId(id);
            UserProfile userProfile = userProfileMap.get(id);
            if (userProfile != null && userProfile.isEnabled()) {
                userInfoDTO.setUsername(usernameMap.get(id));
                userInfoDTO.setFirstName(userProfile.getFirstName());
                userInfoDTO.setLastName(userProfile.getLastName());
            }
            result.add(userInfoDTO);
        }
        return result;
    }

    private Map<String, String> getUsernameMap(Collection<String> userIds) {
        Map<String, String> userAuthMap = new HashMap<>();
        userAuthRepository.findAllById(userIds).forEach(x -> userAuthMap.put(x.getId(), x.getUsername()));
        return userAuthMap;
    }
    
    private Map<String, UserProfile> getUserProfileMap(Collection<String> userIds) {
        Map<String, UserProfile> userProfileMap = new HashMap<>();
        userProfileRepository.findAllById(userIds).forEach(x -> userProfileMap.put(x.getId(), x));
        return userProfileMap;
    }
}