package ru.drsanches.life_together.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.drsanches.life_together.data.profile.dto.UserInfoDTO;
import ru.drsanches.life_together.exception.ApplicationException;
import ru.drsanches.life_together.exception.ServerError;
import ru.drsanches.life_together.service.controller.FriendsService;
import springfox.documentation.annotations.ApiIgnore;
import java.util.Set;

@RestController
@RequestMapping(value = "/friends")
@Api(description = "friend requests and friends list operations")
public class FriendsController {

    private final Logger LOG = LoggerFactory.getLogger(FriendsController.class);

    @Autowired
    private FriendsService friendsService;

    @ApiOperation(value = "Returns a list of friends information")
    @ApiImplicitParam(name = "Token", value = "Access token", paramType = "header", required = true)
    @RequestMapping(path = "", method = RequestMethod.GET)
    public Set<UserInfoDTO> getFriends(@ApiIgnore OAuth2Authentication authentication) {
        return friendsService.getFriends(authentication);
    }

    @ApiOperation(value = "Returns a list of information about users from whom a friend request was received")
    @ApiImplicitParam(name = "Token", value = "Access token", paramType = "header", required = true)
    @RequestMapping(path = "/requests/incoming", method = RequestMethod.GET)
    public Set<UserInfoDTO> getIncomingRequests(@ApiIgnore OAuth2Authentication authentication) {
        return friendsService.getIncomingRequests(authentication);
    }

    @ApiOperation(value = "Returns a list of information about users to whom a friend request was sent")
    @ApiImplicitParam(name = "Token", value = "Access token", paramType = "header", required = true)
    @RequestMapping(path = "/requests/outgoing", method = RequestMethod.GET)
    public Set<UserInfoDTO> getOutgoingRequests(@ApiIgnore OAuth2Authentication authentication) {
        return friendsService.getOutgoingRequests(authentication);
    }

    @ApiOperation(value = "Sends a friend request or confirms of another user's request")
    @ApiImplicitParam(name = "Token", value = "Access token", paramType = "header", required = true)
    @RequestMapping(path = "/manage/{userId}", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public void sendRequest(@ApiIgnore OAuth2Authentication authentication, @PathVariable String userId) {
        friendsService.sendRequest(authentication, userId);
    }

    @ApiOperation(value = "Cancels the friend request of the current or another user or removes user from friends")
    @ApiImplicitParam(name = "Token", value = "Access token", paramType = "header", required = true)
    @RequestMapping(path = "/manage/{userId}", method = RequestMethod.DELETE)
    public void removeRequest(@ApiIgnore OAuth2Authentication authentication, @PathVariable String userId) {
        friendsService.removeRequest(authentication, userId);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({ApplicationException.class})
    public String handleUserException(Exception e) {
        LOG.warn(e.getMessage(), e);
        return e.getMessage();
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler({ServerError.class})
    public String handleServerException(ServerError e) {
        LOG.error(e.getInfo(), e);
        return e.getMessage();
    }
}