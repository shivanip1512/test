package com.cannontech.amr.rfn.service;

import java.util.concurrent.CountDownLatch;

import org.apache.log4j.Logger;

import com.cannontech.amr.rfn.message.read.RfnMeterReadingDataReplyType;
import com.cannontech.amr.rfn.message.read.RfnMeterReadingReplyType;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.core.dynamic.PointValueHolder;

public class WaitableRfnMeterReadCompletionCallback implements RfnMeterReadCompletionCallback{
    
    private Logger log = YukonLogManager.getLogger(WaitableRfnMeterReadCompletionCallback.class);
    
    private CountDownLatch statusLatch = new CountDownLatch(1);
    private CountDownLatch completeLatch = new CountDownLatch(1);
    
    private RfnMeterReadCompletionCallback delegate;
    
    public WaitableRfnMeterReadCompletionCallback(RfnMeterReadCompletionCallback delegate) {
        this.delegate = delegate;
    }

    @Override
    public void complete() {
        delegate.complete();
        completeLatch.countDown();
    }

    @Override
    public void receivedStatusError(RfnMeterReadingReplyType reason) {
        delegate.receivedStatusError(reason);
    }

    @Override
    public void receivedData(PointValueHolder value) {
        delegate.receivedData(value);
    }

    @Override
    public void receivedStatus(RfnMeterReadingReplyType status) {
        delegate.receivedStatus(status);
        statusLatch.countDown();
    }
    
    @Override
    public void receivedDataError(RfnMeterReadingDataReplyType replyType) {
        delegate.receivedDataError(replyType);
    }
    
    @Override
    public void processingExceptionOccured(String message) {
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