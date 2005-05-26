package com.cannontech.notif.voice.callstates;

import com.cannontech.notif.voice.Call;

/**
 * 
 */
public abstract class CallState {
    public abstract boolean isDone();
    public abstract boolean isReady();
    public void handleTimeout(Call call) {};

}
