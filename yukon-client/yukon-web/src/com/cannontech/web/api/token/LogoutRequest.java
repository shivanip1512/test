package com.cannontech.web.api.token;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class LogoutRequest {

    private String refreshToken;
    private boolean logoutfromAllSystem;

    @JsonCreator
    LogoutRequest() {

    }

    LogoutRequest(@JsonProperty(value = "refreshToken") String refreshToken, @JsonProperty(value = "logoutfromAllSystem") boolean logoutfromAllSystem) {
        this.refreshToken = refreshToken;
        this.logoutfromAllSystem = logoutfromAllSystem;

    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public boolean isLogoutfromAllSystem() {
        return logoutfromAllSystem;
    }

    public void setLogoutfromAllSystem(boolean logoutfromAllSystem) {
        this.logoutfromAllSystem = logoutfromAllSystem;
    }
}

