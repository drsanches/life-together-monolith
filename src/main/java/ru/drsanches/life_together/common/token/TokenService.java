package ru.drsanches.life_together.common.token;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.drsanches.life_together.auth.data.model.UserAuth;
import ru.drsanches.life_together.auth.service.UserAuthDomainService;
import ru.drsanches.life_together.exception.application.NoUsernameException;
import ru.drsanches.life_together.exception.auth.WrongTokenException;
import ru.drsanches.life_together.exception.auth.WrongUsernamePasswordException;
import ru.drsanches.life_together.common.token.data.Role;
import ru.drsanches.life_together.common.token.data.Token;
import ru.drsanches.life_together.common.token.data.TokenRepository;
import ru.drsanches.life_together.common.utils.CredentialsHelper;
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
    private TokenSupplier tokenSupplier;

    @Autowired
    private CredentialsHelper credentialsHelper;

    @Autowired
    private UserAuthDomainService userAuthDomainService;

    public Token createToken(String userId, String username, String password) {
        UserAuth userAuth = checkUser(username, password);
        return createToken(userId, userAuth.getRole());
    }

    public void validate(String accessToken) {
        if (accessToken == null || extractTokenId(accessToken) == null) {
            throw new WrongTokenException();
        }
        Optional<Token> token = tokenRepository.findById(extractTokenId(accessToken));

        if (token.isEmpty()) {
            throw new WrongTokenException();
        }
        if (token.get().getExpiresAt().before(new GregorianCalendar())) {
            throw new WrongTokenException();
        }
        tokenSupplier.set(token.get());
    }

    public Token refreshToken(String refreshToken) {
        Token token = getTokenByRefreshToken(refreshToken);
        removeAllTokens(token.getUserId());
        return createToken(token.getUserId(), token.getRole());
    }

    public void removeToken(String token) {
        tokenRepository.deleteById(extractTokenId(token));
    }

    public void removeAllTokens(String userId) {
        tokenRepository.deleteAll(tokenRepository.findByUserId(userId));
    }

    public UserAuth checkUser(String username, String password) {
        UserAuth userAuth;
        try {
            userAuth = userAuthDomainService.getEnabledByUsername(username);
            credentialsHelper.checkPassword(password, userAuth.getPassword());
        } catch (NoUsernameException e) {
            throw new WrongUsernamePasswordException(e);
        }
        return userAuth;
    }

    private Token createToken(String userId, Role role) {
        Token token = new Token();
        token.setAccessToken(UUID.randomUUID().toString());
        token.setRefreshToken(UUID.randomUUID().toString());
        token.setTokenType(TOKEN_TYPE);
        GregorianCalendar expiresAt = new GregorianCalendar();
        expiresAt.add(CALENDAR_FIELD, CALENDAR_VALUE);
        token.setExpiresAt(expiresAt);
        token.setUserId(userId);
        token.setRole(role);
        tokenRepository.save(token);
        tokenSupplier.set(token);
        return token;
    }

    private Token getTokenByRefreshToken(String refreshToken) {
        if (refreshToken == null || extractTokenId(refreshToken) == null) {
            throw new WrongTokenException();
        }
        Optional<Token> tokenModel = tokenRepository.findByRefreshToken(extractTokenId(refreshToken));
        if (tokenModel.isEmpty()) {
            throw new WrongTokenException();
        }
        return tokenModel.get();
    }

    private String extractTokenId(String token) {
        if (token.contains(TOKEN_TYPE + " ")) {
            return token.substring(TOKEN_TYPE.length() + 1);
        } else if (token.contains(TOKEN_TYPE + "%20")) {
            return token.substring(TOKEN_TYPE.length() + 3);
        }
        return null;
    }
}