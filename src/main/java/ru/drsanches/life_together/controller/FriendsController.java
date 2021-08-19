package ru.drsanches.life_together.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.drsanches.life_together.data.profile.dto.UserInfoDTO;
import ru.drsanches.life_together.service.controller.FriendsService;
import java.util.Set;

@RestController
@RequestMapping(value = "/api/v1/friends")
@Api(description = "friend requests and friends list operations")
public class FriendsController {

    private final Logger LOG = LoggerFactory.getLogger(FriendsController.class);

    @Autowired
    private FriendsService friendsService;

    @RequestMapping(path = "", method = RequestMethod.GET)
    @ApiOperation(value = "Returns a list of friends information")
    public Set<UserInfoDTO> getFriends(@RequestHeader("Authorization") String token) {
        return friendsService.getFriends(token);
    }

    @RequestMapping(path = "/requests/incoming", method = RequestMethod.GET)
    @ApiOperation(value = "Returns a list of information about users from whom a friend request was received")
    public Set<UserInfoDTO> getIncomingRequests(@RequestHeader("Authorization") String token) {
        return friendsService.getIncomingRequests(token);
    }

    @RequestMapping(path = "/requests/outgoing", method = RequestMethod.GET)
    @ApiOperation(value = "Returns a list of information about users to whom a friend request was sent")
    public Set<UserInfoDTO> getOutgoingRequests(@RequestHeader("Authorization") String token) {
        return friendsService.getOutgoingRequests(token);
    }

    @RequestMapping(path = "/manage/{userId}", method = RequestMethod.POST)
    @ApiOperation(value = "Sends a friend request or confirms of another user's request")
    @ResponseStatus(HttpStatus.CREATED)
    public void sendRequest(@RequestHeader("Authorization") String token, @PathVariable String userId) {
        friendsService.sendRequest(token, userId);
    }

    @RequestMapping(path = "/manage/{userId}", method = RequestMethod.DELETE)
    @ApiOperation(value = "Cancels the friend request of the current or another user or removes user from friends")
    public void removeRequest(@RequestHeader("Authorization") String token, @PathVariable String userId) {
        friendsService.removeRequest(token, userId);
    }
}