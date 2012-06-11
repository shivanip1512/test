package com.cannontech.core.authentication.service.impl;

import java.util.concurrent.TimeUnit;

import org.joda.time.Duration;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.exception.AuthenticationThrottleException;
import com.cannontech.core.authentication.model.PasswordPolicy;
import com.cannontech.core.authentication.service.AuthenticationThrottleDto;
import com.cannontech.core.authentication.service.PasswordPolicyService;
import com.cannontech.core.authentication.service.StaticAuthenticationThrottleService;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

public class StaticAuthenticationThrottleServiceImpl implements StaticAuthenticationThrottleService {

    @Autowired private PasswordPolicyService passwordPolicyService; 
    @Autowired private YukonUserDao yukonUserDao;
    
    // map of username and AuthenticationThrottle data
    private Cache<String, AuthenticationThrottle> authThrottleMap = CacheBuilder.newBuilder().expireAfterWrite(1, TimeUnit.HOURS).build();

    private static final int DEFAULT_LOCKOUT_THRESHOLD = 5;
    private Duration DEFAULT_LOCKOUT_DURATION = Duration.standardMinutes(20);
    
    @Override
    public synchronized void loginAttempted(String username) throws AuthenticationThrottleException {
        LiteYukonUser attemptedLoginUser = yukonUserDao.findUserByUsername(username);
        PasswordPolicy passwordPolicy = null; 
        if (attemptedLoginUser != null) {
                passwordPolicyService.findPasswordPolicy(attemptedLoginUser);
        }
        
        int lockoutThreshold = (passwordPolicy != null) ? passwordPolicy.getLockoutThreshold() : DEFAULT_LOCKOUT_THRESHOLD;
        Duration lockoutDuration = (passwordPolicy != null) ? passwordPolicy.getLockoutDuration() : DEFAULT_LOCKOUT_DURATION;
        
        AuthenticationThrottle authThrottle = authThrottleMap.getIfPresent(username);
        if (authThrottle != null && authThrottle.isLockedOut()) {
            throw new AuthenticationThrottleException(authThrottle.getThrottleDurationSeconds());
        } else {
            if (authThrottle == null) {
                authThrottle = new AuthenticationThrottle(lockoutDuration, lockoutThreshold);
                authThrottleMap.put(username, authThrottle);
            } else {
                authThrottle.updateAuthLockout();
            }
        }
    }

    @Override
    public synchronized void loginSucceeded(String username) {
        authThrottleMap.invalidate(username);
    }

    @Override
    public AuthenticationThrottleDto getAuthenticationThrottleData(String username) {
        
        AuthenticationThrottleDto authThrottleDto = null;
        AuthenticationThrottle authThrottle = authThrottleMap.getIfPresent(username);
        if (authThrottle != null) {
            authThrottleDto = authThrottle.getAuthenticationThrottleData();
        }
        return authThrottleDto;
    }

    @Override
    public void removeAuthenticationThrottle(String username) {
        authThrottleMap.invalidate(username);
    }

    @Override
    public synchronized void resetAll() {
        authThrottleMap.invalidateAll();
    }

    public class AuthenticationThrottle {

        private Duration lockoutDuration;
        private int lockoutThreshold;
        private Instant lastFailedLoginTime;
        private long retryCount;

        public AuthenticationThrottle(Duration lockoutDuration, int lockoutThreshold) {
            this.lockoutDuration = lockoutDuration;
            this.lockoutThreshold = lockoutThreshold;
            this.lastFailedLoginTime = Instant.now();
            this.retryCount = 0;
        }

        public Instant getLastFailedLoginTime() {
            return lastFailedLoginTime;
        }
        public Duration getLockoutDuration() {
            return lockoutDuration;
        }
        public long getRetryCount() {
            return retryCount;
        }

        public void updateAuthLockout() {
            this.lastFailedLoginTime = Instant.now();
            this.retryCount++;
        }

        public boolean isLockedOut() {
            return retryCount >= lockoutThreshold;
        }
        
        public Instant getThrottleEndtime() {
            if (isLockedOut()) {
                return lastFailedLoginTime.plus(lockoutDuration);
            }
            
            return null;
        }

        public long getThrottleDurationSeconds() {
            // throttle expires if current time >= throttleEndtime
            long throttleSeconds = new Duration(Instant.now(), getThrottleEndtime()).getStandardSeconds();
            throttleSeconds = (throttleSeconds < 0) ? 0 : throttleSeconds;

            return throttleSeconds;
        }

        
        public AuthenticationThrottleDto getAuthenticationThrottleData() {
            AuthenticationThrottleDto authThrottleDto = new AuthenticationThrottleDto();
            authThrottleDto.setLastFailedLoginTime(lastFailedLoginTime.toDate());
            authThrottleDto.setRetryCount(retryCount);
            
            if (getThrottleEndtime() != null) {
                authThrottleDto.setThrottleEndtime(getThrottleEndtime().toDate());
            }
            authThrottleDto.setThrottleDurationSeconds(lockoutDuration.getStandardSeconds());
            
            return authThrottleDto;
        }
    }
}