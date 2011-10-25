package com.cannontech.amr.rfn.service;

import java.util.concurrent.CountDownLatch;

import org.apache.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;

public abstract class WaitableRfnMeterDisconnectCallback implements RfnMeterDisconnectCallback {

    private Logger log = YukonLogManager.getLogger(WaitableRfnMeterDisconnectCallback.class);

    private CountDownLatch completeLatch = new CountDownLatch(1);

    @Override
    public final void complete() {
        doComplete();
        completeLatch.countDown();
    }

    protected void doComplete() {
        
    }

    public void waitForCompletion() throws InterruptedException {

        log.debug("Starting await completion");

        completeLatch.await();

        log.debug("Finished await completion");
    }

}