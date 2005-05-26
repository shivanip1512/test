package com.cannontech.notif.voice.callstates;

/**
 * 
 */
public class Unconfirmed extends FinalState {

    private String _reason;
    
    public Unconfirmed() {
        _reason = "unknown";
    }

    public Unconfirmed(String reason) {
        _reason = reason;
    }
    
    public String toString() {
        return "Unconfirmed (" + _reason + ")";
    }

}
