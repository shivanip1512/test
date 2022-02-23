package com.cannontech.web.api.token;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class RefreshTokenRequest {

    private String refreshToken;

    @JsonCreator
    RefreshTokenRequest() {

    }

    RefreshTokenRequest(@JsonProperty(value = "refreshToken") String refreshToken) {
        this.refreshToken = refreshToken;

    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

}