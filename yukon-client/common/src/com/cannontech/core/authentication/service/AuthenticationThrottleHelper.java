package com.cannontech.core.authentication.service;

import com.cannontech.common.exception.AuthenticationThrottleException;

/**
 * Helper Interface to maintain Authentication Throttle, so failed login
 * attempts are given sliding scale of increasing retry timeouts, to thwart
 * brute force login attempts.
 * @author mmalekar
 */
public interface AuthenticationThrottleHelper {

    public static final String AUTH_THROTTLE_EXP_BASE_KEY = "AUTH_THROTTLE_EXP_BASE";
    public static final String AUTH_THROTTLE_DELTA_KEY = "AUTH_THROTTLE_DELTA";
    
    /**
     * Default Authentication Throttle Exponential Base param, used to ramp up throttle
     * duration slowly or rapidly
     */
    public static final double AUTH_THROTTLE_EXP_BASE = Math.E/2;
    
    /**
     * Default Authentication Throttle static Delta param, used to ramp up throttle
     * duration slowly or rapidly
     */
    public static final double AUTH_THROTTLE_DELTA = 0.0;    

    /**
     * Default value to Cleanup any abandoned AuthenticationThrottle data that
     * is older than this number of days
     */
    public static final int ABANDONED_AUTH_THROTTLE_DAYS = -100;

    /**
     * Add/update AuthenticationThrottle for the username. eg., after a login attempt
     * @param username
     * @throws AuthenticationThrottleException, if login attempted before wait time elapsed
     */
    public void loginAttempted(String username) throws AuthenticationThrottleException;

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
     * Set authThrottle Exponential Base param, used to ramp up throttle duration slowly or
     * rapidly
     * @param authThrottleExpBase
     */
    public void setAuthThrottleExpBase(double authThrottleExpBase);
    
    /**
     * Set authThrottle static Delta param, used to ramp up throttle duration slowly or
     * rapidly
     * @param authThrottleDelta
     */
    public void setAuthThrottleDelta(double authThrottleDelta);    

    /**
     * Set abandonedAuthThrottleDays, used to cleanup expired throttle data.
     * @param abandonedAuthThrottleDays
     */
    public void setAbandonedAuthThrottleDays(int abandonedAuthThrottleDays);

}
