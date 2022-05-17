package com.cannontech.web.api.der.edge;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EdgeUnicastResponse {
    String token;
    
    public EdgeUnicastResponse() {}
    
    public EdgeUnicastResponse(String token) {
        this.token = token;
    }
    
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
}
