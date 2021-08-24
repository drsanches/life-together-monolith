package ru.drsanches.life_together.auth.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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
    public UserAuthInfoDTO info(@RequestHeader("Authorization") String token) {
        return userAuthWebService.info(token);
    }

    @RequestMapping(value = "/changeUsername", method = RequestMethod.PUT)
    @ApiOperation(value = "Changes username")
    public void changeUsername(@RequestHeader("Authorization") String token, @RequestBody ChangeUsernameDTO changeUsernameDTO) {
        userAuthWebService.changeUsername(token, changeUsernameDTO);
    }

    @RequestMapping(value = "/changePassword", method = RequestMethod.PUT)
    @ApiOperation(value = "Changes password")
    public void changePassword(@RequestHeader("Authorization") String token, @RequestBody ChangePasswordDTO changePasswordDTO) {
        userAuthWebService.changePassword(token, changePasswordDTO);
    }

    @RequestMapping(value = "/changeEmail", method = RequestMethod.PUT)
    @ApiOperation(value = "Changes email")
    public void changeEmail(@RequestHeader("Authorization") String token, @RequestBody ChangeEmailDTO changeEmailDTO) {
        userAuthWebService.changeEmail(token, changeEmailDTO);
    }

    @RequestMapping(value = "/refreshToken", method = RequestMethod.GET)
    @ApiOperation(value = "Returns new authorization token")
    public TokenDTO refreshToken(@RequestHeader("Authorization") String refreshToken) {
        return userAuthWebService.refreshToken(refreshToken);
    }

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    @ApiOperation(value = "Logs out of the current user, old tokens become invalid")
    public void logout(@RequestHeader("Authorization") String token) {
        userAuthWebService.logout(token);
    }

    @RequestMapping(value = "/deleteUser", method = RequestMethod.POST)
    @ApiOperation(value = "Deletes current user account")
    public void disableUser(@RequestHeader("Authorization") String token, @RequestBody DeleteUserDTO deleteUserDTO) {
        userAuthWebService.disableUser(token, deleteUserDTO);
    }
}