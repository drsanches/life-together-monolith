package ru.drsanches.life_together.service.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.drsanches.life_together.data.friends.model.FriendRequest;
import ru.drsanches.life_together.repository.FriendRequestRepository;
import ru.drsanches.life_together.repository.UserProfileRepository;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RecipientsValidator {

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private FriendRequestRepository friendRequestRepository;

    @Autowired
    private UserIdService userIdService;

    public Set<String> getWrongIds(String fromUserId, Set<String> toUserIds) {
        Set<String> toFriends = getFriendIds(fromUserId).stream()
                .filter(toUserIds::contains).collect(Collectors.toSet());
        Set<String> existingFriends = new HashSet<>();
        userProfileRepository.findAllById(toFriends).forEach(userProfile -> existingFriends.add(userProfile.getId()));
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