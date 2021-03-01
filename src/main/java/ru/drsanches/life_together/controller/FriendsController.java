package ru.drsanches.life_together.controller;

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
import java.security.Principal;
import java.util.Set;

@RestController
@RequestMapping(value = "/friends")
public class FriendsController {

    private final Logger LOG = LoggerFactory.getLogger(FriendsController.class);

    @Autowired
    private FriendsService friendsService;

    @RequestMapping(path = "", method = RequestMethod.GET)
    public Set<UserInfoDTO> getFriends(OAuth2Authentication authentication) {
        return friendsService.getFriends(authentication);
    }

    @RequestMapping(path = "/requests/incoming", method = RequestMethod.GET)
    public Set<UserInfoDTO> getIncomingRequests(OAuth2Authentication authentication) {
        return friendsService.getIncomingRequests(authentication);
    }

    @RequestMapping(path = "/requests/outgoing", method = RequestMethod.GET)
    public Set<UserInfoDTO> getOutgoingRequests(OAuth2Authentication authentication) {
        return friendsService.getOutgoingRequests(authentication);
    }

    @RequestMapping(path = "/manage/{username}", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public void sendRequest(OAuth2Authentication authentication, @PathVariable String username) {
        friendsService.sendRequest(authentication, username);
    }

    @RequestMapping(path = "/manage/{username}", method = RequestMethod.DELETE)
    public void removeRequest(OAuth2Authentication authentication, @PathVariable String username) {
        friendsService.removeRequest(authentication, username);
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