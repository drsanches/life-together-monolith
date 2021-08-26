package ru.drsanches.life_together.app.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
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
    @ApiImplicitParam(name = "Authorization", value = "Access token", paramType = "header", required = true)
    public List<UserInfoDTO> getFriends() {
        return friendsWebService.getFriends();
    }

    @RequestMapping(path = "/requests/incoming", method = RequestMethod.GET)
    @ApiOperation(value = "Returns a list of information about users from whom a friend request was received")
    @ApiImplicitParam(name = "Authorization", value = "Access token", paramType = "header", required = true)
    public List<UserInfoDTO> getIncomingRequests() {
        return friendsWebService.getIncomingRequests();
    }

    @RequestMapping(path = "/requests/outgoing", method = RequestMethod.GET)
    @ApiOperation(value = "Returns a list of information about users to whom a friend request was sent")
    @ApiImplicitParam(name = "Authorization", value = "Access token", paramType = "header", required = true)
    public List<UserInfoDTO> getOutgoingRequests() {
        return friendsWebService.getOutgoingRequests();
    }

    @RequestMapping(path = "/manage/{userId}", method = RequestMethod.POST)
    @ApiOperation(value = "Sends a friend request or confirms of another user's request")
    @ApiImplicitParam(name = "Authorization", value = "Access token", paramType = "header", required = true)
    @ResponseStatus(HttpStatus.CREATED)
    public void sendRequest(@PathVariable String userId) {
        friendsWebService.sendRequest(userId);
    }

    @RequestMapping(path = "/manage/{userId}", method = RequestMethod.DELETE)
    @ApiOperation(value = "Cancels the friend request from the current or to another user or removes user from friends")
    @ApiImplicitParam(name = "Authorization", value = "Access token", paramType = "header", required = true)
    public void removeRequest(@PathVariable String userId) {
        friendsWebService.removeRequest(userId);
    }
}