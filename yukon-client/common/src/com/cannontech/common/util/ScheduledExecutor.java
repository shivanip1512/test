package com.cannontech.common.util;

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * An {@link Executor} that can schedule commands to run after a given
 * delay, or to execute periodically. 
 * 
 * <p> For some reason Sun didn't create an interface for this stuff.
 *
 * <p> The <tt>schedule</tt> methods create tasks with various delays
 * and return a task object that can be used to cancel or check
 * execution. The <tt>scheduleAtFixedRate</tt> and
 * <tt>scheduleWithFixedDelay</tt> methods create and execute tasks
 * that run periodically until cancelled.  
 *
 * <p> Commands submitted using the {@link Executor#execute} method
 * are scheduled with
 * a requested delay of zero. Zero and negative delays (but not
 * periods) are also allowed in <tt>schedule</tt> methods, and are
 * treated as requests for immediate execution.
 *
 * <p>All <tt>schedule</tt> methods accept <em>relative</em> delays and
 * periods as arguments, not absolute times or dates. It is a simple
 * matter to transform an absolute time represented as a {@link
 * java.util.Date} to the required form. For example, to schedule at
 * a certain future <tt>date</tt>, you can use: <tt>schedule(task,
 * date.getTime() - System.currentTimeMillis(),
 * TimeUnit.MILLISECONDS)</tt>. Beware however that expiration of a
 * relative delay need not coincide with the current <tt>Date</tt> at
 * which the task is enabled due to network time synchronization
 * protocols, clock drift, or other factors. 
 * 
 * @see ThreadCachingScheduledExecutorService
 *
 */
public interface ScheduledExecutor extends Executor {


    /**
     * Creates and executes a one-shot action that becomes enabled
     * after the given delay.
     * @param command the task to execute.
     * @param delay the time from now to delay execution.
     * @param unit the time unit of the delay parameter.
     * @return a Future representing pending completion of the task,
     * and whose <tt>get()</tt> method will return <tt>null</tt>
     * upon completion.
     * @throws RejectedExecutionException if task cannot be scheduled
     * for execution.
     * @throws NullPointerException if command is null
     */
    public ScheduledFuture<?> schedule(Runnable command, long delay,  TimeUnit unit);

    /**
     * Creates and executes a ScheduledFuture that becomes enabled after the
     * given delay.
     * @param callable the function to execute.
     * @param delay the time from now to delay execution.
     * @param unit the time unit of the delay parameter.
     * @return a ScheduledFuture that can be used to extract result or cancel.
     * @throws RejectedExecutionException if task cannot be scheduled
     * for execution.
     * @throws NullPointerException if callable is null
     */
    public <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit);

    /**
     * Creates and executes a periodic action that becomes enabled first
     * after the given initial delay, and subsequently with the given
     * period; that is executions will commence after
     * <tt>initialDelay</tt> then <tt>initialDelay+period</tt>, then
     * <tt>initialDelay + 2 * period</tt>, and so on.  
     * If any execution of the task
     * encounters an exception, subsequent executions are suppressed.
     * Otherwise, the task will only terminate via cancellation or
     * termination of the executor.
     * @param command the task to execute.
     * @param initialDelay the time to delay first execution.
     * @param period the period between successive executions.
     * @param unit the time unit of the initialDelay and period parameters
     * @return a Future representing pending completion of the task,
     * and whose <tt>get()</tt> method will throw an exception upon
     * cancellation.
     * @throws RejectedExecutionException if task cannot be scheduled
     * for execution.
     * @throws NullPointerException if command is null
     * @throws IllegalArgumentException if period less than or equal to zero.
     */
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay,  long period, TimeUnit unit);

    /**
     * Creates and executes a periodic action that becomes enabled first
     * after the given initial delay, and subsequently with the
     * given delay between the termination of one execution and the
     * commencement of the next. If any execution of the task
     * encounters an exception, subsequent executions are suppressed.
     * Otherwise, the task will only terminate via cancellation or
     * termination of the executor.
     * @param command the task to execute.
     * @param initialDelay the time to delay first execution.
     * @param delay the delay between the termination of one
     * execution and the commencement of the next.
     * @param unit the time unit of the initialDelay and delay parameters
     * @return a Future representing pending completion of the task,
     * and whose <tt>get()</tt> method will throw an exception upon
     * cancellation.
     * @throws RejectedExecutionException if task cannot be scheduled
     * for execution.
     * @throws NullPointerException if command is null
     * @throws IllegalArgumentException if delay less than or equal to zero.
     */
    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay,  long delay, TimeUnit unit);

}

