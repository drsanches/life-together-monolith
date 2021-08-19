package ru.drsanches.life_together.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.drsanches.life_together.data.auth.dto.ChangeEmailDTO;
import ru.drsanches.life_together.data.auth.dto.ChangePasswordDTO;
import ru.drsanches.life_together.data.auth.dto.ChangeUsernameDTO;
import ru.drsanches.life_together.data.auth.dto.DeleteUserDTO;
import ru.drsanches.life_together.data.auth.dto.LoginDTO;
import ru.drsanches.life_together.data.auth.dto.RegistrationDTO;
import ru.drsanches.life_together.data.auth.dto.TokenDTO;
import ru.drsanches.life_together.data.auth.dto.UserAuthInfoDTO;
import ru.drsanches.life_together.service.controller.UserAuthService;

@RestController
@RequestMapping(value = "/auth")
@Api(description = "authentication data management")
public class UserAuthController {

    private final Logger LOG = LoggerFactory.getLogger(UserAuthController.class);

    @Autowired
    private UserAuthService userAuthService;

    @RequestMapping(value = "/registration", method = RequestMethod.POST)
    @ApiOperation(value = "Registers new user account and returns user information")
    @ResponseStatus(HttpStatus.CREATED)
    public UserAuthInfoDTO registration(@RequestBody RegistrationDTO registrationDTO) {
        return userAuthService.registration(registrationDTO);
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ApiOperation(value = "Returns authorization tokens")
    public TokenDTO login(@RequestBody LoginDTO loginDTO) {
        return userAuthService.login(loginDTO);
    }

    @RequestMapping(value = "/info", method = RequestMethod.GET)
    @ApiOperation(value = "Returns current user private information")
    public UserAuthInfoDTO info(@RequestHeader("Authorization") String token) {
        return userAuthService.info(token);
    }

    @RequestMapping(value = "/changeUsername", method = RequestMethod.PUT)
    @ApiOperation(value = "Changes username")
    public void changeUsername(@RequestHeader("Authorization") String token, @RequestBody ChangeUsernameDTO changeUsernameDTO) {
        userAuthService.changeUsername(token, changeUsernameDTO);
    }

    @RequestMapping(value = "/changePassword", method = RequestMethod.PUT)
    @ApiOperation(value = "Changes password")
    public void changePassword(@RequestHeader("Authorization") String token, @RequestBody ChangePasswordDTO changePasswordDTO) {
        userAuthService.changePassword(token, changePasswordDTO);
    }

    @RequestMapping(value = "/changeEmail", method = RequestMethod.PUT)
    @ApiOperation(value = "Changes email")
    public void changeEmail(@RequestHeader("Authorization") String token, @RequestBody ChangeEmailDTO changeEmailDTO) {
        userAuthService.changeEmail(token, changeEmailDTO);
    }

    @RequestMapping(value = "/refreshToken", method = RequestMethod.GET)
    @ApiOperation(value = "Returns new authorization token")
    public TokenDTO refreshToken(@RequestHeader("Authorization") String refreshToken) {
        return userAuthService.refreshToken(refreshToken);
    }

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    @ApiOperation(value = "Logs out of the current user, old tokens become invalid")
    public void logout(@RequestHeader("Authorization") String token) {
        userAuthService.logout(token);
    }

    @RequestMapping(value = "/deleteUser", method = RequestMethod.POST)
    @ApiOperation(value = "Deletes current user account")
    public void deleteUser(@RequestHeader("Authorization") String token, @RequestBody DeleteUserDTO deleteUserDTO) {
        userAuthService.deleteUser(token, deleteUserDTO);
    }
}