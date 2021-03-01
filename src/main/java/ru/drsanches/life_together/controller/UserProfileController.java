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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.drsanches.life_together.data.profile.dto.ChangeUserProfileDTO;
import ru.drsanches.life_together.data.profile.dto.UserInfoDTO;
import ru.drsanches.life_together.exception.ApplicationException;
import ru.drsanches.life_together.exception.ServerError;
import ru.drsanches.life_together.service.controller.UserProfileService;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@RequestMapping(value = "/profile")
@Api(description = "viewing and editing public user profiles")
public class UserProfileController {

    private final Logger LOG = LoggerFactory.getLogger(UserProfileController.class);

    @Autowired
    private UserProfileService userProfileService;

    @ApiOperation(value = "Returns current user profile information")
    @ApiImplicitParam(name = "Token", value = "Access token", paramType = "header", required = true)
    @RequestMapping(value = "", method = RequestMethod.GET)
    public UserInfoDTO getCurrentProfile(@ApiIgnore OAuth2Authentication authentication) {
        return userProfileService.getProfile(authentication);
    }

    @ApiOperation(value = "Changes current user profile")
    @ApiImplicitParam(name = "Token", value = "Access token", paramType = "header", required = true)
    @RequestMapping(value = "", method = RequestMethod.PUT)
    public void changeCurrentProfile(@ApiIgnore OAuth2Authentication authentication, @RequestBody ChangeUserProfileDTO changeUserProfileDTO) {
        userProfileService.changeCurrentProfile(authentication, changeUserProfileDTO);
    }

    @ApiOperation(value = "Returns another user profile information")
    @ApiImplicitParam(name = "Token", value = "Access token", paramType = "header", required = true)
    @RequestMapping(value = "/{username}", method = RequestMethod.GET)
    public UserInfoDTO getProfile(@PathVariable String username) {
        return userProfileService.getProfile(username);
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