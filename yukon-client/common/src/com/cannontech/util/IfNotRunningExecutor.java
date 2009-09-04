package com.cannontech.util;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * This object is designed to be shared by multiple threads that
 * occasionally need to run a task that is common to all threads.
 * 
 * For example, reloading a cache may need to be done every 30 seconds,
 * but it may be desirable for the other threads to keep
 * using the old data while the new data is being loaded.
 * 
 * Important: if two threads invoke the runIfNotAlreadyRunning method
 * at the same time, only one of the runners will run! For that reason
 * it probably only makes sense that the runner be the same every time.
 *
 */
public class IfNotRunningExecutor {
    private AtomicBoolean running = new AtomicBoolean(false);
    
    public void runIfNotAlreadyRunning(Runnable runner) {
        boolean iAmTheRunner = running.compareAndSet(false, true);
        if (iAmTheRunner) {
            try {
                runner.run();
            } finally {
                running.set(false);
            }
        }
    }
}
