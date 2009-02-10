package com.cannontech.core.authentication.service.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cannontech.common.exception.AuthenticationThrottleException;
import com.cannontech.core.authentication.service.AuthenticationThrottleDto;
import com.cannontech.core.authentication.service.AuthenticationThrottleHelper;

/**
 * Helper impl to maintain AuthenticationThottle data, so failed login attempts
 * are given sliding scale of increasing retry timeouts, to thwart brute force
 * login attempts.
 * @author mmalekar
 */
public class AuthenticationThrottleHelperImpl implements
        AuthenticationThrottleHelper {

    // map of username and AuthenticationThrottle data
    private Map<String, AuthenticationThrottle> authThrottleMap = new HashMap<String, AuthenticationThrottle>();

    // authThrottleBase, used to ramp up throttle duration slowly or rapidly
    private double authThrottleBase = AuthenticationThrottleHelper.AUTH_THROTTLE_BASE;

    // Cleanup any abandoned AuthenticationThrottle data that is older than X
    // days
    private int abandonedAuthThrottleDays = AuthenticationThrottleHelper.ABANDONED_AUTH_THROTTLE_DAYS;

    @Override
    public synchronized long loginAttempted(String username) throws AuthenticationThrottleException {
        long waitSecondsBefore = 0;
        long waitSecondsAfter = 0;
        // see if the previous wait time elapsed
        AuthenticationThrottle authThrottle = authThrottleMap.get(username);
        if (authThrottle == null) {
            authThrottle = new AuthenticationThrottle();
            authThrottleMap.put(username, authThrottle);
        } else {
            waitSecondsBefore = authThrottle.getThrottleDurationSeconds();
            authThrottle.updateAuthThrottle();
        }
        // get the new wait time
        waitSecondsAfter = authThrottle.getThrottleDurationSeconds();
        
        // if login attempted before the wait time elapsed, throw an exception with new wait time
        if (waitSecondsBefore > 0) {
            throw new AuthenticationThrottleException(waitSecondsAfter);
        }
        else {
            // normal case, return the next wait time to use if login fails            
            return waitSecondsAfter;    
        }
    }

    @Override
    public synchronized void loginSucceeded(String username) {
        authThrottleMap.remove(username);
    }

    @Override
    public synchronized AuthenticationThrottleDto getAuthenticationThrottleData(
            String username) {
        AuthenticationThrottleDto authThrottleDto = null;
        AuthenticationThrottle authThrottle = authThrottleMap.get(username);
        if (authThrottle != null) {
            authThrottleDto = authThrottle.getAuthenticationThrottleData();
        }
        return authThrottleDto;
    }

    @Override
    public synchronized void removeAuthenticationThrottle(String username) {
        authThrottleMap.remove(username);
    }

    @Override
    public synchronized void cleanupAuthenticationThrottle() {
        Calendar oldCutoffCal = Calendar.getInstance();
        oldCutoffCal.add(Calendar.DAY_OF_MONTH, abandonedAuthThrottleDays);
        long oldCutoffTimeout = oldCutoffCal.getTimeInMillis();

        List<String> authThrottleRemoveList = new ArrayList<String>();
        for (Map.Entry<String, AuthenticationThrottle> authThrottle : authThrottleMap.entrySet()) {

            if (authThrottle.getValue().getThrottleEndtime() < oldCutoffTimeout) {
                authThrottleRemoveList.add(authThrottle.getKey());
            }
        }
        for (String username : authThrottleRemoveList) {
            authThrottleMap.remove(username);
        }
    }

    @Override
    public synchronized void resetAll() {
        authThrottleMap = new HashMap<String, AuthenticationThrottle>();
    }

    @Override
    public synchronized void setAuthThrottleBase(double authThrottleBase) {
        this.authThrottleBase = authThrottleBase;
    }

    @Override
    public synchronized void setAbandonedAuthThrottleDays(
            int abandonedAuthThrottleDays) {
        this.abandonedAuthThrottleDays = abandonedAuthThrottleDays;
    }

    public class AuthenticationThrottle {

        private long lastFailedLoginTime;
        private long retryCount;

        public AuthenticationThrottle() {
            lastFailedLoginTime = new Date().getTime();
            retryCount = 0;
        }

        public long getLastFailedLoginTime() {
            return lastFailedLoginTime;
        }

        public long getRetryCount() {
            return retryCount;
        }

        public void updateAuthThrottle() {
            this.lastFailedLoginTime = new Date().getTime();
            this.retryCount++;
        }

        public long getThrottleEndtime() {
            return lastFailedLoginTime + (Math.round(Math.pow(authThrottleBase,
                                                              retryCount)) * 1000);
        }

        public long getThrottleDurationSeconds() {
            // throttle expires if current time >= throttleEndtime
            long throttleSeconds = (getThrottleEndtime() - new Date().getTime()) / 1000;
            throttleSeconds = (throttleSeconds < 0) ? 0 : throttleSeconds;

            return throttleSeconds;
        }

        public AuthenticationThrottleDto getAuthenticationThrottleData() {
            AuthenticationThrottleDto authThrottleDto = new AuthenticationThrottleDto();
            authThrottleDto.setLastFailedLoginTime(new Date(lastFailedLoginTime));
            authThrottleDto.setRetryCount(retryCount);
            authThrottleDto.setThrottleEndtime(new Date(getThrottleEndtime()));
            authThrottleDto.setThrottleDurationSeconds(getThrottleDurationSeconds());

            return authThrottleDto;
        }
    }

}
