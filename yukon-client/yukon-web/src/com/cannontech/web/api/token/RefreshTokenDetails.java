package com.cannontech.web.api.token;

public class RefreshTokenDetails {

    private String refreshTokenId;
    private String refreshToken;
    private Integer userId;

    public RefreshTokenDetails(String refreshTokenId, String refreshToken, Integer userId) {
        this.refreshTokenId = refreshTokenId;
        this.refreshToken = refreshToken;
        this.userId = userId;
    }

    public String getRefreshTokenId() {
        return refreshTokenId;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public Integer getUserId() {
        return userId;
    }

}
