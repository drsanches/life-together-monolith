package ru.drsanches.life_together.data.mapper;

import org.springframework.stereotype.Component;
import ru.drsanches.life_together.data.auth.dto.TokenDTO;
import ru.drsanches.life_together.token.Token;

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