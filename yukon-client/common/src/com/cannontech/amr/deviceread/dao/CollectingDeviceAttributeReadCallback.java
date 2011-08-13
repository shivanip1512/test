package com.cannontech.amr.deviceread.dao;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.core.dynamic.PointValueHolder;
import com.google.common.collect.Lists;

public class CollectingDeviceAttributeReadCallback implements DeviceAttributeReadCallback {

    private CountDownLatch latch = new CountDownLatch(1);
    private List<DeviceAttributeReadError> messages = Lists.newArrayList();
    
    @Override
    public void complete() {
        latch.countDown();
    }

    @Override
    public synchronized void receivedError(PaoIdentifier pao, DeviceAttributeReadError error) {
        // method synchronized to protect list, we know it can't be read until
        // we are done doing all of the adding
        messages.add(error);
    }

    @Override
    public void receivedValue(PaoIdentifier pao, PointValueHolder value) {
    }
    
    @Override
    public void receivedLastValue(PaoIdentifier pao) {
    }
    
    @Override
    public synchronized void receivedException(DeviceAttributeReadError exception) {
        messages.add(exception);
    }
    
    public List<DeviceAttributeReadError> getMessages() {
        waitForCompletion();
        return messages;
    }
    
    public boolean isSuccess() {
        waitForCompletion();
        return messages.isEmpty();
    }

    public void waitForCompletion() {
        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


}
