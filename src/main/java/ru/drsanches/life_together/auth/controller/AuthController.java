package ru.drsanches.life_together.auth.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
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
import ru.drsanches.life_together.auth.data.user.Role;
import ru.drsanches.life_together.auth.data.user.UserAuth;
import ru.drsanches.life_together.auth.data.user.UserAuthRepository;
import java.util.UUID;

@RestController
@RequestMapping(value = "/auth")
public class AuthController {

    private final Logger LOG = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private UserAuthRepository userAuthRepository;

    @RequestMapping(value = "/registration", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public void registration(@RequestBody RegistrationDTO registrationDTO) {
        userAuthRepository.save(new UserAuth(
                UUID.randomUUID().toString(),
                registrationDTO.getUsername(),
                registrationDTO.getPassword(),
                registrationDTO.getEmail(),
                true,
                Role.USER
        ));
    }

    @RequestMapping(value = "/testCreate", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.CREATED)
    public void testCreate() {
        userAuthRepository.save(new UserAuth(
                UUID.randomUUID().toString(),
                "username",
                "password",
                "email",
                true,
                Role.USER
        ));
    }

    @RequestMapping(value = "test/{username}", method = RequestMethod.GET)
    public UserAuth test(@PathVariable String username) {
        return userAuthRepository.findByUsername(username).get();
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public void login(@RequestBody LoginDTO loginDTO) {}

    @RequestMapping(value = "/changeUsername", method = RequestMethod.PUT)
    public void changeUsername(@RequestBody ChangeUsernameDTO changeUsernameDTO) {}

    @RequestMapping(value = "/changePassword", method = RequestMethod.PUT)
    public void changePassword(@RequestBody ChangePasswordDTO changePasswordDTO) {}

    @RequestMapping(value = "/changeEmail", method = RequestMethod.PUT)
    public void changeEmail(@RequestBody ChangeEmailDTO changeEmailDTO) {}

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public void logout() {}

    @RequestMapping(value = "/deleteUser", method = RequestMethod.DELETE)
    public void deleteUser(@RequestBody DeleteUserDTO deleteUserDTO) {}
}