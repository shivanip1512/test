package com.cannontech.common.util;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.Delayed;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicLong;

public class ScheduledExecutorMock implements ScheduledExecutor {
    private long currentTime = System.currentTimeMillis();
    private boolean immediateMode = true;
    private PriorityQueue<ScheduledFutureBase> tasks = new PriorityQueue<ScheduledFutureBase>();
    
    public ScheduledExecutorMock(boolean immediateMode) {
        this.immediateMode = immediateMode;
    }

    public ScheduledFuture<?> schedule(final Runnable command, long delay, TimeUnit unit) {
        ScheduledFutureBase<Object> task = new ScheduledFutureBase<Object>(Executors.callable(command), delay, unit);
        tasks.add(task);
        return task;
    }

    public <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit) {
        ScheduledFutureBase<V> task = new ScheduledFutureBase<V>(callable, delay, unit);
        tasks.add(task);
        return task;
    }

    public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period,
                                                  TimeUnit unit) {
        ScheduledFutureBase<Object> task = new ScheduledFutureBase<Object>(Executors.callable(command), initialDelay, period, unit);
        tasks.add(task);
        return task;
    }

    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay,
                                                     long delay, TimeUnit unit) {
        ScheduledFutureBase<Object> task = new ScheduledFutureBase<Object>(Executors.callable(command), initialDelay, delay, unit);
        tasks.add(task);
        return task;
    }

    public void execute(Runnable command) {
        if (immediateMode) {
            command.run();
        } else {
            ScheduledFutureBase<Object> task = new ScheduledFutureBase<Object>(Executors.callable(command), 0, TimeUnit.MILLISECONDS);
            tasks.add(task);
        }
    }
    
    private static final AtomicLong sequencer = new AtomicLong(0);
    
    private final class ScheduledFutureBase<T> implements ScheduledFuture<T> {
        private long timeMillis;
        private final Callable<T> runner;
        private T result = null;
        private Exception heldException = null;
        private long periodMillis = -1;
        private boolean done = false;
        

        private boolean cancelled;
        private long sequenceNumber;

        public ScheduledFutureBase(Callable<T> runner, long delay, TimeUnit unit) {
            this.runner = runner;
            this.timeMillis = currentTime + TimeUnit.MILLISECONDS.convert(delay, unit);
            sequenceNumber = sequencer.getAndIncrement();
        }

        public ScheduledFutureBase(Callable<T> runner, long delay, long period, TimeUnit unit) {
            this.runner = runner;
            this.timeMillis = currentTime + TimeUnit.MILLISECONDS.convert(delay, unit);
            this.periodMillis = period;
            sequenceNumber = sequencer.getAndIncrement();
        }
        
        public long getDelay(TimeUnit unit) {
            return unit.convert(timeMillis - currentTime, TimeUnit.MILLISECONDS);
        }

        public boolean cancel(boolean mayInterruptIfRunning) {
            cancelled = true;
            return tasks.remove(this);
        }

        public T get() throws InterruptedException,ExecutionException {
            if (cancelled) {
                throw new CancellationException();
            }
            if (!isDone()) {
                throw new UnsupportedOperationException("This mock object won't block a get request");
            }
            if (heldException != null) {
                throw new ExecutionException(heldException);
            }
            return result;
        }

        public T get(long timeout,TimeUnit unit) throws InterruptedException,ExecutionException,TimeoutException{
            return get();
        }

        public boolean isCancelled() {
            return cancelled;
        }

        public boolean isDone() {
            return done;
        }

        public void execute() {
            try {
                result = runner.call();
                done = true;
            } catch (Exception e) {
                heldException = e;
            }
            if (periodMillis > 0) {
                timeMillis = currentTime + periodMillis;
                tasks.add(this);
            }
        }

        public int compareTo(Delayed other) {
            if (other == this) // compare zero ONLY if same object
                return 0;
            ScheduledFutureBase<?> x = (ScheduledFutureBase<?>)other;
            long diff = timeMillis - x.timeMillis;
            if (diff < 0)
                return -1;
            else if (diff > 0)
                return 1;
            else if (sequenceNumber < x.sequenceNumber)
                return -1;
            else
                return 1;
        }
    }

    
    public void doNextTask() {
        ScheduledFutureBase guy = tasks.remove();
        doTask(guy);
    }

    private void doTask(ScheduledFutureBase guy) {
        long delay = guy.getDelay(TimeUnit.MILLISECONDS);
        currentTime += delay;
        guy.execute();
    }
    
    public void doAllTasks() {
        List<ScheduledFutureBase> tempList = new ArrayList<ScheduledFutureBase>(tasks.size());
        ScheduledFutureBase sfb = tasks.poll();
        while (sfb != null) {
            tempList.add(sfb);
            sfb = tasks.poll();
        }
        
        for (ScheduledFutureBase sfb2 : tempList) {
            doTask(sfb2);
        }
        
    }
    
    public int getTaskQueueCount() {
        return tasks.size();
    }

}
