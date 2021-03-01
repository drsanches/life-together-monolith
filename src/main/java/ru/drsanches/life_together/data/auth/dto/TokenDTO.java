package ru.drsanches.life_together.data.auth.dto;

import io.swagger.annotations.ApiModelProperty;

public class TokenDTO {

    @ApiModelProperty(required = true)
    private String accessToken;

    @ApiModelProperty(required = true)
    private String refreshToken;

    @ApiModelProperty(required = true)
    private String tokenType;

    @ApiModelProperty(required = true)
    private Integer expiresIn;

    public TokenDTO() {}

    public TokenDTO(String accessToken, String refreshToken, String tokenType, Integer expiresIn) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.tokenType = tokenType;
        this.expiresIn = expiresIn;
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

    public Integer getExpiresIn() {
        return expiresIn;
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

    public void setExpiresIn(Integer expiresIn) {
        this.expiresIn = expiresIn;
    }

    @Override
    public String toString() {
        return "TokenDTO{" +
                "tokenType='" + tokenType + '\'' +
                '}';
    }
}