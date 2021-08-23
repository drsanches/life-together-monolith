package ru.drsanches.life_together.auth.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.drsanches.life_together.auth.data.dto.ChangeEmailDTO;
import ru.drsanches.life_together.auth.data.dto.ChangePasswordDTO;
import ru.drsanches.life_together.auth.data.dto.ChangeUsernameDTO;
import ru.drsanches.life_together.auth.data.dto.DeleteUserDTO;
import ru.drsanches.life_together.auth.data.dto.LoginDTO;
import ru.drsanches.life_together.auth.data.dto.RegistrationDTO;
import ru.drsanches.life_together.auth.data.dto.TokenDTO;
import ru.drsanches.life_together.auth.data.dto.UserAuthInfoDTO;
import ru.drsanches.life_together.auth.data.enumeration.Role;
import ru.drsanches.life_together.auth.data.model.UserAuth;
import ru.drsanches.life_together.integration.token.TokenMapper;
import ru.drsanches.life_together.exception.NoUserIdException;
import ru.drsanches.life_together.auth.data.repository.UserAuthRepository;
import ru.drsanches.life_together.exception.ApplicationException;
import ru.drsanches.life_together.integration.token.CredentialsHelper;
import ru.drsanches.life_together.integration.token.Token;
import ru.drsanches.life_together.integration.token.TokenService;
import ru.drsanches.life_together.integration.ProfileIntegrationService;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserAuthService {

    private final Logger LOG = LoggerFactory.getLogger(UserAuthService.class);

    @Autowired
    private UserAuthRepository userAuthRepository;

    @Autowired
    private ProfileIntegrationService profileIntegrationService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private CredentialsHelper credentialsHelper;

    @Autowired
    private TokenMapper tokenMapper;

    public UserAuthInfoDTO registration(RegistrationDTO registrationDTO) {
        registrationDTO.setUsername(registrationDTO.getUsername().toLowerCase());
        UserAuth userAuth = new UserAuth();
        userAuth.setId(UUID.randomUUID().toString());
        userAuth.setUsername(registrationDTO.getUsername());
        userAuth.setPassword(credentialsHelper.encodePassword(registrationDTO.getPassword()));
        userAuth.setEmail(registrationDTO.getEmail());
        userAuth.setEnabled(true);
        userAuth.setRole(Role.USER);
        profileIntegrationService.createUser(userAuth);
        LOG.info("New user has been created: {}", userAuth.toString());
        return new UserAuthInfoDTO(userAuth.getId(), userAuth.getUsername(), userAuth.getEmail());
    }

    public TokenDTO login(LoginDTO loginDTO) {
        loginDTO.setUsername(loginDTO.getUsername().toLowerCase());
        String userId = getUserId(loginDTO.getUsername());
        Token token = tokenService.createToken(userId, loginDTO.getUsername(), loginDTO.getPassword());
        return tokenMapper.convert(token);
    }

    public UserAuthInfoDTO info(String token) {
        String userId = tokenService.getUserId(token);
        UserAuth current = getUserByIdIfExists(userId);
        UserAuthInfoDTO userAuthInfoDTO = new UserAuthInfoDTO();
        userAuthInfoDTO.setId(current.getId());
        userAuthInfoDTO.setUsername(current.getUsername());
        userAuthInfoDTO.setEmail(current.getEmail());
        return userAuthInfoDTO;
    }

    public void changeUsername(String token, ChangeUsernameDTO changeUsernameDTO) {
        String userId = tokenService.getUserId(token);
        UserAuth current = getUserByIdIfExists(userId);
        credentialsHelper.checkPassword(changeUsernameDTO.getPassword(), current.getPassword());
        String oldUsername = current.getUsername();
        if (changeUsernameDTO.getNewUsername().equals(oldUsername)) {
            throw new ApplicationException("New username is equal to old");
        }
        current.setUsername(changeUsernameDTO.getNewUsername());
        userAuthRepository.save(current);
        logout(token);
        LOG.info("Username has been changed: {}. Old username: {}", current.toString(), oldUsername);
    }

    public void changePassword(String token, ChangePasswordDTO changePasswordDTO) {
        String userId = tokenService.getUserId(token);
        UserAuth current = getUserByIdIfExists(userId);
        credentialsHelper.checkPassword(changePasswordDTO.getOldPassword(), current.getPassword());
        if (changePasswordDTO.getOldPassword().equals(changePasswordDTO.getNewPassword())) {
            throw new ApplicationException("New password is equal to old");
        }
        current.setPassword(credentialsHelper.encodePassword(changePasswordDTO.getNewPassword()));
        userAuthRepository.save(current);
        logout(token);
        LOG.info("Password has been changed for user: {}", current.toString());
    }

    public void changeEmail(String token, ChangeEmailDTO changeEmailDTO) {
        String userId = tokenService.getUserId(token);
        UserAuth current = getUserByIdIfExists(userId);
        credentialsHelper.checkPassword(changeEmailDTO.getPassword(), current.getPassword());
        if (changeEmailDTO.getNewEmail().equals(current.getEmail())) {
            throw new ApplicationException("New email is equal to old");
        }
        current.setEmail(changeEmailDTO.getNewEmail());
        userAuthRepository.save(current);
        LOG.info("Email has been changed: {}", current.toString());
    }

    public TokenDTO refreshToken(String refreshToken) {
        return tokenMapper.convert(tokenService.refreshToken(refreshToken));
    }

    public void logout(String token) {
        tokenService.removeToken(token);
    }

    public void deleteUser(String token, DeleteUserDTO deleteUserDTO) {
        String userId = tokenService.getUserId(token);
        UserAuth current = getUserByIdIfExists(userId);
        credentialsHelper.checkPassword(deleteUserDTO.getPassword(), current.getPassword());
        logout(token);
        current.setEnabled(false);
        current.setUsername(UUID.randomUUID().toString() + "_" + current.getUsername());
        profileIntegrationService.disableUser(current);
        LOG.info("User has been disabled: {}", current.toString());
    }

    private UserAuth getUserByIdIfExists(String userId) {
        Optional<UserAuth> user = userAuthRepository.findById(userId);
        if (user.isEmpty() || !user.get().isEnabled()) {
            throw new NoUserIdException(userId);
        }
        return user.get();
    }

    private String getUserId(String username) {
        Optional<UserAuth> userAuth = userAuthRepository.findByUsername(username);
        if (userAuth.isEmpty() || !userAuth.get().isEnabled()) {
            LOG.warn("No user with username '{}'", username);
            return null;
        }
        return userAuth.get().getId();
    }
}