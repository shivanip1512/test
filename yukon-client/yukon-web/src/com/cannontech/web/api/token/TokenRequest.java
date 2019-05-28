package com.cannontech.web.api.token;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TokenRequest {

   String username;
   String password;

    @JsonCreator
    TokenRequest() {

    }

    TokenRequest(@JsonProperty(value = "username") String username, @JsonProperty(value = "password") String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

}
