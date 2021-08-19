package ru.drsanches.life_together.token;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.drsanches.life_together.exception.AuthException;
import ru.drsanches.life_together.service.utils.UserIdService;
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

    @Autowired
    private UserIdService userIdService;

    public Token createToken(String username, String password) {
        credentialsHelper.checkUser(username, password);
        return createToken(userIdService.getUserIdFromDB(username));
    }

    public String extractTokenId(String token) {
        return token.substring(TOKEN_TYPE.length() + 1);
    }

    public void validate(String token) {
        if (token == null) {
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
        Optional<Token> tokenModel = tokenRepository.findById(extractTokenId(token));
        if (tokenModel.isEmpty()) {
            throw new AuthException();
        }
        return tokenModel.get().getUserId();
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