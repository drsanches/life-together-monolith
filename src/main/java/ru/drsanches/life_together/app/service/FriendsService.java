package ru.drsanches.life_together.app.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import ru.drsanches.life_together.app.data.friends.model.FriendRequest;
import ru.drsanches.life_together.app.data.friends.model.FriendRequestKey;
import ru.drsanches.life_together.app.data.profile.dto.UserInfoDTO;
import ru.drsanches.life_together.exception.ApplicationException;
import ru.drsanches.life_together.exception.NoUserIdException;
import ru.drsanches.life_together.app.data.repository.FriendRequestRepository;
import ru.drsanches.life_together.integration.UserInfoService;
import ru.drsanches.life_together.integration.token.TokenService;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class FriendsService {

    private final Logger LOG = LoggerFactory.getLogger(FriendsService.class);

    @Autowired
    private FriendRequestRepository friendRequestRepository;

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private TokenService tokenService;

    public Set<UserInfoDTO> getFriends(String token) {
        String userId = tokenService.getUserId(token);
        Set<String> outgoing = getOutgoingRequestIds(userId);
        Set<String> incoming = getIncomingRequestIds(userId);
        Set<String> friends = incoming.stream()
                .filter(outgoing::contains)
                .collect(Collectors.toSet());
        return userInfoService.getUserInfoSet(friends);
    }

    public Set<UserInfoDTO> getIncomingRequests(String token) {
        String userId = tokenService.getUserId(token);
        Set<String> outgoing = getOutgoingRequestIds(userId);
        Set<String> incoming = getIncomingRequestIds(userId);
        Set<String> friends = incoming.stream()
                .filter(x -> !outgoing.contains(x))
                .collect(Collectors.toSet());
        return userInfoService.getUserInfoSet(friends);
    }

    public Set<UserInfoDTO> getOutgoingRequests(String token) {
        String userId = tokenService.getUserId(token);
        Set<String> outgoing = getOutgoingRequestIds(userId);
        Set<String> incoming = getIncomingRequestIds(userId);
        Set<String> friends = outgoing.stream()
                .filter(x -> !incoming.contains(x))
                .collect(Collectors.toSet());
        return userInfoService.getUserInfoSet(friends);
    }

    public void sendRequest(String token, String toUserId) {
        String fromUserId = tokenService.getUserId(token);
        if (!userInfoService.userProfileEnabled(toUserId)) {
            throw new NoUserIdException(toUserId);
        }
        if (fromUserId.equals(toUserId)) {
            throw new ApplicationException("You can't send yourself a friend request");
        }
        FriendRequest friendRequest = new FriendRequest(fromUserId, toUserId);
        try {
            friendRequestRepository.save(friendRequest);
            LOG.info("New friend request has been added: {}", friendRequest.toString());
        } catch(DataIntegrityViolationException e) {
            //TODO: Never uses
            LOG.warn("Friend request already exists: " + friendRequest.toString(),  e);
        }
    }

    public void removeRequest(String token, String userId) {
        String currentUserId = tokenService.getUserId(token);
        if (!userInfoService.userAuthExists(userId)) {
            throw new NoUserIdException(userId);
        }
        if (currentUserId.equals(userId)) {
            LOG.warn("currentUserId and userId is equal");
            return;
        }
        FriendRequestKey friendRequestKey1 = new FriendRequestKey(currentUserId, userId);
        try {
            friendRequestRepository.deleteById(friendRequestKey1);
            LOG.info("Friend request has been removed: {}", friendRequestKey1.toString());
        } catch(EmptyResultDataAccessException e) {
            LOG.warn("Friend request does not exist: " + friendRequestKey1.toString(), e);
        }
        FriendRequestKey reversedFriendRequestKey = new FriendRequestKey(userId, currentUserId);
        try {
            friendRequestRepository.deleteById(reversedFriendRequestKey);
            LOG.info("Reversed friend request has been removed: {}", reversedFriendRequestKey.toString());
        } catch(EmptyResultDataAccessException e) {
            LOG.info("Reversed friend request does not exist: " + reversedFriendRequestKey.toString(), e);
        }
    }

    private Set<String> getOutgoingRequestIds(String userId) {
        return friendRequestRepository.findByIdFromUserId(userId).stream()
                .map(FriendRequest::getToUser)
                .collect(Collectors.toSet());
    }

    private Set<String> getIncomingRequestIds(String userId) {
        return friendRequestRepository.findByIdToUserId(userId).stream()
                .map(FriendRequest::getFromUserId)
                .collect(Collectors.toSet());
    }
}