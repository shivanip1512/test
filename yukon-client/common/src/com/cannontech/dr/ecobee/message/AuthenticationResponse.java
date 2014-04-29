package com.cannontech.dr.ecobee.message;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public final class AuthenticationResponse extends AbstractResponse {
    private final String token;
    
    @JsonCreator
    public AuthenticationResponse(@JsonProperty("token") String token, @JsonProperty("status") Status status) {
        super(status);
        this.token = token;
    }
    
    public String getToken() {
        return token;
    }
}
