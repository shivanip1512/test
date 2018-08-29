package com.cannontech.dr.pxwhite.model;

/**
 * Credentials for authenticating with PX White.
 */
public class PxWhiteCredentials {
    private final String user;
    private final String password;
    private final String applicationId;
    
    public PxWhiteCredentials(String user, String password, String applicationId) {
        this.user = user;
        this.password = password;
        this.applicationId = applicationId;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public String getApplicationId() {
        return applicationId;
    }
    
}
