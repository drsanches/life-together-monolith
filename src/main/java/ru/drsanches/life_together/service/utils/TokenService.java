package ru.drsanches.life_together.service.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.endpoint.TokenEndpoint;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
import org.springframework.stereotype.Service;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import ru.drsanches.life_together.data.auth.dto.TokenDTO;
import ru.drsanches.life_together.exception.ServerError;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Service
public class TokenService {

    @Autowired
    private TokenEndpoint tokenEndpoint;

    @Autowired
    private JdbcTokenStore tokenStore;

    public TokenDTO createToken(String username, String password) {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("username", username);
        parameters.put("password", password);
        parameters.put("grant_type", "password");
        parameters.put("scope", "ui");

        try {
            OAuth2AccessToken oAuth2AccessToken = tokenEndpoint
                    .postAccessToken(new CustomPrincipal("browser", true), parameters)
                    .getBody();
            if (oAuth2AccessToken == null) {
                throw new ServerError("Empty OAuth2AccessToken");
            }
            TokenDTO tokenDTO = new TokenDTO();
            tokenDTO.setAccessToken(oAuth2AccessToken.getValue());
            tokenDTO.setRefreshToken(oAuth2AccessToken.getRefreshToken().getValue());
            tokenDTO.setTokenType(OAuth2AccessToken.BEARER_TYPE);
            tokenDTO.setExpiresIn(oAuth2AccessToken.getExpiresIn());
            return tokenDTO;
        } catch (HttpRequestMethodNotSupportedException e) {
            throw new ServerError(e);
        }
    }

    public void removeToken(String username) {
        tokenStore.findTokensByUserName(username).forEach(token -> {
            tokenStore.removeRefreshToken(token.getRefreshToken());
            tokenStore.removeAccessToken(token);
        });
    }

    private static class CustomPrincipal implements Authentication {

        private final String name;

        private final boolean isAuthenticated;

        public CustomPrincipal(String name, boolean isAuthenticated) {
            this.name = name;
            this.isAuthenticated = isAuthenticated;
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
        public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {}

        @Override
        public String getName() {
            return name;
        }
    }
}