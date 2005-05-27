package com.cannontech.notif.test;

import com.cannontech.notif.voice.Call;
import com.cannontech.notif.voice.Dialer;
import com.cannontech.notif.voice.callstates.Connecting;

/**
 * 
 */
public class TestDialer extends Dialer {

    protected void dialCall(Call call) {
        try {
            call.changeState(new Connecting());
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public String toString() {
        return "TestDialer";
    }

}
