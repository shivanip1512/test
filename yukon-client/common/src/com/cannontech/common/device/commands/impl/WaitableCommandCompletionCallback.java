package com.cannontech.common.device.commands.impl;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.log4j.Logger;
import org.springframework.core.style.ToStringCreator;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.commands.CollectingCommandCompletionCallback;

public class WaitableCommandCompletionCallback extends
        CollectingCommandCompletionCallback {

    private final CountDownLatch latch = new CountDownLatch(1);
    private Logger log = YukonLogManager.getLogger(WaitableCommandCompletionCallback.class);

    public void doComplete() {
        latch.countDown();
    }

    public void waitForCompletion(long time) throws InterruptedException,
            TimeoutException {
        log.debug("Starting await on " + Thread.currentThread() + " for " + time);
        boolean success = latch.await(time, TimeUnit.SECONDS);
        log .debug("Finished await on " + Thread.currentThread() + " with " + success);
        if (!success) {
            throw new TimeoutException("Commander command execution did not complete with " + time + " seconds");
        }
    }

    @Override
    public String toString() {
        return new ToStringCreator(this).toString();
    }

}