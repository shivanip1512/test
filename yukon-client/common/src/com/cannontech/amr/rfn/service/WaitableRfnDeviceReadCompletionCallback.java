package com.cannontech.amr.rfn.service;

import java.util.concurrent.CountDownLatch;

import org.apache.log4j.Logger;
import org.springframework.context.MessageSourceResolvable;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.core.dynamic.PointValueHolder;

public class WaitableRfnDeviceReadCompletionCallback<T1,T2> implements RfnDeviceReadCompletionCallback<T1, T2> {
    
    private Logger log = YukonLogManager.getLogger(WaitableRfnDeviceReadCompletionCallback.class);
    
    private CountDownLatch statusLatch = new CountDownLatch(1);
    private CountDownLatch completeLatch = new CountDownLatch(1);
    
    private RfnDeviceReadCompletionCallback<T1, T2> delegate;
    
    public WaitableRfnDeviceReadCompletionCallback(RfnDeviceReadCompletionCallback<T1, T2> delegate) {
        this.delegate = delegate;
    }

    @Override
    public void complete() {
        delegate.complete();
        completeLatch.countDown();
    }

    @Override
    public void receivedStatusError(T1 reason) {
        delegate.receivedStatusError(reason);
        statusLatch.countDown();
    }

    @Override
    public void receivedData(PointValueHolder value) {
        delegate.receivedData(value);
    }

    @Override
    public void receivedStatus(T1 status) {
        delegate.receivedStatus(status);
        statusLatch.countDown();
    }
    
    @Override
    public void receivedDataError(T2 replyType) {
        delegate.receivedDataError(replyType);
    }
    
    @Override
    public void processingExceptionOccured(MessageSourceResolvable message) {
        log.error(message);
    }
    
    public void waitForStatusResponse() throws InterruptedException {
        
        log.debug("Starting await status response");
        
        statusLatch.await();
        
        log.debug("Finished await status response");
    }
    
    public void waitForCompletion() throws InterruptedException {
        
        log.debug("Starting await completion");
        
        completeLatch.await();
        
        log.debug("Finished await completion");
    }

}