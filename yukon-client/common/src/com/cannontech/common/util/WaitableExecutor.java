package com.cannontech.common.util;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;


/**
 * Someday I'd like to rewrite this so it doesn't require a big list of futures.
 * But that's too hard today, at least this lays out the usage.
 *
 */
public class WaitableExecutor implements Executor {
    private final Logger log = YukonLogManager.getLogger(WaitableExecutor.class);
    private ExecutorService executorService;
    private ConcurrentLinkedQueue<Future<?>> futures = new ConcurrentLinkedQueue<Future<?>>();
    private AtomicBoolean waitStarted = new AtomicBoolean(false);

    @Override
    public void execute(Runnable command) {
        Future<?> future = executorService.submit(command);
        futures.add(future);
        if (waitStarted.get()) {
            throw new IllegalStateException("await was called before execute finished");
        }
    }
    
    public void await() throws InterruptedException, ExecutionException {
        boolean success = waitStarted.compareAndSet(false, true);
        if (!success) {
            throw new IllegalStateException("await may only be called once");
        }
        
        ExecutionException lastException = null;
        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (ExecutionException e) {
                if (lastException != null) {
                    log.warn("Overwriting last exception", lastException);
                }
                lastException = e;
            }
        }
        if (lastException != null) {
            throw lastException;
        }
    }

    public WaitableExecutor(ExecutorService executorService) {
        super();
        this.executorService = executorService;
    }

}
