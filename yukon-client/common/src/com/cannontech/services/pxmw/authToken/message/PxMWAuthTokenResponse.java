package com.cannontech.services.pxmw.authToken.message;

import java.io.Serializable;

public class PxMWAuthTokenResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    private String authToken;

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

}
