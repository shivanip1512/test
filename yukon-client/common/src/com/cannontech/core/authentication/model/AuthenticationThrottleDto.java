package com.cannontech.core.authentication.model;

import java.util.Date;

public class AuthenticationThrottleDto implements Comparable<AuthenticationThrottleDto> {

    private Date lastFailedLoginTime;
    private long retryCount;
    private Date throttleEndtime;
    private long throttleDurationSeconds;

    public Date getLastFailedLoginTime() {
        return lastFailedLoginTime;
    }

    public void setLastFailedLoginTime(Date lastFailedLoginTime) {
        this.lastFailedLoginTime = lastFailedLoginTime;
    }

    public long getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(long retryCount) {
        this.retryCount = retryCount;
    }

    public Date getThrottleEndtime() {
        return throttleEndtime;
    }

    public void setThrottleEndtime(Date throttleEndtime) {
        this.throttleEndtime = throttleEndtime;
    }

    public long getThrottleDurationSeconds() {
        return throttleDurationSeconds;
    }

    public void setThrottleDurationSeconds(long throttleDurationSeconds) {
        this.throttleDurationSeconds = throttleDurationSeconds;
    }

    public long getActualThrottleDuration() {
        return throttleEndtime.getTime() - lastFailedLoginTime.getTime();
    }
    
    @Override
    public String toString() {
        return String.format("AuthenticationThrottleDto [lastFailedLoginTime=%s, retryCount=%s, throttleEndtime=%s," 
                + " throttleDurationSeconds=%s]", lastFailedLoginTime, retryCount, throttleEndtime,
                throttleDurationSeconds);
    }
    
    @Override
    public int compareTo(AuthenticationThrottleDto anotherThrottle) {
        int compareResult = 0;

        if (this.retryCount > anotherThrottle.retryCount 
                || this.throttleEndtime.compareTo(anotherThrottle.getThrottleEndtime()) > 0 
                || this.getActualThrottleDuration() > anotherThrottle.getActualThrottleDuration()) {
            compareResult = 1;
        } else if (this.retryCount < anotherThrottle.retryCount 
                || this.throttleEndtime.compareTo(anotherThrottle.getThrottleEndtime()) < 0 
                || this.getActualThrottleDuration() < anotherThrottle.getActualThrottleDuration()) {
            compareResult = -1;
        }// otherwise they are equal

        return compareResult;
    }
}