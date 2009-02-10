package com.cannontech.core.authentication.service;

import com.cannontech.common.exception.AuthenticationThrottleException;

/**
 * Helper Interface to maintain Authentication Throttle, so failed login
 * attempts are given sliding scale of increasing retry timeouts, to thwart
 * brute force login attempts.
 * @author mmalekar
 */
public interface AuthenticationThrottleHelper {

    /**
     * Default Authentication Throttle Base param, used to ramp up throttle
     * duration slowly or rapidly
     */
    public static final double AUTH_THROTTLE_BASE = Math.E;

    /**
     * Default value to Cleanup any abandoned AuthenticationThrottle data that
     * is older than this number of days
     */
    public static final int ABANDONED_AUTH_THROTTLE_DAYS = -100;

    /**
     * Add AuthenticationThrottle for the username and return the retry timeout
     * duration. eg., after a login attempt
     * @param username
     * @return retry wait duration in seconds
     * @throws AuthenticationThrottleException, if login attempted before wait time elapsed
     */
    public long loginAttempted(String username) throws AuthenticationThrottleException;

    /**
     * Remove AuthenticationThrottle for the username, eg. after a successful
     * login.
     * @param username
     */
    public void loginSucceeded(String username);

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
     * Clean up any authentication throttle data that has expired more than
     * ABANDONED_AUTH_THROTTLE_DAYS days ago. Optionally,
     * abandonedAuthTimeoutDays can be set before this call.
     */
    public void cleanupAuthenticationThrottle();

    /**
     * Resets all the AuthenticationThrottle data in case needed
     */
    public void resetAll();

    /**
     * Set authThrottleBase param, used to ramp up throttle duration slowly or
     * rapidly
     * @param authThrottleBase
     */
    public void setAuthThrottleBase(double authThrottleBase);

    /**
     * Set abandonedAuthThrottleDays, used to cleanup expired throttle data.
     * @param abandonedAuthThrottleDays
     */
    public void setAbandonedAuthThrottleDays(int abandonedAuthThrottleDays);

}
