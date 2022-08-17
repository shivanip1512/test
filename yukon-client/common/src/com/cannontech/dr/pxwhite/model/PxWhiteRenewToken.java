package com.cannontech.dr.pxwhite.model;

/**
 * Request object for renewing a PX White security token.
 */
public final class PxWhiteRenewToken {
    private final String user;
    private final String token;
    
    public PxWhiteRenewToken(String user, String token) {
        this.user = user;
        this.token = token;
    }

    public String getUser() {
        return user;
    }

    public String getToken() {
        return token;
    }
    
}
