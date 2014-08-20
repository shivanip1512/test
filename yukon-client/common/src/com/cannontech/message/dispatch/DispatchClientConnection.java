package com.cannontech.message.dispatch;

import java.util.concurrent.ArrayBlockingQueue;

import org.jfree.util.Log;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;

import com.cannontech.message.util.Message;

@ManagedResource
public class DispatchClientConnection extends com.cannontech.message.util.ClientConnection {
    private static final int maxQueueSize = 64 * 1024;
    private static final int latchOffOnRemainingCapacity = 1024;
    private static final int latchOnRemainingCapacity = maxQueueSize * 50 / 100;

    private boolean isBehindLatch = false;
    private ArrayBlockingQueue<Message> inQueue = new ArrayBlockingQueue<>(maxQueueSize);
    private final Thread worker;

    public DispatchClientConnection() {
        super("Dispatch");

        worker = new Thread() {
            @Override
            public void run() {
                while (true) {
                    Message msg;

                    try {
                        msg = inQueue.take();
                        if (inQueue.remainingCapacity() > latchOnRemainingCapacity) {
                            isBehindLatch = false;
                        }
                    } catch (InterruptedException e) {
                        logger.warn("received shutdown signal, queue size: " + inQueue.size());
                        break;
                    }

                    DispatchClientConnection.super.fireMessageEvent(msg);
                }
            }
        };
        worker.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                logger.error("Error in thread " + t, e);
            }
        });
        worker.start();
    }

    @Override
    protected void fireMessageEvent(Message msg) {
        if (logger.isTraceEnabled()) {
            logger.trace("fireMessageEvent - " + msg);
        }

        try {
            inQueue.put(msg);
            if (inQueue.remainingCapacity() < latchOffOnRemainingCapacity) {
                isBehindLatch = true;
            }
        } catch (InterruptedException e) {
            Log.error("interrupted trying to queue message " + msg);
        }

        logger.trace("fireMessageEvent - finished");
    }

    @ManagedAttribute
    public boolean isBehind() {
        return isBehindLatch;
    }

    @ManagedAttribute
    public int getQueueSize() {
        return inQueue.size();
    }

    /**
     * Ends the worker thread.
     */
    public void contextShutdown() {
        worker.interrupt();
    }
}
