package com.cannontech.core.authentication.service;

import com.cannontech.common.exception.BadAuthenticationException;
import com.cannontech.common.exception.PasswordExpiredException;
import com.cannontech.core.authentication.model.AuthType;
import com.cannontech.core.authentication.model.AuthenticationThrottleDto;
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
     * Get the default authorization type.  This may differ from the type in use by the login for
     * the user if the utility has updated the role property.  Password changes should use this so
     * the user is updated to the current authorization type when changing their password.
     */
    public AuthType getDefaultAuthType(LiteYukonUser user);

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
     * @throws PasswordExpiredException 
     */
    public LiteYukonUser login(String username, String password) throws BadAuthenticationException, PasswordExpiredException;

    /**
     * This method checks to see if the user's login is expired.  This can be done by an operator forcing a login's password to be expired
     * or for the login's password age to be longer that the password policy's allowed age.
     */
    public boolean isPasswordExpired(LiteYukonUser user);
    
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
     * This method checks to see if the supplied password is being reused before it is valid to reuse it.  If the password is
     * being used before it's allows this method will return true.
     */
    public boolean isPasswordBeingReused(LiteYukonUser yukonUser, String newPassword, int numberOfPasswordsToCheck);

    /**
     * This method expires all the passwords for a given group.  This will then force the users to reset their password when they login.
     */
    public void expireAllPasswords(int groupId);
}