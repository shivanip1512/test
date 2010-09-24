package com.cannontech.amr.rfn.service;

import java.util.concurrent.CountDownLatch;

import org.apache.log4j.Logger;

import com.cannontech.amr.rfn.message.disconnect.RfnMeterDisconnectConfirmationReplyType;
import com.cannontech.amr.rfn.message.disconnect.RfnMeterDisconnectInitialReplyType;
import com.cannontech.clientutils.YukonLogManager;

public class WaitableRfnMeterDisconnectCallback implements RfnMeterDisconnectCallback{
    
    private Logger log = YukonLogManager.getLogger(WaitableRfnMeterDisconnectCallback.class);
    
    private CountDownLatch initialLatch = new CountDownLatch(1);
    private CountDownLatch completeLatch = new CountDownLatch(1);
    
    private RfnMeterDisconnectCallback delegate;
    
    public WaitableRfnMeterDisconnectCallback(RfnMeterDisconnectCallback delegate) {
        this.delegate = delegate;
    }
    
    @Override
    public void complete() {
        delegate.complete();
        completeLatch.countDown();
    }

    @Override
    public void receivedInitialError(RfnMeterDisconnectInitialReplyType replyType) {
        delegate.receivedInitialError(replyType);
    }
    
    @Override
    public void receivedInitialReply(RfnMeterDisconnectInitialReplyType replyType) {
        delegate.receivedInitialReply(replyType);
        initialLatch.countDown();
    }
    
    @Override
    public void receivedConfirmationError(RfnMeterDisconnectConfirmationReplyType replyType) {
        delegate.receivedConfirmationError(replyType);
    }
    
    @Override
    public void receivedConfirmationReply(RfnMeterDisconnectConfirmationReplyType replyType) {
        delegate.receivedConfirmationReply(replyType);
    }

    @Override
    public void processingExceptionOccured(String message) {
        log.error(message);
    }
    
public void waitForInitialResponse() throws InterruptedException {
        
        log.debug("Starting await initial response");
        
        initialLatch.await();
        
        log.debug("Finished await initial response");
    }
    
    public void waitForCompletion() throws InterruptedException {
        
        log.debug("Starting await completion");
        
        completeLatch.await();
        
        log.debug("Finished await completion");
    }

}