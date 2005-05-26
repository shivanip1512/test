package com.cannontech.notif.voice.callstates;

/**
 *  
 */
public class NoConnection extends FinalState {

    private String _reason;

    public NoConnection(String reason) {
        _reason = reason;
    }

    public String toString() {
        return "NoConnection  (" + _reason + ")";
    }

}
