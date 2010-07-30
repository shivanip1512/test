package com.cannontech.amr.crf.service;

import java.util.concurrent.CountDownLatch;

import org.apache.log4j.Logger;

import com.cannontech.amr.crf.message.CrfMeterReadingReplyType;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.core.dynamic.PointValueHolder;

public class WaitableCrfMeterReadCompletionCallback implements CrfMeterReadCompletionCallback{
    
    private Logger log = YukonLogManager.getLogger(WaitableCrfMeterReadCompletionCallback.class);
    
    private CountDownLatch statusLatch = new CountDownLatch(1);
    private CountDownLatch completeLatch = new CountDownLatch(1);
    
    private CrfMeterReadCompletionCallback delegate;
    
    public WaitableCrfMeterReadCompletionCallback(CrfMeterReadCompletionCallback delegate) {
        this.delegate = delegate;
    }

    @Override
    public void complete() {
        delegate.complete();
        completeLatch.countDown();
    }

    @Override
    public void receivedError(CrfMeterReadingReplyType reason) {
        delegate.receivedError(reason);
    }

    @Override
    public void receivedValue(PointValueHolder value) {
        delegate.receivedValue(value);
    }

    @Override
    public void receivedStatus(CrfMeterReadingReplyType status) {
        delegate.receivedStatus(status);
        statusLatch.countDown();
    }
    
    public void waitForStatusResponse() throws InterruptedException {
        
        long startTime = System.currentTimeMillis();
        log.debug("Starting await status response on " + Thread.currentThread() + " at " + startTime);
        
        statusLatch.await();
        
        log.debug("Finished await status response on " + Thread.currentThread());
    }
    
    public void waitForCompletion() throws InterruptedException {
        
        long startTime = System.currentTimeMillis();
        log.debug("Starting await completion on " + Thread.currentThread() + " at " + startTime);
        
        completeLatch.await();
        
        log.debug("Finished await completion on " + Thread.currentThread());
    }

}