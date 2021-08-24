package ru.drsanches.life_together.app.service.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.drsanches.life_together.app.data.profile.dto.UserInfoDTO;
import ru.drsanches.life_together.app.data.profile.mapper.UserInfoMapper;
import ru.drsanches.life_together.app.service.domain.FriendsDomainService;
import ru.drsanches.life_together.app.service.domain.UserProfileDomainService;
import ru.drsanches.life_together.exception.application.ApplicationException;
import ru.drsanches.life_together.exception.application.NoUserIdException;
import ru.drsanches.life_together.integration.token.TokenService;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FriendsWebService {

    private final Logger LOG = LoggerFactory.getLogger(FriendsWebService.class);

    @Autowired
    private FriendsDomainService friendsDomainService;

    @Autowired
    private UserProfileDomainService userProfileDomainService;

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Autowired
    private TokenService tokenService;

    public List<UserInfoDTO> getFriends(String token) {
        String userId = tokenService.getUserId(token);
        List<String> friends = friendsDomainService.getFriendsIdList(userId);
        return userProfileDomainService.getAllByIds(friends).stream()
                .map(userInfoMapper::convert)
                .collect(Collectors.toList());
    }

    public List<UserInfoDTO> getIncomingRequests(String token) {
        String userId = tokenService.getUserId(token);
        List<String> incoming = friendsDomainService.getIncomingRequestIdList(userId);
        return userProfileDomainService.getAllByIds(incoming).stream()
                .map(userInfoMapper::convert)
                .collect(Collectors.toList());
    }

    public List<UserInfoDTO> getOutgoingRequests(String token) {
        String userId = tokenService.getUserId(token);
        List<String> outgoing = friendsDomainService.getOutgoingRequestIdList(userId);
        return userProfileDomainService.getAllByIds(outgoing).stream()
                .map(userInfoMapper::convert)
                .collect(Collectors.toList());
    }

    public void sendRequest(String token, String toUserId) {
        String fromUserId = tokenService.getUserId(token);
        if (!userProfileDomainService.enabledExistsById(toUserId)) {
            throw new NoUserIdException(toUserId);
        }
        if (fromUserId.equals(toUserId)) {
            throw new ApplicationException("You can't send yourself a friend request");
        }
        friendsDomainService.saveFriendRequest(fromUserId, toUserId);
    }

    public void removeRequest(String token, String userId) {
        String currentUserId = tokenService.getUserId(token);
        if (!userProfileDomainService.anyExistsById(userId)) {
            throw new NoUserIdException(userId);
        }
        if (currentUserId.equals(userId)) {
            LOG.warn("currentUserId and userId is equal");
            return;
        }
        friendsDomainService.removeFriendRequest(currentUserId, userId);
    }
}