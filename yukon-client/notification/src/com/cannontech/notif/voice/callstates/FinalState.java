package com.cannontech.notif.voice.callstates;


/**
 * 
 */
public class FinalState extends CallState {

    public boolean isDone() {
        return true;
    }

    public boolean isReady() {
        return false;
    }

}
