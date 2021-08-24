package ru.drsanches.life_together.app.service.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.drsanches.life_together.app.data.friends.model.FriendRequest;
import ru.drsanches.life_together.app.data.friends.repository.FriendRequestRepository;
import ru.drsanches.life_together.app.data.profile.model.UserProfile;
import ru.drsanches.life_together.app.service.domain.UserProfileDomainService;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RecipientsValidator {

    @Autowired
    private UserProfileDomainService userProfileDomainService;

    @Autowired
    private FriendRequestRepository friendRequestRepository;

    public Set<String> getWrongIds(String fromUserId, Set<String> toUserIds) {
        Set<String> toFriends = getFriendIds(fromUserId).stream()
                .filter(toUserIds::contains).collect(Collectors.toSet());
        List<String> existingFriends = userProfileDomainService.getAllByIds(toFriends).stream()
                .filter(UserProfile::isEnabled)
                .map(UserProfile::getId)
                .collect(Collectors.toList());
        return toUserIds.stream()
                .filter(id -> !existingFriends.contains(id) && !id.equals(fromUserId))
                .collect(Collectors.toSet());
    }

    private Set<String> getFriendIds(String userId) {
        Set<String> outgoing = friendRequestRepository.findByIdFromUserId(userId).stream()
                .map(FriendRequest::getToUser).collect(Collectors.toSet());
        Set<String> incoming = friendRequestRepository.findByIdToUserId(userId).stream()
                .map(FriendRequest::getFromUserId).collect(Collectors.toSet());
        return incoming.stream().filter(outgoing::contains).collect(Collectors.toSet());
    }
}