package ru.drsanches.life_together.app.service.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.drsanches.life_together.app.data.friends.dto.RemoveRequestDTO;
import ru.drsanches.life_together.app.data.friends.dto.SendRequestDTO;
import ru.drsanches.life_together.app.data.profile.dto.UserInfoDTO;
import ru.drsanches.life_together.app.data.profile.mapper.UserInfoMapper;
import ru.drsanches.life_together.app.service.domain.FriendsDomainService;
import ru.drsanches.life_together.app.service.domain.UserProfileDomainService;
import ru.drsanches.life_together.common.token.TokenSupplier;
import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Validated
public class FriendsWebService {

    private final Logger LOG = LoggerFactory.getLogger(FriendsWebService.class);

    @Autowired
    private FriendsDomainService friendsDomainService;

    @Autowired
    private UserProfileDomainService userProfileDomainService;

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Autowired
    private TokenSupplier tokenSupplier;

    public List<UserInfoDTO> getFriends() {
        String userId = tokenSupplier.get().getUserId();
        List<String> friends = friendsDomainService.getFriendsIdList(userId);
        return userProfileDomainService.getAllByIds(friends).stream()
                .map(userInfoMapper::convert)
                .collect(Collectors.toList());
    }

    public List<UserInfoDTO> getIncomingRequests() {
        String userId = tokenSupplier.get().getUserId();
        List<String> incoming = friendsDomainService.getIncomingRequestIdList(userId);
        return userProfileDomainService.getAllByIds(incoming).stream()
                .map(userInfoMapper::convert)
                .collect(Collectors.toList());
    }

    public List<UserInfoDTO> getOutgoingRequests() {
        String userId = tokenSupplier.get().getUserId();
        List<String> outgoing = friendsDomainService.getOutgoingRequestIdList(userId);
        return userProfileDomainService.getAllByIds(outgoing).stream()
                .map(userInfoMapper::convert)
                .collect(Collectors.toList());
    }

    public void sendRequest(@Valid SendRequestDTO sendRequestDTO) {
        String fromUserId = tokenSupplier.get().getUserId();
        friendsDomainService.saveFriendRequest(fromUserId, sendRequestDTO.getUserId());
        LOG.info("User with id '{}' send friend request to user '{}'", fromUserId, sendRequestDTO.getUserId());
    }

    public void removeRequest(@Valid RemoveRequestDTO removeRequestDTO) {
        String currentUserId = tokenSupplier.get().getUserId();
        friendsDomainService.removeFriendRequest(currentUserId, removeRequestDTO.getUserId());
        LOG.info("User with id '{}' canceled friendship for user '{}'", currentUserId, removeRequestDTO.getUserId());
    }
}