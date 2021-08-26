package ru.drsanches.life_together.app.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import ru.drsanches.life_together.app.data.profile.dto.ChangeUserProfileDTO;
import ru.drsanches.life_together.app.data.profile.dto.UserInfoDTO;
import ru.drsanches.life_together.app.service.web.UserProfileWebService;

@RestController
@RequestMapping(value = "/api/v1/profile")
@Api(description = "viewing and editing public user profiles")
public class UserProfileController {

    @Autowired
    private UserProfileWebService userProfileWebService;

    @RequestMapping(value = "", method = RequestMethod.GET)
    @ApiOperation(value = "Returns current user profile information")
    @ApiImplicitParam(name = "Authorization", value = "Access token", paramType = "header", required = true)
    public UserInfoDTO getCurrentProfile() {
        return userProfileWebService.getCurrentProfile();
    }

    @RequestMapping(value = "", method = RequestMethod.PUT)
    @ApiOperation(value = "Sets new profile data for current user")
    @ApiImplicitParam(name = "Authorization", value = "Access token", paramType = "header", required = true)
    public void changeCurrentProfile(@RequestBody ChangeUserProfileDTO changeUserProfileDTO) {
        userProfileWebService.changeCurrentProfile(changeUserProfileDTO);
    }

    @RequestMapping(value = "/search/{username}", method = RequestMethod.GET)
    @ApiOperation(value = "Returns another user profile information by username")
    @ApiImplicitParam(name = "Authorization", value = "Access token", paramType = "header", required = true)
    public UserInfoDTO searchProfile(@PathVariable String username) {
        return userProfileWebService.searchProfile(username);
    }

    @RequestMapping(value = "/{userId}", method = RequestMethod.GET)
    @ApiOperation(value = "Returns another user profile information by id")
    @ApiImplicitParam(name = "Authorization", value = "Access token", paramType = "header", required = true)
    public UserInfoDTO getProfile(@PathVariable String userId) {
        return userProfileWebService.getProfile(userId);
    }
}