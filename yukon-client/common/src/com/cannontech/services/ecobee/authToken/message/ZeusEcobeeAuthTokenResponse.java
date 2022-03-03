package com.cannontech.services.ecobee.authToken.message;

import java.io.Serializable;

public class ZeusEcobeeAuthTokenResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    private String authToken;
    private String refreshToken;
    private String expiryTimestamp;

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getExpiryTimestamp() {
        return expiryTimestamp;
    }

    public void setExpiryTimestamp(String expiryTimestamp) {
        this.expiryTimestamp = expiryTimestamp;
    }
}
