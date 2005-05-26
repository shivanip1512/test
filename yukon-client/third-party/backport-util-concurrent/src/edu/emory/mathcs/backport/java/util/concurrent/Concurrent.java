/*
 * Written by Doug Lea with assistance from members of JCP JSR-166
 * Expert Group and released to the public domain, as explained at
 * http://creativecommons.org/licenses/publicdomain
 */

package edu.emory.mathcs.backport.java.util.concurrent;
import java.util.*;
import edu.emory.mathcs.backport.java.util.*;

/**
 * Static methods that operate on or return instances of collection
 * and synchronizer classes and interfaces defined in this package.
 *
 * @since 1.6
 * @author Doug Lea
 */


public class Concurrent {
    /**
     * Returns a view of a {@link BlockingDeque} as a stack-based
     * Last-in-first-out (Lifo) {@link BlockingQueue}. Method
     * <tt>put</tt> is mapped to <tt>putFirst</tt>, <tt>take</tt> is
     * mapped to <tt>takeFirst</tt> and so on. This view can be useful
     * when you would like to use a method requiring a
     * <tt>BlockingQueue</tt> but you need Lifo ordering.
     * @param deque the BlockingDeque
     * @return the queue
     */
    public static BlockingQueue asLifoBlockingQueue(BlockingDeque deque){
        return new AsLIFOBlockingQueue(deque);
    }

    static class AsLIFOBlockingQueue extends AbstractQueue
        implements BlockingQueue, java.io.Serializable {
        private final BlockingDeque q;
        AsLIFOBlockingQueue(BlockingDeque q) { this.q = q; }
        public boolean offer(Object o)       { return q.offerFirst(o); }
        public Object poll()                 { return q.pollFirst(); }
        public Object remove()               { return q.removeFirst(); }
        public Object peek()                 { return q.peekFirst(); }
        public Object element()              { return q.getFirst(); }
        public int size()                    { return q.size(); }
        public boolean isEmpty()             { return q.isEmpty(); }
        public boolean contains(Object o)    { return q.contains(o); }
        public Iterator iterator()           { return q.iterator(); }
        public Object[] toArray()            { return q.toArray(); }
        public Object[] toArray(Object[] a)  { return q.toArray(a); }
        public boolean add(Object o)         { return q.offerFirst(o); }
        public boolean remove(Object o)      { return q.remove(o); }
        public void clear()                  { q.clear(); }
        public int remainingCapacity()       { return q.remainingCapacity(); }
        public int drainTo(Collection c)     { return q.drainTo(c); }
        public int drainTo(Collection c, int m) {
            return q.drainTo(c, m);
        }
        public void put(Object o) throws InterruptedException { q.putFirst(o); }
        public Object take() throws InterruptedException { return q.takeFirst(); }
        public boolean offer(Object o, long timeout, TimeUnit unit)
            throws InterruptedException {
            return q.offerFirst(o, timeout, unit);
        }
        public Object poll(long timeout, TimeUnit unit)
            throws InterruptedException {
            return q.pollFirst(timeout, unit);
        }
    }

}
