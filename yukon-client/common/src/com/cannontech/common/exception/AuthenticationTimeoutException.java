package com.cannontech.common.exception;

/**
 * Used to indicate that authentication failed with retry timeout duration.
 * Specifically designed to not track a cause or a message that would indicate
 * why authentication failed.
 */
public class AuthenticationTimeoutException extends BadAuthenticationException {
    private long timeoutSeconds;

    public AuthenticationTimeoutException(long timeoutSeconds) {
        super();
        this.timeoutSeconds = timeoutSeconds;
    }

    public long getTimeoutSeconds() {
        return timeoutSeconds;
    }
}
