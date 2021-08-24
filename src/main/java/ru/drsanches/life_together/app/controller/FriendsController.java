package ru.drsanches.life_together.app.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.drsanches.life_together.app.data.profile.dto.UserInfoDTO;
import ru.drsanches.life_together.app.service.web.FriendsWebService;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/friends")
@Api(description = "friend requests and friends list operations")
public class FriendsController {

    @Autowired
    private FriendsWebService friendsWebService;

    @RequestMapping(path = "", method = RequestMethod.GET)
    @ApiOperation(value = "Returns a list of friends information")
    public List<UserInfoDTO> getFriends(@RequestHeader("Authorization") String token) {
        return friendsWebService.getFriends(token);
    }

    @RequestMapping(path = "/requests/incoming", method = RequestMethod.GET)
    @ApiOperation(value = "Returns a list of information about users from whom a friend request was received")
    public List<UserInfoDTO> getIncomingRequests(@RequestHeader("Authorization") String token) {
        return friendsWebService.getIncomingRequests(token);
    }

    @RequestMapping(path = "/requests/outgoing", method = RequestMethod.GET)
    @ApiOperation(value = "Returns a list of information about users to whom a friend request was sent")
    public List<UserInfoDTO> getOutgoingRequests(@RequestHeader("Authorization") String token) {
        return friendsWebService.getOutgoingRequests(token);
    }

    @RequestMapping(path = "/manage/{userId}", method = RequestMethod.POST)
    @ApiOperation(value = "Sends a friend request or confirms of another user's request")
    @ResponseStatus(HttpStatus.CREATED)
    public void sendRequest(@RequestHeader("Authorization") String token, @PathVariable String userId) {
        friendsWebService.sendRequest(token, userId);
    }

    @RequestMapping(path = "/manage/{userId}", method = RequestMethod.DELETE)
    @ApiOperation(value = "Cancels the friend request from the current or to another user or removes user from friends")
    public void removeRequest(@RequestHeader("Authorization") String token, @PathVariable String userId) {
        friendsWebService.removeRequest(token, userId);
    }
}