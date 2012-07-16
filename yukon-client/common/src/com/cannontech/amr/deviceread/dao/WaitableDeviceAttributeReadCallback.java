package com.cannontech.amr.deviceread.dao;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;

public abstract class WaitableDeviceAttributeReadCallback implements DeviceAttributeReadCallback {

    private Logger log = YukonLogManager.getLogger(WaitableDeviceAttributeReadCallback.class);
    private long timeoutInMillis = 120000;  //default 2mins
    private CountDownLatch latch = new CountDownLatch(1);
    
    public WaitableDeviceAttributeReadCallback() {}
    
    public WaitableDeviceAttributeReadCallback(long timeoutInMillis) {
        this.timeoutInMillis = timeoutInMillis;
    }

    @Override
    public void complete() {
        latch.countDown();
    }

    public void waitForCompletion() throws InterruptedException {
        log.debug("Starting await completion");
        latch.await(timeoutInMillis, TimeUnit.MILLISECONDS);
        log.debug("Finished await completion");
    }
}
