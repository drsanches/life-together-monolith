package ru.drsanches.life_together.integration.token;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.drsanches.life_together.exception.AuthException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.GregorianCalendar;
import java.util.Optional;
import java.util.UUID;

@Service
public class TokenService {

    private static final String TOKEN_TYPE = "Bearer";

    private static final int CALENDAR_FIELD = GregorianCalendar.DAY_OF_YEAR;

    private static final int CALENDAR_VALUE = 10;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private CredentialsHelper credentialsHelper;

    public Token createToken(String userId, String username, String password) {
        credentialsHelper.checkUser(username, password);
        return createToken(userId);
    }

    public String extractTokenId(String token) {
        if (token.contains(TOKEN_TYPE + " ")) {
            return token.substring(TOKEN_TYPE.length() + 1);
        } else if (token.contains(TOKEN_TYPE + "%20")) {
            return token.substring(TOKEN_TYPE.length() + 3);
        }
        return null;
    }

    public void validate(String token) {
        if (token == null || extractTokenId(token) == null) {
            throw new AuthException();
        }
        Optional<Token> tokenObject = tokenRepository.findById(extractTokenId(token));

        if (tokenObject.isEmpty()) {
            throw new AuthException();
        }
        if (tokenObject.get().getExpiresAt().before(new GregorianCalendar())) {
            throw new AuthException();
        }
    }

    public Token refreshToken(String refreshToken) {
        Optional<Token> tokenOptional = tokenRepository.findByRefreshToken(extractTokenId(refreshToken));
        if (tokenOptional.isEmpty()) {
            throw new AuthException();
        }
        Token tokenObject = tokenOptional.get();
        tokenRepository.deleteById(tokenObject.getAccessToken());
        return createToken(tokenObject.getUserId());
    }

    public void removeToken(String token) {
        tokenRepository.deleteById(extractTokenId(token));
    }

    public String getUserId(String token) {
        if (token == null || extractTokenId(token) == null) {
            throw new AuthException();
        }
        Optional<Token> tokenModel = tokenRepository.findById(extractTokenId(token));
        if (tokenModel.isEmpty()) {
            throw new AuthException();
        }
        return tokenModel.get().getUserId();
    }

    public String getTokenFromRequest(HttpServletRequest httpRequest) {
        String token = httpRequest.getHeader("Authorization"); //TODO: Returns string "null" for incognito tab
        if (token == null) {
            token = getAccessTokenFromCookies(httpRequest.getCookies());
        }
        return token;
    }

    public String getAccessTokenFromCookies(Cookie[] cookies) {
        if (cookies == null) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("Authorization")) {
                return cookie.getValue();
            }
        }
        return null;
    }

    private Token createToken(String userId) {
        Token token = new Token();
        token.setAccessToken(UUID.randomUUID().toString());
        token.setRefreshToken(UUID.randomUUID().toString());
        token.setTokenType(TOKEN_TYPE);
        GregorianCalendar expiresAt = new GregorianCalendar();
        expiresAt.add(CALENDAR_FIELD, CALENDAR_VALUE);
        token.setExpiresAt(expiresAt);
        token.setUserId(userId);
        tokenRepository.save(token);
        return token;
    }
}