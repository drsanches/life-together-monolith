package ru.drsanches.life_together.data.auth.dto;

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
    private GregorianCalendar expiresAt;

    public TokenDTO() {}

    public TokenDTO(String accessToken, String refreshToken, String tokenType, GregorianCalendar expiresAt) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.tokenType = tokenType;
        this.expiresAt = expiresAt;
    }

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