/*
 * Written by Doug Lea with assistance from members of JCP JSR-166
 * Expert Group and released to the public domain. Use, modify, and
 * redistribute this code in any way without acknowledgement.
 */

package edu.emory.mathcs.backport.java.util.concurrent;

import edu.emory.mathcs.backport.java.util.concurrent.helpers.*;

/**
 * A synchronization aid that allows one or more threads to wait until
 * a set of operations being performed in other threads completes.
 *
 * <p>A <tt>CountDownLatch</tt> is initialized with a given
 * <em>count</em>.  The {@link #await await} methods block until the current
 * {@link #getCount count} reaches zero due to invocations of the
 * {@link #countDown} method, after which all waiting threads are
 * released and any subsequent invocations of {@link #await await} return
 * immediately. This is a one-shot phenomenon -- the count cannot be
 * reset.  If you need a version that resets the count, consider using
 * a {@link CyclicBarrier}.
 *
 * <p>A <tt>CountDownLatch</tt> is a versatile synchronization tool
 * and can be used for a number of purposes.  A
 * <tt>CountDownLatch</tt> initialized with a count of one serves as a
 * simple on/off latch, or gate: all threads invoking {@link #await await}
 * wait at the gate until it is opened by a thread invoking {@link
 * #countDown}.  A <tt>CountDownLatch</tt> initialized to <em>N</em>
 * can be used to make one thread wait until <em>N</em> threads have
 * completed some action, or some action has been completed N times.
 * <p>A useful property of a <tt>CountDownLatch</tt> is that it
 * doesn't require that threads calling <tt>countDown</tt> wait for
 * the count to reach zero before proceeding, it simply prevents any
 * thread from proceeding past an {@link #await await} until all
 * threads could pass.
 *
 * <p><b>Sample usage:</b> Here is a pair of classes in which a group
 * of worker threads use two countdown latches:
 * <ul>
 * <li>The first is a start signal that prevents any worker from proceeding
 * until the driver is ready for them to proceed;
 * <li>The second is a completion signal that allows the driver to wait
 * until all workers have completed.
 * </ul>
 *
 * <pre>
 * class Driver { // ...
 *   void main() throws InterruptedException {
 *     CountDownLatch startSignal = new CountDownLatch(1);
 *     CountDownLatch doneSignal = new CountDownLatch(N);
 *
 *     for (int i = 0; i < N; ++i) // create and start threads
 *       new Thread(new Worker(startSignal, doneSignal)).start();
 *
 *     doSomethingElse();            // don't let run yet
 *     startSignal.countDown();      // let all threads proceed
 *     doSomethingElse();
 *     doneSignal.await();           // wait for all to finish
 *   }
 * }
 *
 * class Worker implements Runnable {
 *   private final CountDownLatch startSignal;
 *   private final CountDownLatch doneSignal;
 *   Worker(CountDownLatch startSignal, CountDownLatch doneSignal) {
 *      this.startSignal = startSignal;
 *      this.doneSignal = doneSignal;
 *   }
 *   public void run() {
 *      try {
 *        startSignal.await();
 *        doWork();
 *        doneSignal.countDown();
 *      } catch (InterruptedException ex) {} // return;
 *   }
 *
 *   void doWork() { ... }
 * }
 *
 * </pre>
 *
 * <p>Another typical usage would be to divide a problem into N parts,
 * describe each part with a Runnable that executes that portion and
 * counts down on the latch, and queue all the Runnables to an
 * Executor.  When all sub-parts are complete, the coordinating thread
 * will be able to pass through await. (When threads must repeatedly
 * count down in this way, instead use a {@link CyclicBarrier}.)
 *
 * <pre>
 * class Driver2 { // ...
 *   void main() throws InterruptedException {
 *     CountDownLatch doneSignal = new CountDownLatch(N);
 *     Executor e = ...
 *
 *     for (int i = 0; i < N; ++i) // create and start threads
 *       e.execute(new WorkerRunnable(doneSignal, i));
 *
 *     doneSignal.await();           // wait for all to finish
 *   }
 * }
 *
 * class WorkerRunnable implements Runnable {
 *   private final CountDownLatch doneSignal;
 *   private final int i;
 *   WorkerRunnable(CountDownLatch doneSignal, int i) {
 *      this.doneSignal = doneSignal;
 *      this.i = i;
 *   }
 *   public void run() {
 *      try {
 *        doWork(i);
 *        doneSignal.countDown();
 *      } catch (InterruptedException ex) {} // return;
 *   }
 *
 *   void doWork() { ... }
 * }
 *
 * </pre>
 *
 * @since 1.5
 * @author Doug Lea
 */
public class CountDownLatch {

    private int count_;

    /**
     * Constructs a <tt>CountDownLatch</tt> initialized with the given
     * count.
     *
     * @param count the number of times {@link #countDown} must be invoked
     * before threads can pass through {@link #await}.
     *
     * @throws IllegalArgumentException if <tt>count</tt> is less than zero.
     */
    public CountDownLatch(int count) {
        if (count < 0) throw new IllegalArgumentException("count < 0");
        this.count_ = count;
    }

    /**
     * Causes the current thread to wait until the latch has counted down to
     * zero, unless the thread is {@link Thread#interrupt interrupted}.
     *
     * <p>If the current {@link #getCount count} is zero then this method
     * returns immediately.
     * <p>If the current {@link #getCount count} is greater than zero then
     * the current thread becomes disabled for thread scheduling
     * purposes and lies dormant until one of two things happen:
     * <ul>
     * <li>The count reaches zero due to invocations of the
     * {@link #countDown} method; or
     * <li>Some other thread {@link Thread#interrupt interrupts} the current
     * thread.
     * </ul>
     * <p>If the current thread:
     * <ul>
     * <li>has its interrupted status set on entry to this method; or
     * <li>is {@link Thread#interrupt interrupted} while waiting,
     * </ul>
     * then {@link InterruptedException} is thrown and the current thread's
     * interrupted status is cleared.
     *
     * @throws InterruptedException if the current thread is interrupted
     * while waiting.
     */
    public void await() throws InterruptedException {
        if (Thread.interrupted()) throw new InterruptedException();
        synchronized(this) {
            while (count_ > 0)
                wait();
        }
    }

    /**
     * Causes the current thread to wait until the latch has counted down to
     * zero, unless the thread is {@link Thread#interrupt interrupted},
     * or the specified waiting time elapses.
     *
     * <p>If the current {@link #getCount count} is zero then this method
     * returns immediately with the value <tt>true</tt>.
     *
     * <p>If the current {@link #getCount count} is greater than zero then
     * the current thread becomes disabled for thread scheduling
     * purposes and lies dormant until one of three things happen:
     * <ul>
     * <li>The count reaches zero due to invocations of the
     * {@link #countDown} method; or
     * <li>Some other thread {@link Thread#interrupt interrupts} the current
     * thread; or
     * <li>The specified waiting time elapses.
     * </ul>
     * <p>If the count reaches zero then the method returns with the
     * value <tt>true</tt>.
     * <p>If the current thread:
     * <ul>
     * <li>has its interrupted status set on entry to this method; or
     * <li>is {@link Thread#interrupt interrupted} while waiting,
     * </ul>
     * then {@link InterruptedException} is thrown and the current thread's
     * interrupted status is cleared.
     *
     * <p>If the specified waiting time elapses then the value <tt>false</tt>
     * is returned.
     * If the time is
     * less than or equal to zero, the method will not wait at all.
     *
     * @param timeout the maximum time to wait
     * @param unit the time unit of the <tt>timeout</tt> argument.
     * @return <tt>true</tt> if the count reached zero  and <tt>false</tt>
     * if the waiting time elapsed before the count reached zero.
     *
     * @throws InterruptedException if the current thread is interrupted
     * while waiting.
     */
    public boolean await(long timeout, TimeUnit unit) throws InterruptedException {
        if (Thread.interrupted()) throw new InterruptedException();
        long nanos = unit.toNanos(timeout);
        synchronized (this) {
            if (count_ <= 0)
                return true;
            else if (nanos <= 0)
                return false;
            else {
                long deadline = Utils.nanoTime() + nanos;
                for (; ; ) {
                    TimeUnit.NANOSECONDS.timedWait(this, nanos);
                    if (count_ <= 0)
                        return true;
                    else {
                        nanos = deadline - Utils.nanoTime();
                        if (nanos <= 0)
                            return false;
                    }
                }
            }
        }
    }

    /**
     * Decrements the count of the latch, releasing all waiting threads if
     * the count reaches zero.
     * <p>If the current {@link #getCount count} is greater than zero then
     * it is decremented. If the new count is zero then all waiting threads
     * are re-enabled for thread scheduling purposes.
     * <p>If the current {@link #getCount count} equals zero then nothing
     * happens.
     */
    public synchronized void countDown() {
        if (count_ == 0) return;
        if (--count_ == 0)
            notifyAll();
    }

    /**
     * Returns the current count.
     * <p>This method is typically used for debugging and testing purposes.
     * @return the current count.
     */
    public long getCount() {
        return count_;
    }

    /**
     * Returns a string identifying this latch, as well as its state.
     * The state, in brackets, includes the String
     * &quot;Count =&quot; followed by the current count.
     * @return a string identifying this latch, as well as its
     * state
     */
    public String toString() {
        return super.toString() + "[Count = " + getCount() + "]";
    }

}
