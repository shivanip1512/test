package com.cannontech.core.authentication.service;

import com.cannontech.common.exception.AuthenticationThrottleException;

public interface AuthenticationThrottleService {
    /**
     * Add/update AuthenticationThrottle for the username. eg., after a login attempt
     * @throws AuthenticationThrottleException, if login attempted before wait time elapsed
     */
    public void loginAttempted(String username) throws AuthenticationThrottleException;

    /**
     * Remove AuthenticationThrottle for the username, eg. after a successful login.
     */
    public void loginSucceeded(String username);

    /**
     * Get AuthenticationThrottleDto data for the username.
     */
    public AuthenticationThrottleDto getAuthenticationThrottleData(String username);

    /**
     * Remove AuthenticationThrottle for the username, upon manual override by an operator
     */
    public void removeAuthenticationThrottle(String username);

    /**
     * Resets all the AuthenticationThrottle data in case needed
     */
    public void resetAll();
}