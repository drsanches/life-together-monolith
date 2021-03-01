package ru.drsanches.life_together.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
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
import ru.drsanches.life_together.exception.ApplicationException;
import ru.drsanches.life_together.exception.ServerError;
import ru.drsanches.life_together.service.controller.UserAuthService;
import springfox.documentation.annotations.ApiIgnore;
import java.security.Principal;

@RestController
@RequestMapping(value = "/auth")
@Api(description = "authentication data management")
public class UserAuthController {

    private final Logger LOG = LoggerFactory.getLogger(UserAuthController.class);

    @Autowired
    private UserAuthService userAuthService;

    @ApiOperation(value = "Registers new user account")
    @RequestMapping(value = "/registration", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public void registration(@RequestBody RegistrationDTO registrationDTO) {
        userAuthService.registration(registrationDTO);
    }

    @ApiOperation(value = "Returns authorization tokens")
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public TokenDTO login(@RequestBody LoginDTO loginDTO) {
        return userAuthService.login(loginDTO);
    }

    @ApiOperation(value = "Returns current user private information")
    @ApiImplicitParam(name = "Token", value = "Access token", paramType = "header", required = true)
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public UserAuthInfoDTO info(@ApiIgnore Principal principal) {
        return userAuthService.info(principal.getName());
    }

    @ApiOperation(value = "Changes username")
    @ApiImplicitParam(name = "Token", value = "Access token", paramType = "header", required = true)
    @RequestMapping(value = "/changeUsername", method = RequestMethod.PUT)
    public void changeUsername(@ApiIgnore Principal principal, @RequestBody ChangeUsernameDTO changeUsernameDTO) {
        userAuthService.changeUsername(principal.getName(), changeUsernameDTO);
    }

    @ApiOperation(value = "Changes password")
    @ApiImplicitParam(name = "Token", value = "Access token", paramType = "header", required = true)
    @RequestMapping(value = "/changePassword", method = RequestMethod.PUT)
    public void changePassword(@ApiIgnore Principal principal, @RequestBody ChangePasswordDTO changePasswordDTO) {
        userAuthService.changePassword(principal.getName(), changePasswordDTO);
    }

    @ApiOperation(value = "Changes email")
    @ApiImplicitParam(name = "Token", value = "Access token", paramType = "header", required = true)
    @RequestMapping(value = "/changeEmail", method = RequestMethod.PUT)
    public void changeEmail(@ApiIgnore Principal principal, @RequestBody ChangeEmailDTO changeEmailDTO) {
        userAuthService.changeEmail(principal.getName(), changeEmailDTO);
    }

    //TODO: Add refresh token endpoint

    @ApiOperation(value = "Logs out of the current user, old tokens become invalid")
    @ApiImplicitParam(name = "Token", value = "Access token", paramType = "header", required = true)
    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public void logout(@ApiIgnore Principal principal) {
        userAuthService.logout(principal.getName());
    }

    @ApiOperation(value = "Deletes current user account")
    @ApiImplicitParam(name = "Token", value = "Access token", paramType = "header", required = true)
    @RequestMapping(value = "/deleteUser", method = RequestMethod.POST)
    public void deleteUser(@ApiIgnore Principal principal, @RequestBody DeleteUserDTO deleteUserDTO) {
        userAuthService.deleteUser(principal.getName(), deleteUserDTO);
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