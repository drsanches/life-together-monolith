package ru.drsanches.life_together.token;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.GregorianCalendar;

@Entity
@Table(name="token")
public class Token {

    @Id
    @Column
    private String accessToken;

    @Column(unique = true, nullable = false)
    private String refreshToken;

    @Column(nullable = false)
    private String tokenType;

    @Column(nullable = false)
    private GregorianCalendar expiresAt;

    @Column(nullable = false)
    private String userId;

    public Token() {}

    public Token(String accessToken, String refreshToken, String tokenType, GregorianCalendar expiresAt, String userId) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.tokenType = tokenType;
        this.expiresAt = expiresAt;
        this.userId = userId;
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

    public String getUserId() {
        return userId;
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

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "Token{" +
                "tokenType='" + tokenType + '\'' +
                ", userId='" + userId + '\'' +
                '}';
    }
}