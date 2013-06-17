package com.cannontech.cbc.commands;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import com.cannontech.message.capcontrol.model.CapControlServerResponse;

public class WaitableCommandResultCallback implements CommandResultCallback {
    
    private CountDownLatch completeLatch = new CountDownLatch(1);
    private CommandResultCallback delegate;
    
    public WaitableCommandResultCallback(CommandResultCallback delegate) {
        this.delegate = delegate;
    }

    public boolean await(long seconds) throws InterruptedException {
        boolean  gotResponse = completeLatch.await(seconds, TimeUnit.SECONDS);
        return gotResponse;
    }


    @Override
    public void recievedResponse(CapControlServerResponse response) {
        delegate.recievedResponse(response);
        completeLatch.countDown();
    }

    @Override
    public void processingExceptionOccured(String reason) {
        delegate.processingExceptionOccured(reason);
        completeLatch.countDown();
    }

    @Override
    public CapControlServerResponse getResponse() {
        return delegate.getResponse();
    }

    @Override
    public String getErrorMessage() {
        return delegate.getErrorMessage();
    }

}