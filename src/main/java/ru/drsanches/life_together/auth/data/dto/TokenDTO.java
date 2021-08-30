package ru.drsanches.life_together.auth.data.dto;

import io.swagger.annotations.ApiModelProperty;
import ru.drsanches.life_together.common.utils.GregorianCalendarConvertor;

public class TokenDTO {

    @ApiModelProperty(required = true)
    private String accessToken;

    @ApiModelProperty(required = true)
    private String refreshToken;

    @ApiModelProperty(required = true)
    private String tokenType;

    @ApiModelProperty(notes = GregorianCalendarConvertor.PATTERN, required = true)
    private String expiresAt;

    public TokenDTO() {}

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public String getExpiresAt() {
        return expiresAt;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public void setExpiresAt(String expiresAt) {
        this.expiresAt = expiresAt;
    }

    @Override
    public String toString() {
        return "TokenDTO{" +
                "tokenType='" + tokenType + '\'' +
                '}';
    }
}