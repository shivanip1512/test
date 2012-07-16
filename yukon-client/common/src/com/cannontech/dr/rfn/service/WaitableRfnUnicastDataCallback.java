package com.cannontech.dr.rfn.service;

import java.util.concurrent.CountDownLatch;

import org.apache.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.dr.rfn.message.unicast.RfnExpressComUnicastDataReplyType;
import com.cannontech.dr.rfn.message.unicast.RfnExpressComUnicastReplyType;

public class WaitableRfnUnicastDataCallback implements RfnUnicastDataCallback {

    private static final Logger log = YukonLogManager.getLogger(WaitableRfnUnicastDataCallback.class);
    
    private CountDownLatch statusLatch = new CountDownLatch(1);
    private CountDownLatch completeLatch = new CountDownLatch(1);
    
    private RfnUnicastDataCallback delegate;
    
    public WaitableRfnUnicastDataCallback(RfnUnicastDataCallback delegate) {
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
    public void receivedData(Object value) {
        delegate.receivedData(value);
    }

    @Override
    public void receivedStatus(RfnExpressComUnicastReplyType status) {
        delegate.receivedStatus(status);
        statusLatch.countDown();
    }
    
    @Override
    public void receivedDataError(RfnExpressComUnicastDataReplyType replyType) {
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