package ru.drsanches.life_together.auth.data.dto;

import io.swagger.annotations.ApiModelProperty;
import java.util.GregorianCalendar;

public class TokenDTO {

    @ApiModelProperty(required = true)
    private String accessToken;

    @ApiModelProperty(required = true)
    private String refreshToken;

    @ApiModelProperty(required = true)
    private String tokenType;

    @ApiModelProperty(required = true)
    private GregorianCalendar expiresAt; //TODO: Change to String

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

    public GregorianCalendar getExpiresAt() {
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

    public void setExpiresAt(GregorianCalendar expiresAt) {
        this.expiresAt = expiresAt;
    }

    @Override
    public String toString() {
        return "TokenDTO{" +
                "tokenType='" + tokenType + '\'' +
                '}';
    }
}