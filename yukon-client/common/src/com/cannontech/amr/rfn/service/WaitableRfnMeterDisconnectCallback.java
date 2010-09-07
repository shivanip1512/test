package com.cannontech.amr.rfn.service;

import java.util.concurrent.CountDownLatch;

import org.apache.log4j.Logger;

import com.cannontech.amr.rfn.message.disconnect.RfnMeterDisconnectReplyType;
import com.cannontech.clientutils.YukonLogManager;

public class WaitableRfnMeterDisconnectCallback implements RfnMeterDisconnectCallback{
    
    private Logger log = YukonLogManager.getLogger(WaitableRfnMeterDisconnectCallback.class);
    
    private CountDownLatch completeLatch = new CountDownLatch(1);
    
    private RfnMeterDisconnectCallback delegate;
    
    public WaitableRfnMeterDisconnectCallback(RfnMeterDisconnectCallback delegate) {
        this.delegate = delegate;
    }
    
    @Override
    public void receivedData(RfnMeterDisconnectReplyType replyType) {
        delegate.receivedData(replyType);
    }
    
    @Override
    public void receivedError(String errorCode) {
        delegate.receivedError(errorCode);
    }

    @Override
    public void complete() {
        delegate.complete();
        completeLatch.countDown();
    }

    @Override
    public void processingExceptionOccured(String message) {
        log.error(message);
    }
    
    public void waitForCompletion() throws InterruptedException {
        
        log.debug("Starting await completion");
        
        completeLatch.await();
        
        log.debug("Finished await completion");
    }

}