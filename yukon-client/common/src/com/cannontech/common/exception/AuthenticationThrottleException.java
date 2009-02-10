package com.cannontech.common.exception;

/**
 * Used to indicate that authentication failed with retry duration in seconds.
 * Specifically designed to not track a cause or a message that would indicate
 * why authentication failed.
 */
public class AuthenticationThrottleException extends BadAuthenticationException {
    private long throttleSeconds;

    public AuthenticationThrottleException(long throttleSeconds) {
        super();
        this.throttleSeconds = throttleSeconds;
    }

    public long getThrottleSeconds() {
        return throttleSeconds;
    }
}
