package ru.drsanches.life_together.auth.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.endpoint.TokenEndpoint;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
import org.springframework.stereotype.Service;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import ru.drsanches.life_together.auth.data.dto.ChangeEmailDTO;
import ru.drsanches.life_together.auth.data.dto.ChangePasswordDTO;
import ru.drsanches.life_together.auth.data.dto.ChangeUsernameDTO;
import ru.drsanches.life_together.auth.data.dto.DeleteUserDTO;
import ru.drsanches.life_together.auth.data.dto.LoginDTO;
import ru.drsanches.life_together.auth.data.dto.RegistrationDTO;
import ru.drsanches.life_together.auth.data.dto.UserAuthInfoDTO;
import ru.drsanches.life_together.auth.data.user.Role;
import ru.drsanches.life_together.auth.data.user.UserAuth;
import ru.drsanches.life_together.auth.data.user.UserAuthRepository;
import ru.drsanches.life_together.auth.exception.ApplicationException;
import ru.drsanches.life_together.auth.exception.ServerError;
import ru.drsanches.life_together.auth.exception.UserAlreadyExistsException;
import javax.security.auth.Subject;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService {

    private final Logger LOG = LoggerFactory.getLogger(AuthService.class);

    private static final BCryptPasswordEncoder ENCODER = new BCryptPasswordEncoder();

    @Autowired
    private UserAuthRepository userAuthRepository;

    @Autowired
    private TokenEndpoint tokenEndpoint;

    @Autowired
    private JdbcTokenStore tokenStore;

    public void registration(RegistrationDTO registrationDTO) {
        UserAuth userAuth = new UserAuth();
        userAuth.setId(UUID.randomUUID().toString());
        userAuth.setUsername(registrationDTO.getUsername());
        userAuth.setPassword(ENCODER.encode(registrationDTO.getPassword()));
        userAuth.setEmail(registrationDTO.getEmail());
        userAuth.setEnabled(true);
        userAuth.setRole(Role.USER);
        try {
            userAuthRepository.save(userAuth);
        } catch(DataIntegrityViolationException e) {
            throw new UserAlreadyExistsException(registrationDTO.getUsername(), e);
        }
        LOG.info("New userAuth has been created: {}", userAuth.toString());
    }

    public ResponseEntity<OAuth2AccessToken> login(LoginDTO loginDTO) {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("username", loginDTO.getUsername());
        parameters.put("password", loginDTO.getPassword());
        parameters.put("grant_type", "password");
        parameters.put("scope", "ui");

        try {
            return tokenEndpoint.postAccessToken(new CustomPrincipal("browser", true), parameters);
        } catch (HttpRequestMethodNotSupportedException e) {
            e.printStackTrace();
            return null;
        }
    }

    public UserAuthInfoDTO info(String username) {
        UserAuth current = getUserByUsernameIfExists(username);
        UserAuthInfoDTO userAuthInfoDTO = new UserAuthInfoDTO();
        userAuthInfoDTO.setId(current.getId());
        userAuthInfoDTO.setUsername(username);
        userAuthInfoDTO.setEmail(current.getEmail());
        return userAuthInfoDTO;
    }

    public void changeUsername(String username, ChangeUsernameDTO changeUsernameDTO) {
        UserAuth current = getUserByUsernameIfExists(username);
        checkPassword(changeUsernameDTO.getPassword(), current.getPassword());
        if (changeUsernameDTO.getNewUsername().equals(username)) {
            throw new ApplicationException("New username is equal to old");
        }
        current.setUsername(changeUsernameDTO.getNewUsername());
        userAuthRepository.save(current);
        logout(username);
        LOG.info("Username has been changed: {}. Old username: {}", current.toString(), username);
    }

    public void changePassword(String username, ChangePasswordDTO changePasswordDTO) {
        UserAuth current = getUserByUsernameIfExists(username);
        checkPassword(changePasswordDTO.getOldPassword(), current.getPassword());
        if (changePasswordDTO.getOldPassword().equals(changePasswordDTO.getNewPassword())) {
            throw new ApplicationException("New password is equal to old");
        }
        current.setPassword(ENCODER.encode(changePasswordDTO.getNewPassword()));
        userAuthRepository.save(current);
        logout(username);
        LOG.info("Password has been changed for user: {}", current.toString());
    }

    public void changeEmail(String username, ChangeEmailDTO changeEmailDTO) {
        UserAuth current = getUserByUsernameIfExists(username);
        checkPassword(changeEmailDTO.getPassword(), current.getPassword());
        if (changeEmailDTO.getNewEmail().equals(current.getEmail())) {
            throw new ApplicationException("New email is equal to old");
        }
        current.setEmail(changeEmailDTO.getNewEmail());
        userAuthRepository.save(current);
        LOG.info("Email has been changed: {}", current.toString());
    }

    public void logout(String username) {
        tokenStore.findTokensByUserName(username).forEach(token -> {
            tokenStore.removeRefreshToken(token.getRefreshToken());
            tokenStore.removeAccessToken(token);
        });
    }

    public void deleteUser(String username, DeleteUserDTO deleteUserDTO) {
        UserAuth current = getUserByUsernameIfExists(username);
        checkPassword(deleteUserDTO.getPassword(), current.getPassword());
        logout(username);
        current.setEnabled(false);
        current.setUsername(UUID.randomUUID().toString() + "_" + current.getUsername());
        userAuthRepository.save(current);
        LOG.info("User has been disabled: {}", current.toString());
    }

    private UserAuth getUserByUsernameIfExists(String username) {
        Optional<UserAuth> user = userAuthRepository.findByUsername(username);
        if (user.isEmpty() || !user.get().isEnabled()) {
            throw new ServerError("An error occurred while getting the user with username'" + username + "'");
        }
        return user.get();
    }

    private void checkPassword(String rawPassword, String encodedPassword) {
        if (!ENCODER.matches(rawPassword, encodedPassword)) {
            throw new ApplicationException("Wrong password");
        }
    }

    private class CustomPrincipal implements Authentication {

        private String name;

        private boolean isAuthenticated;

        public CustomPrincipal(String name, boolean isAuthenticated) {
            this.name = name;
            this.isAuthenticated = isAuthenticated;
        }

        @Override
        public boolean implies(Subject subject) {
            return false;
        }

        public CustomPrincipal() {
            super();
        }

        @Override
        public int hashCode() {
            return super.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            return super.equals(obj);
        }

        @Override
        protected Object clone() throws CloneNotSupportedException {
            return super.clone();
        }

        @Override
        public String toString() {
            return super.toString();
        }

        @Override
        protected void finalize() throws Throwable {
            super.finalize();
        }

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            return null;
        }

        @Override
        public Object getCredentials() {
            return null;
        }

        @Override
        public Object getDetails() {
            return null;
        }

        @Override
        public Object getPrincipal() {
            return null;
        }

        @Override
        public boolean isAuthenticated() {
            return isAuthenticated;
        }

        @Override
        public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {

        }

        @Override
        public String getName() {
            return name;
        }
    }
}