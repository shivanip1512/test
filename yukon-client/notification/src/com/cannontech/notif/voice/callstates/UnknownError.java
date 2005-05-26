package com.cannontech.notif.voice.callstates;

/**
 * 
 */
public class UnknownError extends FinalState {

    private Throwable _e;

    public UnknownError(Throwable e) {
        _e = e;
    }
    
    public String toString() {
        return "UnknownError: " + _e;
    }
}