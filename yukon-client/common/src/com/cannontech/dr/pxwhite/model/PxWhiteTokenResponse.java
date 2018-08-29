package com.cannontech.dr.pxwhite.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Response object for PX White token and token renewal requests.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public final class PxWhiteTokenResponse {
    private final String token;
    
    @JsonCreator
    public PxWhiteTokenResponse(@JsonProperty("Token") String token) {
        this.token = token;
    }
    
    public String getToken() {
        return token;
    }
}
