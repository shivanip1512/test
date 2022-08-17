package com.cannontech.dr.pxwhite.model;

/**
 * Credentials for authenticating with PX White.
 */
public class PxWhiteCredentials {
    private final String user; //typically an email address
    private final String password;
    private final String applicationId; //In the UUID form aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee (8-4-4-4-12)
    
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
