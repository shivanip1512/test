package com.cannontech.core.authentication.service.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cannontech.core.authentication.service.AuthenticationTimeoutHelper;

/**
 * Helper impl to maintain Authentication timeouts, so failed login attempts
 * are given sliding scale of increasing retry timeouts, to thwart brute force
 * login attempts.
 * @author mmalekar
 */
public class AuthenticationTimeoutHelperImpl implements AuthenticationTimeoutHelper {

    // map of username and AuthenticationTimeout data
    private Map<String, AuthenticationTimeout> authTimeoutMap = new HashMap<String, AuthenticationTimeout>();

    // Cleanup any abandoned AuthenticationTimeout data that is older than X days
    private int abandonedAuthTimeoutDays = AuthenticationTimeoutHelper.ABANDONED_AUTH_TIMEOUT_DAYS;

    @Override
    public synchronized long addAuthenticationTimeout(String username) {
        AuthenticationTimeout authTimeout = authTimeoutMap.get(username);
        if (authTimeout == null) {
            authTimeout = new AuthenticationTimeout();
            authTimeoutMap.put(username, authTimeout);
        } else {
            authTimeout.updateLastFailedLoginTime();
            authTimeout.updateRetryCount();
        }
        return getAuthenticationTimeoutDuration(username);
    }

    @Override
    public synchronized void removeAuthenticationTimeout(String username) {
        authTimeoutMap.remove(username);
    }

    @Override
    public synchronized long getAuthenticationTimeoutDuration(String username) {
        long timeoutSeconds = 0;
        // timeout expires if current time >= lastFailedLoginTime +
        // exponential(retryCount)
        AuthenticationTimeout authTimeout = authTimeoutMap.get(username);
        if (authTimeout != null) {
            timeoutSeconds = (authTimeout.getTimeoutEndtime() - new Date().getTime()) / 1000;
            timeoutSeconds = (timeoutSeconds < 0) ? 0 : timeoutSeconds;
        }

        return timeoutSeconds;
    }

    @Override
    public synchronized void cleanupAuthenticationTimeout() {
        Calendar oldCutoffCal = Calendar.getInstance();
        oldCutoffCal.add(Calendar.DAY_OF_MONTH, abandonedAuthTimeoutDays);
        long oldCutoffTimeout = oldCutoffCal.getTimeInMillis();

        List<String> authTimeoutRemoveList = new ArrayList<String>();
        for (Map.Entry<String, AuthenticationTimeout> authTimeout : authTimeoutMap.entrySet()) {

            if (authTimeout.getValue().getTimeoutEndtime() < oldCutoffTimeout) {
                authTimeoutRemoveList.add(authTimeout.getKey());
            }
        }
        for (String username : authTimeoutRemoveList) {
            authTimeoutMap.remove(username);
        }
    }

    @Override
    public synchronized void reset() {
        authTimeoutMap = new HashMap<String, AuthenticationTimeout>();
    }

    @Override
    public synchronized void setAbandonedAuthTimeoutDays(
            int abandonedAuthTimeoutDays) {
        this.abandonedAuthTimeoutDays = abandonedAuthTimeoutDays;
    }

    public static class AuthenticationTimeout {
        private long lastFailedLoginTime;
        private long retryCount;

        public AuthenticationTimeout() {
            lastFailedLoginTime = new Date().getTime();
            retryCount = 0;
        }

        public long getLastFailedLoginTime() {
            return lastFailedLoginTime;
        }

        public long getRetryCount() {
            return retryCount;
        }

        public void updateLastFailedLoginTime() {
            this.lastFailedLoginTime = new Date().getTime();
        }

        public void updateRetryCount() {
            this.retryCount++;
        }

        public long getTimeoutEndtime() {
            return lastFailedLoginTime + (Math.round(Math.exp(retryCount)) * 1000);
        }
    }
}
