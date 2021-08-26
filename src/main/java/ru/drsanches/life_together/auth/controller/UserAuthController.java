package ru.drsanches.life_together.auth.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.drsanches.life_together.auth.data.dto.ChangeEmailDTO;
import ru.drsanches.life_together.auth.data.dto.ChangePasswordDTO;
import ru.drsanches.life_together.auth.data.dto.ChangeUsernameDTO;
import ru.drsanches.life_together.auth.data.dto.DeleteUserDTO;
import ru.drsanches.life_together.auth.data.dto.LoginDTO;
import ru.drsanches.life_together.auth.data.dto.RegistrationDTO;
import ru.drsanches.life_together.auth.data.dto.TokenDTO;
import ru.drsanches.life_together.auth.data.dto.UserAuthInfoDTO;
import ru.drsanches.life_together.auth.service.UserAuthWebService;

@RestController
@RequestMapping(value = "/api/v1/auth")
@Api(description = "authentication data management")
public class UserAuthController {

    @Autowired
    private UserAuthWebService userAuthWebService;

    @RequestMapping(value = "/registration", method = RequestMethod.POST)
    @ApiOperation(value = "Registers new user account and returns user information")
    @ResponseStatus(HttpStatus.CREATED)
    public UserAuthInfoDTO registration(@RequestBody RegistrationDTO registrationDTO) {
        return userAuthWebService.registration(registrationDTO);
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ApiOperation(value = "Returns authorization tokens")
    public TokenDTO login(@RequestBody LoginDTO loginDTO) {
        return userAuthWebService.login(loginDTO);
    }

    @RequestMapping(value = "/info", method = RequestMethod.GET)
    @ApiOperation(value = "Returns current user private information")
    @ApiImplicitParam(name = "Authorization", value = "Access token", paramType = "header", required = true)
    public UserAuthInfoDTO info() {
        return userAuthWebService.info();
    }

    @RequestMapping(value = "/changeUsername", method = RequestMethod.PUT)
    @ApiOperation(value = "Changes username")
    @ApiImplicitParam(name = "Authorization", value = "Access token", paramType = "header", required = true)
    public void changeUsername(@RequestBody ChangeUsernameDTO changeUsernameDTO) {
        userAuthWebService.changeUsername(changeUsernameDTO);
    }

    @RequestMapping(value = "/changePassword", method = RequestMethod.PUT)
    @ApiOperation(value = "Changes password")
    @ApiImplicitParam(name = "Authorization", value = "Access token", paramType = "header", required = true)
    public void changePassword(@RequestBody ChangePasswordDTO changePasswordDTO) {
        userAuthWebService.changePassword(changePasswordDTO);
    }

    @RequestMapping(value = "/changeEmail", method = RequestMethod.PUT)
    @ApiOperation(value = "Changes email")
    @ApiImplicitParam(name = "Authorization", value = "Access token", paramType = "header", required = true)
    public void changeEmail(@RequestBody ChangeEmailDTO changeEmailDTO) {
        userAuthWebService.changeEmail(changeEmailDTO);
    }

    @RequestMapping(value = "/refreshToken", method = RequestMethod.GET)
    @ApiOperation(value = "Returns new authorization token")
    public TokenDTO refreshToken(@RequestHeader("Authorization")
                                 @ApiParam(value = "Refresh token", required = true) String refreshToken) {
        return userAuthWebService.refreshToken(refreshToken);
    }

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    @ApiOperation(value = "Logs out of the current user, old tokens become invalid")
    @ApiImplicitParam(name = "Authorization", value = "Access token", paramType = "header", required = true)
    public void logout() {
        userAuthWebService.logout();
    }

    @RequestMapping(value = "/deleteUser", method = RequestMethod.POST)
    @ApiOperation(value = "Deletes current user account")
    @ApiImplicitParam(name = "Authorization", value = "Access token", paramType = "header", required = true)
    public void disableUser(@RequestBody DeleteUserDTO deleteUserDTO) {
        userAuthWebService.disableUser(deleteUserDTO);
    }
}