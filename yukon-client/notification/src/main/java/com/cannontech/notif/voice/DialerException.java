package com.cannontech.notif.voice;

/**
 * This indicates a temporary error.
 * When this error is received, the call
 * will be retried.
 */
public class DialerException extends Exception {
    private static final long serialVersionUID = 1L;

    public DialerException(String msg) {
        super(msg);
    }

}
