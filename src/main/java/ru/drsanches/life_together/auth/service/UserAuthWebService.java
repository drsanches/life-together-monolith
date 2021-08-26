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
import ru.drsanches.life_together.common.token.data.Role;
import ru.drsanches.life_together.auth.data.mapper.UserAuthInfoMapper;
import ru.drsanches.life_together.auth.data.model.UserAuth;
import ru.drsanches.life_together.exception.application.NoUsernameException;
import ru.drsanches.life_together.exception.auth.WrongPasswordException;
import ru.drsanches.life_together.exception.auth.WrongUsernamePasswordException;
import ru.drsanches.life_together.exception.application.ApplicationException;
import ru.drsanches.life_together.common.integration.UserIntegrationService;
import ru.drsanches.life_together.common.utils.CredentialsHelper;
import ru.drsanches.life_together.common.token.TokenService;
import ru.drsanches.life_together.common.token.TokenSupplier;
import ru.drsanches.life_together.common.token.data.Token;
import ru.drsanches.life_together.common.token.data.TokenMapper;
import java.util.UUID;

@Service
public class UserAuthWebService {

    private final Logger LOG = LoggerFactory.getLogger(UserAuthWebService.class);

    @Autowired
    private UserAuthDomainService userAuthDomainService;

    @Autowired
    private UserIntegrationService userIntegrationService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private TokenSupplier tokenSupplier;

    @Autowired
    private CredentialsHelper credentialsHelper;

    @Autowired
    private TokenMapper tokenMapper;

    @Autowired
    private UserAuthInfoMapper userAuthInfoMapper;

    public UserAuthInfoDTO registration(RegistrationDTO registrationDTO) {
        registrationDTO.setUsername(registrationDTO.getUsername().toLowerCase());
        UserAuth userAuth = new UserAuth();
        userAuth.setId(UUID.randomUUID().toString());
        userAuth.setUsername(registrationDTO.getUsername());
        userAuth.setPassword(credentialsHelper.encodePassword(registrationDTO.getPassword()));
        userAuth.setEmail(registrationDTO.getEmail());
        userAuth.setEnabled(true);
        userAuth.setRole(Role.USER);
        userIntegrationService.createUser(userAuth);
        LOG.info("New user with id '{}' has been created", userAuth.getId());
        return userAuthInfoMapper.convert(userAuth);
    }

    public TokenDTO login(LoginDTO loginDTO) {
        loginDTO.setUsername(loginDTO.getUsername().toLowerCase());
        UserAuth userAuth;
        try {
            userAuth = userAuthDomainService.getEnabledByUsername(loginDTO.getUsername());
            credentialsHelper.checkPassword(loginDTO.getPassword(), userAuth.getPassword());
        } catch (NoUsernameException | WrongPasswordException e) {
            throw new WrongUsernamePasswordException(e);
        }
        Token token = tokenService.createToken(userAuth.getId(), userAuth.getRole());
        return tokenMapper.convert(token);
    }

    public UserAuthInfoDTO info() {
        String userId = tokenSupplier.get().getUserId();
        UserAuth current = userAuthDomainService.getEnabledById(userId);
        return userAuthInfoMapper.convert(current);
    }

    public void changeUsername(ChangeUsernameDTO changeUsernameDTO) {
        String userId = tokenSupplier.get().getUserId();
        UserAuth current = userAuthDomainService.getEnabledById(userId);
        credentialsHelper.checkPassword(changeUsernameDTO.getPassword(), current.getPassword());
        String oldUsername = current.getUsername();
        if (changeUsernameDTO.getNewUsername().equals(oldUsername)) {
            throw new ApplicationException("New username is equal to old");
        }
        current.setUsername(changeUsernameDTO.getNewUsername());
        userIntegrationService.updateUser(current);
        tokenService.removeAllTokens(userId);
        LOG.info("User with id '{}' changed username from '{}' to '{}'", current.getId(), oldUsername, current.getUsername());
    }

    public void changePassword(ChangePasswordDTO changePasswordDTO) {
        String userId = tokenSupplier.get().getUserId();
        UserAuth current = userAuthDomainService.getEnabledById(userId);
        credentialsHelper.checkPassword(changePasswordDTO.getOldPassword(), current.getPassword());
        if (changePasswordDTO.getOldPassword().equals(changePasswordDTO.getNewPassword())) {
            throw new ApplicationException("New password is equal to old");
        }
        current.setPassword(credentialsHelper.encodePassword(changePasswordDTO.getNewPassword()));
        userAuthDomainService.save(current);
        tokenService.removeAllTokens(userId);
        LOG.info("User with id '{}' changed password", current.getId());
    }

    public void changeEmail(ChangeEmailDTO changeEmailDTO) {
        String userId = tokenSupplier.get().getUserId();
        UserAuth current = userAuthDomainService.getEnabledById(userId);
        credentialsHelper.checkPassword(changeEmailDTO.getPassword(), current.getPassword());
        if (changeEmailDTO.getNewEmail().equals(current.getEmail())) {
            throw new ApplicationException("New email is equal to old");
        }
        current.setEmail(changeEmailDTO.getNewEmail());
        userAuthDomainService.save(current);
        LOG.info("User with id '{}' changed email", current.getId());
    }

    public TokenDTO refreshToken(String refreshToken) {
        return tokenMapper.convert(tokenService.refreshToken(refreshToken));
    }

    public void logout() {
        tokenService.removeCurrentToken();
    }

    public void disableUser(DeleteUserDTO deleteUserDTO) {
        String userId = tokenSupplier.get().getUserId();
        UserAuth current = userAuthDomainService.getEnabledById(userId);
        credentialsHelper.checkPassword(deleteUserDTO.getPassword(), current.getPassword());
        tokenService.removeAllTokens(userId);
        current.setEnabled(false);
        current.setUsername(UUID.randomUUID().toString() + "_" + current.getUsername());
        userIntegrationService.updateUser(current);
        LOG.info("User with id '{}' has been disabled", current.getId());
    }
}