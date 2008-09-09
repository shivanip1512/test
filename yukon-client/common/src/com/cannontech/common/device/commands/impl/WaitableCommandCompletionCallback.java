package com.cannontech.common.device.commands.impl;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.log4j.Logger;
import org.springframework.core.style.ToStringCreator;

import com.cannontech.amr.errors.model.DeviceErrorDescription;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.commands.CollectingCommandCompletionCallback;

public class WaitableCommandCompletionCallback extends
        CollectingCommandCompletionCallback {

    private final Object lock = new Object();
    private Logger log = YukonLogManager.getLogger(WaitableCommandCompletionCallback.class);
    private long lastResultReceivedTime = 0;
    private long latestFinishTime = 0;

    public void doComplete() {
        super.doComplete();
        kick();
    }

    private void kick() {
        synchronized (lock) {
            lastResultReceivedTime = System.currentTimeMillis();
            lock.notify();
        }
    }

    public void waitForCompletion(long betweenResultsMax, long totalMax) throws InterruptedException,
            TimeoutException {
        this.latestFinishTime = System.currentTimeMillis() + TimeUnit.MILLISECONDS.convert(totalMax, TimeUnit.SECONDS);
        lastResultReceivedTime = System.currentTimeMillis();
        log.debug("Starting await on " + Thread.currentThread() + " for " + totalMax);
        synchronized (lock) {
            while (!isComplete()) {
                if (System.currentTimeMillis() > latestFinishTime) {
                    throw new TimeoutException("The entire commander command execution did not complete within " + totalMax + " seconds");
                }
                if (System.currentTimeMillis() > lastResultReceivedTime + TimeUnit.MILLISECONDS.convert(betweenResultsMax, TimeUnit.SECONDS)) {
                    throw new TimeoutException("The commander command execution did not receive a result for " + betweenResultsMax + " seconds");
                }
                
                // determine how long to wait... should be the shortest of A or B
                // time until command must fully complete
                long timeoutA = latestFinishTime - System.currentTimeMillis();
                // time until we must hear the next response
                long timeoutB = lastResultReceivedTime + TimeUnit.MILLISECONDS.convert(betweenResultsMax, TimeUnit.SECONDS) - System.currentTimeMillis();
                long timeout = Math.min(timeoutA, timeoutB);
                
                lock.wait(timeout);
            }
        }
        log.debug("Finished await on " + Thread.currentThread());
    }
    
    @Override
    public void receivedIntermediateError(Object command,
            DeviceErrorDescription error) {
        super.receivedIntermediateError(command, error);
        kick();
    }
    
    @Override
    public void receivedLastError(Object command, DeviceErrorDescription error) {
        super.receivedLastError(command, error);
        kick();
    }
    
    @Override
    public void receivedLastResultString(Object command, String value) {
        super.receivedLastResultString(command, value);
        kick();
    }
    
    @Override
    public void receivedIntermediateResultString(Object command, String value) {
        super.receivedIntermediateResultString(command, value);
        kick();
    }

    @Override
    public String toString() {
        return new ToStringCreator(this).toString();
    }

}