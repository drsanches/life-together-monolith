package ru.drsanches.life_together.service.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Service;
import ru.drsanches.life_together.data.friends.FriendRequest;
import ru.drsanches.life_together.data.friends.FriendRequestKey;
import ru.drsanches.life_together.data.profile.dto.UserInfoDTO;
import ru.drsanches.life_together.exception.ApplicationException;
import ru.drsanches.life_together.exception.NoUserException;
import ru.drsanches.life_together.repository.FriendRequestRepository;
import ru.drsanches.life_together.service.utils.UserIdService;
import ru.drsanches.life_together.service.utils.UserInfoService;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class FriendsService {

    private final Logger LOG = LoggerFactory.getLogger(FriendsService.class);

    @Autowired
    private FriendRequestRepository friendRequestRepository;

    @Autowired
    private UserIdService userIdService;

    @Autowired
    private UserInfoService userInfoService;

    public Set<UserInfoDTO> getFriends(OAuth2Authentication authentication) {
        String userId = userIdService.getUserIdFromAuth(authentication);
        Set<String> outgoing = getOutgoingRequestIds(userId);
        Set<String> incoming = getIncomingRequestIds(userId);
        Set<String> friends = incoming.stream()
                .filter(outgoing::contains)
                .collect(Collectors.toSet());
        return userInfoService.getUserInfoSet(friends);
    }

    public Set<UserInfoDTO> getIncomingRequests(OAuth2Authentication authentication) {
        String userId = userIdService.getUserIdFromAuth(authentication);
        Set<String> outgoing = getOutgoingRequestIds(userId);
        Set<String> incoming = getIncomingRequestIds(userId);
        Set<String> friends = incoming.stream()
                .filter(x -> !outgoing.contains(x))
                .collect(Collectors.toSet());
        return userInfoService.getUserInfoSet(friends);
    }

    public Set<UserInfoDTO> getOutgoingRequests(OAuth2Authentication authentication) {
        String userId = userIdService.getUserIdFromAuth(authentication);
        Set<String> outgoing = getOutgoingRequestIds(userId);
        Set<String> incoming = getIncomingRequestIds(userId);
        Set<String> friends = outgoing.stream()
                .filter(x -> !incoming.contains(x))
                .collect(Collectors.toSet());
        return userInfoService.getUserInfoSet(friends);
    }

    public void sendRequest(OAuth2Authentication authentication, String username) {
        String fromUserId = userIdService.getUserIdFromAuth(authentication);
        String toUserId = userIdService.getUserIdFromDB(username);
        if (toUserId == null) {
            throw new NoUserException(username);
        }
        if (fromUserId.equals(toUserId)) {
            throw new ApplicationException("You can't send yourself a friend request");
        }
        FriendRequest friendRequest = new FriendRequest(fromUserId, toUserId);
        try {
            friendRequestRepository.save(friendRequest);
            LOG.info("New friend request has been added: {}", friendRequest.toString());
        } catch(DataIntegrityViolationException e) {
            LOG.warn("Friend request already exists: " + friendRequest.toString(),  e);
        }
    }

    public void removeRequest(OAuth2Authentication authentication, String username) {
        String fromUserId = userIdService.getUserIdFromAuth(authentication);
        String toUserId = userIdService.getUserIdFromDB(username);
        if (toUserId == null) {
            throw new NoUserException(username);
        }
        if (fromUserId.equals(toUserId)) {
            LOG.warn("fromUsername and toUsername is equal");
            return;
        }
        FriendRequestKey friendRequestKey1 = new FriendRequestKey(fromUserId, toUserId);
        try {
            friendRequestRepository.deleteById(friendRequestKey1);
            LOG.info("Friend request has been removed: {}", friendRequestKey1.toString());
        } catch(EmptyResultDataAccessException e) {
            LOG.warn("Friend request does not exist: " + friendRequestKey1.toString(), e);
        }
        FriendRequestKey reversedFriendRequestKey = new FriendRequestKey(toUserId, fromUserId);
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