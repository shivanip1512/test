package com.cannontech.notif.voice;

import org.apache.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;



public abstract class Dialer {
    private Logger log = YukonLogManager.getLogger(Dialer.class);

    private final int retryDelayMs;
    private final int maxTries;

    private final int postCallSleepMs;

    public Dialer(int retryDelayMs, int maxTries, int postCallSleepMs) {
        this.retryDelayMs = retryDelayMs;
        this.maxTries = maxTries;
        this.postCallSleepMs = postCallSleepMs;
    }

    public void makeCall(Call call) {
        try {
            int count = 1;
            
            dialCall(call);
            while (call.isRetry()) {       
                if (count >= maxTries) {
                    log.info("Retry count exceeded for: " + call);
                    call.handleFailure("too many retries");
                    return;
                }
                log.debug("Retry " + count + " in " + retryDelayMs + "ms for: " + call);
                count++;
                Thread.sleep(retryDelayMs);
                dialCall(call);
            }

        } catch (InterruptedException e) {
            log.warn("caught InterruptedException", e);
            call.handleFailure("interrupted exception in makeCall: " + e.getMessage());
        } finally {
            call.handleCompletion();
            try {
                log.debug("sleeping for " + postCallSleepMs + "ms");
                Thread.sleep(postCallSleepMs);
            } catch (InterruptedException e) {
            }
        }
    }

    abstract protected void dialCall(Call call);
    
    abstract public String toString();
}
