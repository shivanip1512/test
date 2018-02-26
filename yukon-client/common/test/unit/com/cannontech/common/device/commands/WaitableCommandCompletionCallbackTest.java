package com.cannontech.common.device.commands;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

import com.cannontech.common.device.commands.impl.WaitableCommandCompletionCallback;

public class WaitableCommandCompletionCallbackTest {
    @Test
    public void testWaitForCompletion() {
        final AtomicInteger timeoutCount = new AtomicInteger(0);
        final AtomicInteger interruptCount = new AtomicInteger(0);
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                WaitableCommandCompletionCallback<CommandRequestBase> completionCallback =
                    new WaitableCommandCompletionCallback<>(new CollectingCommandCompletionCallback(), 10, 20);
                try {
                    completionCallback.waitForCompletion();
                    fail("completed normally");
                } catch (InterruptedException e) {
                    System.out.println("InterruptedException");
                    interruptCount.incrementAndGet();
                } catch (TimeoutException e) {
                    System.out.println("TimeoutException");
                    timeoutCount.incrementAndGet();
                }
            }
        };
        
        int threadsToStart = 10;
        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < threadsToStart; ++i) {
            Thread thread = new Thread(runnable);
            threads.add(thread);
            System.out.println("Starting thread " + i);
            thread.start();
            try {
                Thread.sleep(2060);
            } catch (InterruptedException e) {
                fail("this shouldn't happen 1");
            }
        }
        
        try {
            Thread.sleep(20000);
        } catch (InterruptedException e1) {
            fail("this shouldn't happen 2");
        }
        
        // clean up threads, everything should have timed out by now
        for (Thread thread : threads) {
            try {
                thread.interrupt();
                thread.join(100);
                System.out.println("Thread joined");
            } catch (InterruptedException e) {
                fail("this shouldn't happen 3");
            }
        }
        
        assertEquals(threadsToStart, timeoutCount.get());
        assertEquals(0, interruptCount.get());
    }

}
