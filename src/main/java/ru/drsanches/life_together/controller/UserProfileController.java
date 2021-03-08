package ru.drsanches.life_together.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import ru.drsanches.life_together.data.profile.dto.ChangeUserProfileDTO;
import ru.drsanches.life_together.data.profile.dto.UserInfoDTO;
import ru.drsanches.life_together.service.controller.UserProfileService;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@RequestMapping(value = "/profile")
@Api(description = "viewing and editing public user profiles")
public class UserProfileController {

    private final Logger LOG = LoggerFactory.getLogger(UserProfileController.class);

    @Autowired
    private UserProfileService userProfileService;

    @RequestMapping(value = "", method = RequestMethod.GET)
    @ApiOperation(value = "Returns current user profile information")
    @ApiImplicitParam(name = "Token", value = "Access token", paramType = "header", required = true)
    public UserInfoDTO getCurrentProfile(@ApiIgnore OAuth2Authentication authentication) {
        return userProfileService.getProfile(authentication);
    }

    @RequestMapping(value = "", method = RequestMethod.PUT)
    @ApiOperation(value = "Sets new profile data for current user")
    @ApiImplicitParam(name = "Token", value = "Access token", paramType = "header", required = true)
    public void changeCurrentProfile(@ApiIgnore OAuth2Authentication authentication, @RequestBody ChangeUserProfileDTO changeUserProfileDTO) {
        userProfileService.changeCurrentProfile(authentication, changeUserProfileDTO);
    }

    @RequestMapping(value = "/search/{username}", method = RequestMethod.GET)
    @ApiOperation(value = "Returns another user profile information by username")
    @ApiImplicitParam(name = "Token", value = "Access token", paramType = "header", required = true)
    public UserInfoDTO searchProfile(@PathVariable String username) {
        return userProfileService.searchProfile(username);
    }

    @RequestMapping(value = "/{userId}", method = RequestMethod.GET)
    @ApiOperation(value = "Returns another user profile information by id")
    @ApiImplicitParam(name = "Token", value = "Access token", paramType = "header", required = true)
    public UserInfoDTO getProfile(@PathVariable String userId) {
        return userProfileService.getProfile(userId);
    }
}