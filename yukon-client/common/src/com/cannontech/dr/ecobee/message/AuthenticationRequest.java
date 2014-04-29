package com.cannontech.dr.ecobee.message;

public final class AuthenticationRequest {
    private final String username;
    private final String password;
    
    public AuthenticationRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUserName() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
