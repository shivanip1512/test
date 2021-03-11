package com.cannontech.dr.ecobee.message;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class ZeusAuthenticationResponse {
    private final String authToken;
    private final String refreshToken;
    private final String expiryTimestamp;

    @JsonCreator
    public ZeusAuthenticationResponse(@JsonProperty("access_token") String authToken,
            @JsonProperty("refresh_token") String refreshToken,
            @JsonProperty("expiry_timestamp") String expiryTimestamp) {
        this.authToken = authToken;
        this.refreshToken = refreshToken;
        this.expiryTimestamp = expiryTimestamp;
    }

    public String getAuthToken() {
        return authToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public String getExpiryTimestamp() {
        return expiryTimestamp;
    }

}
