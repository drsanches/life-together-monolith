package ru.drsanches.life_together.service.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.drsanches.life_together.data.profile.dto.UserInfoDTO;
import ru.drsanches.life_together.repository.UserAuthRepository;
import ru.drsanches.life_together.repository.UserProfileRepository;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Service
public class UserInfoService {

    private final Logger LOG = LoggerFactory.getLogger(UserInfoService.class);

    @Autowired
    UserAuthRepository userAuthRepository;

    @Autowired
    UserProfileRepository userProfileRepository;

    public Set<UserInfoDTO> getUserInfoSet(Collection<String> userIds) {
        Map<String, String> userAuthMap = getUserAuthMap(userIds);
        Map<String, UserInfoDTO> userProfileMap = getUserProfileMap(userIds);
        userProfileMap.forEach((userId, userInfo) -> {
            String username = userAuthMap.get(userId);
            if (username != null) {
                userInfo.setUsername(userAuthMap.get(userId));
            } else {
                LOG.error("No UserAuth for UserProfile with id '{}'", userId);
            }
        });
        return Set.copyOf(userProfileMap.values());
    }

    private Map<String, String> getUserAuthMap(Collection<String> userIds) {
        Map<String, String> userAuthMap = new HashMap<>();
        userAuthRepository.findAllById(userIds).forEach(x -> userAuthMap.put(x.getId(), x.getUsername()));
        return userAuthMap;
    }
    
    private Map<String, UserInfoDTO> getUserProfileMap(Collection<String> userIds) {
        Map<String, UserInfoDTO> userProfileMap = new HashMap<>();
        userProfileRepository.findAllById(userIds).forEach(x -> {
            UserInfoDTO userInfo = new UserInfoDTO();
            userInfo.setId(x.getId());
            userInfo.setFirstName(x.getFirstName());
            userInfo.setLastName(x.getLastName());
            userProfileMap.put(x.getId(), userInfo);
        });
        return userProfileMap;
    }
}