package com.cannontech.message.dispatch;

import java.util.concurrent.ArrayBlockingQueue;

import org.jfree.util.Log;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;

import com.cannontech.message.util.Command;
import com.cannontech.message.util.Message;

@ManagedResource
public class DispatchClientConnection extends com.cannontech.message.util.ClientConnection {
    private static final int maxQueueSize = 64 * 1024;
    private static final int latchOffOnRemainingCapacity = 1024;
    private static final int latchOnRemainingCapacity = maxQueueSize * 50 / 100;

    private boolean isBehindLatch = false;

    private ArrayBlockingQueue<Message> inQueue = new ArrayBlockingQueue<>(maxQueueSize);

    public DispatchClientConnection() {
        super("Dispatch");

        Thread worker = new Thread() {
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

                    if (msg instanceof Command && ((Command) msg).getOperation() == Command.ARE_YOU_THERE) {
                        // Only instances of com.cannontech.message.dispatch.message.Command should get here
                        // and it should have a ARE_YOU_THERE operation echo it back so vangogh doesn't time
                        // out on us
                        write(msg);
                    }

                    DispatchClientConnection.super.fireMessageEvent(msg);
                }
            }
        };
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
}
