/*
 * Written by Doug Lea with assistance from members of JCP JSR-166
 * Expert Group and released to the public domain. Use, modify, and
 * redistribute this code in any way without acknowledgement.
 */

package edu.emory.mathcs.backport.java.util.concurrent;

import java.util.*;

import edu.emory.mathcs.backport.java.util.AbstractQueue;
import edu.emory.mathcs.backport.java.util.PriorityQueue;
import edu.emory.mathcs.backport.java.util.concurrent.locks.*;
import edu.emory.mathcs.backport.java.util.concurrent.helpers.*;

/**
 * An unbounded {@linkplain BlockingQueue blocking queue} of
 * <tt>Delayed</tt> elements, in which an element can only be taken
 * when its delay has expired.  The <em>head</em> of the queue is that
 * <tt>Delayed</tt> element whose delay expired furthest in the
 * past. If no delay has expired there is no head and <tt>poll</tt>
 * will return <tt>null</tt>. Expiration occurs when an element's
 * <tt>getDelay(TimeUnit.NANOSECONDS)</tt> method returns a value less
 * than or equal to zero.  This queue does not permit <tt>null</tt>
 * elements.
 *
 * <p>This class and its iterator implement all of the
 * <em>optional</em> methods of the {@link Collection} and {@link
 * Iterator} interfaces.
 *
 * <p>This class is a member of the
 * <a href="{@docRoot}/../guide/collections/index.html">
 * Java Collections Framework</a>.
 *
 * @since 1.5
 * @author Doug Lea
 */

public class DelayQueue extends AbstractQueue
    implements BlockingQueue {

    private transient final ReentrantLock lock = new ReentrantLock();
    private transient final Condition available = lock.newCondition();
    private final PriorityQueue q = new PriorityQueue();

    /**
     * Creates a new <tt>DelayQueue</tt> that is initially empty.
     */
    public DelayQueue() {}

    /**
     * Creates a <tt>DelayQueue</tt> initially containing the elements of the
     * given collection of {@link Delayed} instances.
     *
     * @param c the collection
     * @throws NullPointerException if <tt>c</tt> or any element within it
     * is <tt>null</tt>
     *
     */
    public DelayQueue(Collection c) {
        this.addAll(c);
    }

    /**
     * Inserts the specified element into this delay queue.
     *
     * @param o the element to add
     * @return <tt>true</tt>
     * @throws NullPointerException if the specified element is <tt>null</tt>.
     */
    public boolean offer(Object o) {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            Object first = q.peek();
            q.offer(o);
            if (first == null || ((Delayed)o).compareTo(first) < 0)
                available.signalAll();
            return true;
        } finally {
            lock.unlock();
        }
    }


    /**
     * Adds the specified element to this delay queue. As the queue is
     * unbounded this method will never block.
     * @param o the element to add
     * @throws NullPointerException if the specified element is <tt>null</tt>.
     */
    public void put(Object o) {
        offer(o);
    }

    /**
     * Inserts the specified element into this delay queue. As the queue is
     * unbounded this method will never block.
     * @param o the element to add
     * @param timeout This parameter is ignored as the method never blocks
     * @param unit This parameter is ignored as the method never blocks
     * @return <tt>true</tt>
     * @throws NullPointerException if the specified element is <tt>null</tt>.
     */
    public boolean offer(Object o, long timeout, TimeUnit unit) {
        return offer(o);
    }

    /**
     * Adds the specified element to this queue.
     * @param o the element to add
     * @return <tt>true</tt> (as per the general contract of
     * <tt>Collection.add</tt>).
     *
     * @throws NullPointerException if the specified element is <tt>null</tt>.
     */
    public boolean add(Object o) {
        return offer(o);
    }

    /**
     * Retrieves and removes the head of this queue, waiting
     * if no elements with an unexpired delay are present on this queue.
     * @return the head of this queue
     * @throws InterruptedException if interrupted while waiting.
     */
    public Object take() throws InterruptedException {
        final ReentrantLock lock = this.lock;
        lock.lockInterruptibly();
        try {
            for (;;) {
                Object first = q.peek();
                if (first == null) {
                    available.await();
                } else {
                    long delay = ((Delayed)first).getDelay(TimeUnit.NANOSECONDS);
                    if (delay > 0) {
                        available.await(delay, TimeUnit.NANOSECONDS);
                    } else {
                        Object x = q.poll();
                        assert x != null;
                        if (q.size() != 0)
                            available.signalAll(); // wake up other takers
                        return x;

                    }
                }
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * Retrieves and removes the head of this queue, waiting
     * if necessary up to the specified wait time if no elements with
     * an unexpired delay are
     * present on this queue.
     * @param timeout how long to wait before giving up, in units of
     * <tt>unit</tt>
     * @param unit a <tt>TimeUnit</tt> determining how to interpret the
     * <tt>timeout</tt> parameter
     * @return the head of this queue, or <tt>null</tt> if the
     * specified waiting time elapses before an element with
     * an unexpired delay is present.
     * @throws InterruptedException if interrupted while waiting.
     */
    public Object poll(long timeout, TimeUnit unit) throws InterruptedException {
        final ReentrantLock lock = this.lock;
        lock.lockInterruptibly();
        long nanos = unit.toNanos(timeout);
        try {
            long deadline = Utils.nanoTime() + nanos;
            for (;;) {
                Object first = q.peek();
                if (first == null) {
                    if (nanos <= 0)
                        return null;
                    else {
                        available.await(nanos, TimeUnit.NANOSECONDS);
                        nanos = deadline - Utils.nanoTime();
                    }
                } else {
                    long delay =  ((Delayed)first).getDelay(TimeUnit.NANOSECONDS);
                    if (delay > 0) {
                        if (delay > nanos)
                            delay = nanos;
                        available.await(delay, TimeUnit.NANOSECONDS);
                        nanos = deadline - Utils.nanoTime();
                    } else {
                        Object x = q.poll();
                        assert x != null;
                        if (q.size() != 0)
                            available.signalAll();
                        return x;
                    }
                }
            }
        } finally {
            lock.unlock();
        }
    }


    /**
     * Retrieves and removes the head of this queue, or <tt>null</tt>
     * if this queue has no elements with an unexpired delay.
     *
     * @return the head of this queue, or <tt>null</tt> if this
     *         queue has no elements with an unexpired delay.
     */
    public Object poll() {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            Object first = q.peek();
            if (first == null || ((Delayed)first).getDelay(TimeUnit.NANOSECONDS) > 0)
                return null;
            else {
                Object x = q.poll();
                assert x != null;
                if (q.size() != 0)
                    available.signalAll();
                return x;
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * Retrieves, but does not remove, the head of this queue,
     * returning <tt>null</tt> if this queue has no elements with an
     * unexpired delay.
     *
     * @return the head of this queue, or <tt>null</tt> if this queue
     * has no elements with an unexpired delay.
     */
    public Object peek() {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            return q.peek();
        } finally {
            lock.unlock();
        }
    }

    public int size() {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            return q.size();
        } finally {
            lock.unlock();
        }
    }

    public int drainTo(Collection c) {
        if (c == null)
            throw new NullPointerException();
        if (c == this)
            throw new IllegalArgumentException();
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            int n = 0;
            for (;;) {
                Object first = q.peek();
                if (first == null || ((Delayed)first).getDelay(TimeUnit.NANOSECONDS) > 0)
                    break;
                c.add(q.poll());
                ++n;
            }
            if (n > 0)
                available.signalAll();
            return n;
        } finally {
            lock.unlock();
        }
    }

    public int drainTo(Collection c, int maxElements) {
        if (c == null)
            throw new NullPointerException();
        if (c == this)
            throw new IllegalArgumentException();
        if (maxElements <= 0)
            return 0;
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            int n = 0;
            while (n < maxElements) {
                Object first = q.peek();
                if (first == null || ((Delayed)first).getDelay(TimeUnit.NANOSECONDS) > 0)
                    break;
                c.add(q.poll());
                ++n;
            }
            if (n > 0)
                available.signalAll();
            return n;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Atomically removes all of the elements from this delay queue.
     * The queue will be empty after this call returns.
     */
    public void clear() {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            q.clear();
        } finally {
            lock.unlock();
        }
    }

    /**
     * Always returns <tt>Integer.MAX_VALUE</tt> because
     * a <tt>DelayQueue</tt> is not capacity constrained.
     * @return <tt>Integer.MAX_VALUE</tt>
     */
    public int remainingCapacity() {
        return Integer.MAX_VALUE;
    }

    public Object[] toArray() {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            return q.toArray();
        } finally {
            lock.unlock();
        }
    }

    public Object[] toArray(Object[] array) {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            return q.toArray(array);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Removes a single instance of the specified element from this
     * queue, if it is present.
     */
    public boolean remove(Object o) {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            return q.remove(o);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Returns an iterator over the elements in this queue. The iterator
     * does not return the elements in any particular order. The
     * returned iterator is a thread-safe "fast-fail" iterator that will
     * throw {@link java.util.ConcurrentModificationException}
     * upon detected interference.
     *
     * @return an iterator over the elements in this queue.
     */
    public Iterator iterator() {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            return new Itr(q.iterator());
        } finally {
            lock.unlock();
        }
    }

    private class Itr implements Iterator {
        private final Iterator iter;
        Itr(Iterator i) {
            iter = i;
        }

        public boolean hasNext() {
            return iter.hasNext();
        }

        public Object next() {
            final ReentrantLock lock = DelayQueue.this.lock;
            lock.lock();
            try {
                return iter.next();
            } finally {
                lock.unlock();
            }
        }

        public void remove() {
            final ReentrantLock lock = DelayQueue.this.lock;
            lock.lock();
            try {
                iter.remove();
            } finally {
                lock.unlock();
            }
        }
    }

}
