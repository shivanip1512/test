package com.cannontech.notif.voice;

import com.cannontech.notif.voice.callstates.TooManyRetries;


public abstract class Dialer {

    private static final int RETRY_DELAY = 3000;
    private static final int MAX_TRIES = 15;

    public void makeCall(Call call) {
        try {
            int count = 1;
            
            dialCall(call);
            if (call.isRetry()) {
                Thread.sleep(RETRY_DELAY);
                while (call.isReady()) {       
                    count++;
                    dialCall(call);
                    if (count >= MAX_TRIES) {
                        call.changeState(new TooManyRetries());
                        break;
                    }
                    Thread.sleep(RETRY_DELAY);
                }
            }
        } catch (InterruptedException e) {
            call.changeState(new com.cannontech.notif.voice.callstates.UnknownError(e));
        }
    }

    abstract protected void dialCall(Call call);
    
    abstract public String toString();
}
