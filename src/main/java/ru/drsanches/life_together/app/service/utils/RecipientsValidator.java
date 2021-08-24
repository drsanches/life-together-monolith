package ru.drsanches.life_together.app.service.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.drsanches.life_together.app.data.profile.model.UserProfile;
import ru.drsanches.life_together.app.service.domain.FriendsDomainService;
import ru.drsanches.life_together.app.service.domain.UserProfileDomainService;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RecipientsValidator {

    @Autowired
    private UserProfileDomainService userProfileDomainService;

    @Autowired
    private FriendsDomainService friendsDomainService;

    public Set<String> getWrongIds(String fromUserId, Set<String> toUserIds) {
        List<String> toFriends = friendsDomainService.getFriendsIdList(fromUserId);
        List<String> existingFriends = userProfileDomainService.getAllByIds(toFriends).stream()
                .filter(UserProfile::isEnabled)
                .map(UserProfile::getId)
                .collect(Collectors.toList());
        return toUserIds.stream()
                .filter(id -> !existingFriends.contains(id) && !id.equals(fromUserId))
                .collect(Collectors.toSet());
    }
}