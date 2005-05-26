package com.cannontech.notif.voice.callstates;

import com.cannontech.notif.voice.Call;

/**
 * 
 */
public class Connecting extends CallState {

    public boolean isDone() {
        return false;
    }
    
    public boolean isReady() {
        return false;
    }

    public void handleTimeout(Call call) {
        call.changeState(new Unconfirmed("timeout"));
    }
    
    public String toString() {
        return "Connecting";
    }
    

}
