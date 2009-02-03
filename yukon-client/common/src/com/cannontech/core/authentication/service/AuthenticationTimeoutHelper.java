package com.cannontech.core.authentication.service;

/**
 * Helper Interface to maintain Authentication timeouts, so failed login attempts
 * are given sliding scale of increasing retry timeouts, to thwart brute force
 * login attempts.
 * @author mmalekar
 */
public interface AuthenticationTimeoutHelper {

    // Cleanup any abandoned AuthenticationTimeout data that is older than X days
    public static final int ABANDONED_AUTH_TIMEOUT_DAYS = -100;
    
    /**
     * Add AuthenticationTimeout for the username and return the retry timeout
     * duration.
     * @param username
     * @return retry timeout duration in seconds
     */
    public long addAuthenticationTimeout(String username);

    /**
     * Remove AuthenticationTimeout for the username.
     * @param username
     */
    public void removeAuthenticationTimeout(String username);

    /**
     * Get AuthenticationTimeout duration for the username.
     * @param username
     * @return retry timeout duration in seconds
     */
    public long getAuthenticationTimeoutDuration(String username);

    /** 
     * Clean up any timeouts which have expired more than ABANDONED_AUTH_TIMEOUT_DAYS days ago.
     * Optionally, abandonedAuthTimeoutDays can be set before this call.
     */
    public void cleanupAuthenticationTimeout();

    /** Reset the AuthenticationTimeout data in case needed
     * 
     */
    public void reset();
    
    /**
     * Set abandonedAuthTimeoutDays, used to cleanup expired timeout data.
     * @param abandonedAuthTimeoutDays
     */
    public void setAbandonedAuthTimeoutDays(int abandonedAuthTimeoutDays);

}
