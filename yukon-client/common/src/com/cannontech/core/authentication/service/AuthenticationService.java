package com.cannontech.core.authentication.service;

import com.cannontech.common.exception.BadAuthenticationException;
import com.cannontech.database.data.lite.LiteYukonUser;

/**
 * Service used to authenticate users. This is a generic service
 * for providing authentication of Yukon Users based on username
 * and password. It also allows changing, setting, and recovering
 * of passwords if the authentication method for a given user
 * supports it.
 */
public interface AuthenticationService {

    /**
     * Attempt to login to Yukon. How the username/password is authenticated
     * will be determined by the AuthType value in the YukonUser table for
     * the given username. If no username is found or if the authentication
     * fails, a BadAuthenticationException will be thrown. This method will
     * never return null.
     * @param username
     * @param password
     * @return LiteYukonUser after user has been authenticated
     * @throws BadAuthenticationException
     */
    public abstract LiteYukonUser login(String username, String password)
            throws BadAuthenticationException;

    /**
     * Indicates if the underlying authentication method for the user
     * supports setting the password to a new value. For example, LDAP 
     * and RADIUS do not support this.
     * @param type
     * @return true if supported
     */
    public boolean supportsPasswordSet(AuthType type);
    
    /**
     * Sets the user's password assuming the underlying authentication 
     * method supports it.
     * @param user
     * @param newPassword
     */
    public void setPassword(LiteYukonUser user, String newPassword);
    
    /**
     * Indicates if the underlying authentication method for the user
     * supports changing the password to a new value.
     * @param type
     * @return true if supported
     */
    public boolean supportsPasswordChange(AuthType type);
    
    /**
     * Changes the user's password if the oldPassword matches their current password.
     * @param user
     * @param oldPassword
     * @param newPassword
     * @throws BadAuthenticationException if the oldPassword doesn't match
     */
    public void changePassword(LiteYukonUser user, String oldPassword, String newPassword) throws BadAuthenticationException;
    
    /**
     * Indicates if the underlying authentication method for the user
     * supports recovering the plain text password.
     * @param type
     * @return the plain text password
     */
    public boolean supportsPasswordRecovery(AuthType type);
    
    /**
     * Recovers the user's password.
     * @param user
     * @return the user's password.
     */
    public String recoverPassword(LiteYukonUser user);
    
    /**
     * Get AuthenticationThrottleDto data for the username.
     * @param username
     * @return AuthenticationThrottleDto data
     */
    public AuthenticationThrottleDto getAuthenticationThrottleData(
            String username);

    /**
     * Remove AuthenticationThrottle for the username, upon manual override by
     * an operator
     * @param username
     */
    public void removeAuthenticationThrottle(String username);
    
    /**
     * Generate a random password of length numberOfCharacters consisting of letters and digits
     */
    public String generatePassword(int numberOfCharacters);
}