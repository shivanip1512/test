package com.cannontech.common.device.commands.impl;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.log4j.Logger;
import org.springframework.core.style.ToStringCreator;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.commands.CommandCompletionCallback;

public final class WaitableCommandCompletionCallback<T> implements CommandCompletionCallback<T> {

    private final Object lock = new Object();
    private Logger log = YukonLogManager.getLogger(WaitableCommandCompletionCallback.class);
    private volatile long lastResultReceivedTime = 0;
    private long latestFinishTime = 0;
    
    private CommandCompletionCallback<T> delegate;
    private volatile boolean complete;

    private long betweenResultsMax;
    private long totalMax;

    public WaitableCommandCompletionCallback(CommandCompletionCallback<T> delegate,
            long betweenResultsMax, long totalMax) {
        this.delegate = delegate;
        this.betweenResultsMax = betweenResultsMax;
        this.totalMax = totalMax;
    }

    private void kick() {
        synchronized (lock) {
            this.lastResultReceivedTime = System.currentTimeMillis();
            lock.notify();
        }
    }

    public void waitForCompletion() throws InterruptedException, TimeoutException {
    	
    	long startTime = System.currentTimeMillis();
        this.latestFinishTime = startTime + TimeUnit.MILLISECONDS.convert(totalMax, TimeUnit.SECONDS);
        this.lastResultReceivedTime = startTime;
        log.debug("Starting await on " + Thread.currentThread() + " for " + totalMax);
        
        synchronized (lock) {
            while (!complete) {
            	
            	long currentTime = System.currentTimeMillis();
            	long latestBetweenResultsFinishTime = this.lastResultReceivedTime + TimeUnit.MILLISECONDS.convert(betweenResultsMax, TimeUnit.SECONDS);
            	
                if (currentTime >= this.latestFinishTime) {
                    throw new TimeoutException("The entire commander command execution did not complete within " + totalMax + " seconds");
                }
                if (currentTime >= latestBetweenResultsFinishTime) {
                    throw new TimeoutException("The commander command execution did not receive a result for " + betweenResultsMax + " seconds");
                }
                
                // determine how long to wait... should be the shortest of A or B
                // time until command must fully complete
                long timeoutA = this.latestFinishTime - currentTime;
                // time until we must hear the next response
                long timeoutB = latestBetweenResultsFinishTime - currentTime;
                long timeout = Math.min(timeoutA, timeoutB);
                
                lock.wait(timeout);
            }
        }
        log.debug("Finished await on " + Thread.currentThread());
    }
    
    @Override
    public void receivedIntermediateError(T command, SpecificDeviceErrorDescription error) {
        delegate.receivedIntermediateError(command, error);
        kick();
    }
    
    @Override
    public void receivedLastError(T command, SpecificDeviceErrorDescription error) {
        delegate.receivedLastError(command, error);
        kick();
    }
    
    @Override
    public void receivedLastResultString(T command, String value) {
        delegate.receivedLastResultString(command, value);
        kick();
    }
    
    @Override
    public void receivedIntermediateResultString(T command, String value) {
        delegate.receivedIntermediateResultString(command, value);
        kick();
    }
    
    @Override
    public void cancel() {
        delegate.cancel();
    }
    
    @Override
    public void complete() {
        delegate.complete();
        complete = true;
        kick();
    }
    
    @Override
    public void processingExceptionOccured(String reason) {
        delegate.processingExceptionOccured(reason);
    }
    
    @Override
    public void receivedValue(T command, com.cannontech.core.dynamic.PointValueHolder value) {
        delegate.receivedValue(command, value);
    };
    

    @Override
    public String toString() {
        return new ToStringCreator(this).toString();
    }

}