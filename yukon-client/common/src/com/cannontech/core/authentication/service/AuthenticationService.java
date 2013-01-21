package com.cannontech.core.authentication.service;

import com.cannontech.common.exception.BadAuthenticationException;
import com.cannontech.common.exception.PasswordExpiredException;
import com.cannontech.core.authentication.model.AuthType;
import com.cannontech.core.authentication.model.AuthenticationCategory;
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
     * Get the default authentication type. This may differ from the type in use by the login for
     * the user if the utility has updated the role property. Password changes should use this so
     * the user is updated to the current authentication type when changing their password.
     * 
     * This returns the authentication type to actually use when creating the password.
     * 
     * @see AuthType
     */
    public AuthType getDefaultAuthType();

    /**
     * Get the default user authentication type.  This return the abstracted "user authentication".
     * 
     * @see AuthenticationCategory
     */
    public AuthenticationCategory getDefaultAuthenticationCategory();

    /**
     * Attempt to login to Yukon. How the username/password is authenticated
     * will be determined by the AuthType value in the YukonUser table for
     * the given username. If no username is found or if the authentication
     * fails, a BadAuthenticationException will be thrown. This method will
     * never return null.
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
     * @return true if supported
     */
    public boolean supportsPasswordSet(AuthenticationCategory authenticationCategory);

    /**
     * Indicates if the underlying authentication method for the user
     * supports setting the password to a new value. For example, LDAP 
     * and RADIUS do not support this.
     * @return true if supported
     * TODO:  deprecate???
     */
    public boolean supportsPasswordSet(AuthType type);

    /**
     * Sets the authentication category for a user. This method should only be used for
     * authentication categories
     * for which the password cannot be changed by Yukon. If Yukon can change the password,
     * {@link #setPassword(LiteYukonUser, AuthenticationCategory, String) should be used instead.}
     */
    public void setAuthenticationCategory(LiteYukonUser user, AuthenticationCategory authenticationCategory);

    /**
     * Encrypts the password according to the specified authentication category and updates the database.  This
     * should only be used when a specific authentication category (other than the default) is desired.  The normal
     * case is to call {@link #setPassword(LiteYukonUser, String)}.
     * 
     * @throws UnsupportedOperationException If the specified authentication category does not support password sets
     *             via Yukon.
     */
    public void setPassword(LiteYukonUser user, AuthenticationCategory authenticationCategory, String newPassword);

    /**
     * Encrypts the password according to the specified authentication category and updates the database.
     * 
     * This method uses the GlobalSettingType.DEFAULT_AUTH_TYPE to determine the authentication method.
     * 
     * @throws UnsupportedOperationException If the authentication category specified by the global setting does not
     *             support password sets via Yukon.
     */
    public void setPassword(LiteYukonUser user, String newPassword);

    /**
     * Get AuthenticationThrottleDto data for the username.
     * @return AuthenticationThrottleDto data
     */
    public AuthenticationThrottleDto getAuthenticationThrottleData(
            String username);

    /**
     * Remove AuthenticationThrottle for the username, upon manual override by
     * an operator
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
