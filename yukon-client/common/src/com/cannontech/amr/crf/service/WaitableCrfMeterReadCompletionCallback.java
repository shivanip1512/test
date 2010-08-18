package com.cannontech.amr.crf.service;

import java.util.concurrent.CountDownLatch;

import org.apache.log4j.Logger;

import com.cannontech.amr.crf.message.CrfMeterReadingDataReplyType;
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
    public void receivedStatusError(CrfMeterReadingReplyType reason) {
        delegate.receivedStatusError(reason);
    }

    @Override
    public void receivedData(PointValueHolder value) {
        delegate.receivedData(value);
    }

    @Override
    public void receivedStatus(CrfMeterReadingReplyType status) {
        delegate.receivedStatus(status);
        statusLatch.countDown();
    }
    
    @Override
    public void receivedDataError(CrfMeterReadingDataReplyType replyType) {
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