package com.cannontech.core.authentication.service.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigDoubleKeysEnum;
import com.cannontech.common.exception.AuthenticationThrottleException;
import com.cannontech.core.authentication.service.AuthenticationThrottleDto;
import com.cannontech.core.authentication.service.AuthenticationThrottleService;

/**
 * Helper impl to maintain AuthenticationThottle data, so failed login attempts
 * are given sliding scale of increasing retry timeouts, to thwart brute force
 * login attempts.
 * @author mmalekar
 */
public class IncreasingAuthenticationThrottleServiceImpl implements AuthenticationThrottleService, InitializingBean {

    @Autowired private ConfigurationSource configurationSource;
    
    // map of username and AuthenticationThrottle data
    private Map<String, AuthenticationThrottle> authThrottleMap = new HashMap<String, AuthenticationThrottle>();

    private double authThrottleExpBase = Math.E/2; // used to ramp up throttle duration slowly or rapidly
    private double authThrottleDelta =  0.0; // used to ramp up throttle duration slowly or rapidly    
    private int abandonedAuthThrottleDays = -100;  //Cleanup any abandoned AuthenticationThrottle data that is older than X days

    @Override
    public void afterPropertiesSet() throws Exception {

        // Check and see if we need to change the AUTH_THROTTLE_DELTA_KEY value.
        Double authThrottleExpBase = configurationSource.getDouble(MasterConfigDoubleKeysEnum.AUTH_THROTTLE_EXP_BASE);
        if (authThrottleExpBase != null) {
            if (Math.abs(authThrottleExpBase) > 1.0) {
                this.authThrottleExpBase = Math.abs(authThrottleExpBase);
            }
        }

        // Check and see if we need to change the AUTH_THROTTLE_DELTA_KEY value.
        Double authThrottleDeltaStr = configurationSource.getDouble(MasterConfigDoubleKeysEnum.AUTH_THROTTLE_DELTA);
        if (authThrottleDeltaStr != null) {
            if (Math.abs(authThrottleDelta) > 0.0) {
                this.authThrottleDelta = Math.abs(authThrottleDelta);
            }
        }
    }
    
    @Override
    public synchronized void loginAttempted(String username) throws AuthenticationThrottleException {
        long waitSeconds = 0;
        // get the remaining throttle wait time
        AuthenticationThrottle authThrottle = authThrottleMap.get(username);
        if (authThrottle != null) {
            waitSeconds = authThrottle.getThrottleDurationSeconds();
        }
       
        // if login attempted before the wait time elapsed, throw an exception
        if (waitSeconds > 0) {
            throw new AuthenticationThrottleException(waitSeconds);
        } else {
            if (authThrottle == null) {
                authThrottle = new AuthenticationThrottle();
                authThrottleMap.put(username, authThrottle);
            } else {
                authThrottle.updateAuthThrottle();
            }            
        }
    }

    @Override
    public synchronized void loginSucceeded(String username) {
        authThrottleMap.remove(username);
    }

    @Override
    public synchronized AuthenticationThrottleDto getAuthenticationThrottleData(String username) {

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

    public synchronized void setAuthThrottleExpBase(double authThrottleExpBase) {
        this.authThrottleExpBase = authThrottleExpBase;
    }

    public void setAuthThrottleDelta(double authThrottleDelta){
        this.authThrottleDelta = authThrottleDelta;
    }
    
    public synchronized void setAbandonedAuthThrottleDays(int abandonedAuthThrottleDays) {
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
            return lastFailedLoginTime + (Math.round(Math.pow(authThrottleExpBase, retryCount) + authThrottleDelta) * 1000);
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