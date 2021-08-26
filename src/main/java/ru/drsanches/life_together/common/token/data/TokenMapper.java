package ru.drsanches.life_together.common.token.data;

import org.springframework.stereotype.Component;
import ru.drsanches.life_together.auth.data.dto.TokenDTO;

@Component
public class TokenMapper {

    public TokenDTO convert(Token token) {
        TokenDTO tokenDTO = new TokenDTO();
        tokenDTO.setAccessToken(token.getAccessToken());
        tokenDTO.setRefreshToken(token.getRefreshToken());
        tokenDTO.setTokenType(token.getTokenType());
        tokenDTO.setExpiresAt(token.getExpiresAt());
        return tokenDTO;
    }
}