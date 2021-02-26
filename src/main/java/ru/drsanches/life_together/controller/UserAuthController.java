package ru.drsanches.life_together.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
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
import ru.drsanches.life_together.data.auth.dto.UserAuthInfoDTO;
import ru.drsanches.life_together.exception.ApplicationException;
import ru.drsanches.life_together.exception.ServerError;
import ru.drsanches.life_together.service.controller.UserAuthService;
import java.security.Principal;

@RestController
@RequestMapping(value = "/auth")
public class UserAuthController {

    private final Logger LOG = LoggerFactory.getLogger(UserAuthController.class);

    @Autowired
    private UserAuthService userAuthService;

    @RequestMapping(value = "/registration", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public void registration(@RequestBody RegistrationDTO registrationDTO) {
        userAuthService.registration(registrationDTO);
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseEntity<OAuth2AccessToken> login(@RequestBody LoginDTO loginDTO) {
        return userAuthService.login(loginDTO);
    }

    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public UserAuthInfoDTO info(Principal principal) {
        return userAuthService.info(principal.getName());
    }

    @RequestMapping(value = "/changeUsername", method = RequestMethod.PUT)
    public void changeUsername(Principal principal, @RequestBody ChangeUsernameDTO changeUsernameDTO) {
        userAuthService.changeUsername(principal.getName(), changeUsernameDTO);
    }

    @RequestMapping(value = "/changePassword", method = RequestMethod.PUT)
    public void changePassword(Principal principal, @RequestBody ChangePasswordDTO changePasswordDTO) {
        userAuthService.changePassword(principal.getName(), changePasswordDTO);
    }

    @RequestMapping(value = "/changeEmail", method = RequestMethod.PUT)
    public void changeEmail(Principal principal, @RequestBody ChangeEmailDTO changeEmailDTO) {
        userAuthService.changeEmail(principal.getName(), changeEmailDTO);
    }

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public void logout(Principal principal) {
        userAuthService.logout(principal.getName());
    }

    @RequestMapping(value = "/deleteUser", method = RequestMethod.POST)
    public void deleteUser(Principal principal, @RequestBody DeleteUserDTO deleteUserDTO) {
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