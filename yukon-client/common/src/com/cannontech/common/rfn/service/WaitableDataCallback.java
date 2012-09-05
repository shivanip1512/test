package com.cannontech.common.rfn.service;

import java.util.concurrent.CountDownLatch;

import org.apache.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;

/**
 * Addes thread blocking functionality for {@link DataCallback}
 */
public abstract class WaitableDataCallback<T> implements DataCallback<T> {

    private final static Logger log = YukonLogManager.getLogger(WaitableDataCallback.class);

    private CountDownLatch completeLatch = new CountDownLatch(1);

    @Override
    public final void complete() {
        completeLatch.countDown();
    }

    public void waitForCompletion() throws InterruptedException {

        log.debug("Starting await completion");

        completeLatch.await();

        log.debug("Finished await completion");
    }

}