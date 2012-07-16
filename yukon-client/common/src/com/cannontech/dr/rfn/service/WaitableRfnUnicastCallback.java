package com.cannontech.dr.rfn.service;

import java.util.concurrent.CountDownLatch;

import org.apache.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.dr.rfn.message.unicast.RfnExpressComUnicastReplyType;

public class WaitableRfnUnicastCallback implements RfnUnicastCallback {

    private static final Logger log = YukonLogManager.getLogger(WaitableRfnUnicastCallback.class);
    
    private CountDownLatch completeLatch = new CountDownLatch(1);
    
    private RfnUnicastCallback delegate;
    
    public WaitableRfnUnicastCallback(RfnUnicastCallback delegate) {
        this.delegate = delegate;
    }
    
    @Override
    public void complete() {
        delegate.complete();
        completeLatch.countDown();
    }

    @Override
    public void receivedStatusError(RfnExpressComUnicastReplyType reason) {
        delegate.receivedStatusError(reason);
    }

    @Override
    public void receivedStatus(RfnExpressComUnicastReplyType status) {
        delegate.receivedStatus(status);
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