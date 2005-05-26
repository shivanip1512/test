/*
 * Written by Doug Lea with assistance from members of JCP JSR-166
 * Expert Group and released to the public domain, as explained at
 * http://creativecommons.org/licenses/publicdomain
 */

package edu.emory.mathcs.backport.java.util.concurrent;
import java.util.*;
import edu.emory.mathcs.backport.java.util.*;
import edu.emory.mathcs.backport.java.util.concurrent.helpers.*;
import edu.emory.mathcs.backport.java.util.concurrent.locks.*;

/**
 * An optionally-bounded {@linkplain BlockingDeque blocking deque} based on
 * linked nodes.
 *
 * <p> The optional capacity bound constructor argument serves as a
 * way to prevent excessive expansion. The capacity, if unspecified,
 * is equal to {@link Integer#MAX_VALUE}.  Linked nodes are
 * dynamically created upon each insertion unless this would bring the
 * deque above capacity.
 *
 * <p>Most operations run in constant time (ignoring time spent
 * blocking).  Exceptions include {@link #remove(Object) remove},
 * {@link #removeFirstOccurrence removeFirstOccurrence}, {@link
 * #removeLastOccurrence removeLastOccurrence}, {@link #contains
 * contains }, {@link #iterator iterator.remove()}, and the bulk
 * operations, all of which run in linear time.
 *
 * <p>This class and its iterator implement all of the
 * <em>optional</em> methods of the {@link Collection} and {@link
 * Iterator} interfaces.  This class is a member of the <a
 * href="{@docRoot}/../guide/collections/index.html"> Java Collections
 * Framework</a>.
 *
 * @since 1.6
 * @author  Doug Lea
 */
public class LinkedBlockingDeque
    extends AbstractQueue
    implements BlockingDeque, java.io.Serializable {

    /*
     * Implemented as a simple doubly-linked list protected by a
     * single lock and using conditions to manage blocking.
     */

    private static final long serialVersionUID = -387911632671998426L;

    /** Doubly-linked list node class */
    static final class Node {
        Object item;
        Node prev;
        Node next;
        Node(Object x, Node p, Node n) {
            item = x;
            prev = p;
            next = n;
        }
    }

    /** Pointer to first node */
    private transient Node first;
    /** Pointer to last node */
    private transient Node last;
    /** Number of items in the deque */
    private transient int count;
    /** Maximum number of items in the deque */
    private final int capacity;
    /** Main lock guarding all access */
    private final ReentrantLock lock = new ReentrantLock();
    /** Condition for waiting takes */
    private final Condition notEmpty = lock.newCondition();
    /** Condition for waiting puts */
    private final Condition notFull = lock.newCondition();

    /**
     * Creates a <tt>LinkedBlockingDeque</tt> with a capacity of
     * {@link Integer#MAX_VALUE}.
     */
    public LinkedBlockingDeque() {
        this(Integer.MAX_VALUE);
    }

    /**
     * Creates a <tt>LinkedBlockingDeque</tt> with the given (fixed)
     * capacity.
     * @param capacity the capacity of this deque
     * @throws IllegalArgumentException if <tt>capacity</tt> is less than 1
     */
    public LinkedBlockingDeque(int capacity) {
        if (capacity <= 0) throw new IllegalArgumentException();
        this.capacity = capacity;
    }

    /**
     * Creates a <tt>LinkedBlockingDeque</tt> with a capacity of
     * {@link Integer#MAX_VALUE}, initially containing the elements of the
     * given collection,
     * added in traversal order of the collection's iterator.
     * @param c the collection of elements to initially contain
     * @throws NullPointerException if <tt>c</tt> or any element within it
     * is <tt>null</tt>
     */
    public LinkedBlockingDeque(Collection c) {
        this(Integer.MAX_VALUE);
        for (Iterator itr = c.iterator(); itr.hasNext();) {
            Object e = itr.next();
            add(e);
        }
    }


    // Basic linking and unlinking operations, called only while holding lock

    /**
     * Link e as first element, or return false if full
     */
    private boolean linkFirst(Object e) {
        if (count >= capacity)
            return false;
        ++count;
        Node f = first;
        Node x = new Node(e, null, f);
        first = x;
        if (last == null)
            last = x;
        else
            f.prev = x;
        notEmpty.signal();
        return true;
    }

    /**
     * Link e as last element, or return false if full
     */
    private boolean linkLast(Object e) {
        if (count >= capacity)
            return false;
        ++count;
        Node l = last;
        Node x = new Node(e, l, null);
        last = x;
        if (first == null)
            first = x;
        else
            l.next = x;
        notEmpty.signal();
        return true;
    }

    /**
     * Remove and return first element, or null if empty
     */
    private Object unlinkFirst() {
        Node f = first;
        if (f == null)
            return null;
        Node n = f.next;
        first = n;
        if (n == null)
            last = null;
        else
            n.prev = null;
        --count;
        notFull.signal();
        return f.item;
    }

    /**
     * Remove and return last element, or null if empty
     */
    private Object unlinkLast() {
        Node l = last;
        if (l == null)
            return null;
        Node p = l.prev;
        last = p;
        if (p == null)
            first = null;
        else
            p.next = null;
        --count;
        notFull.signal();
        return l.item;
    }

    /**
     * Unlink e
     */
    private void unlink(Node x) {
        Node p = x.prev;
        Node n = x.next;
        if (p == null) {
            if (n == null)
                first = last = null;
            else {
                n.prev = null;
                first = n;
            }
        } else if (n == null) {
            p.next = null;
            last = p;
        } else {
            p.next = n;
            n.prev = p;
        }
        --count;
        notFull.signalAll();
    }

    // Deque methods

    public boolean offerFirst(Object o) {
        if (o == null) throw new NullPointerException();
        lock.lock();
        try {
            return linkFirst(o);
        } finally {
            lock.unlock();
        }
    }

    public boolean offerLast(Object o) {
        if (o == null) throw new NullPointerException();
        lock.lock();
        try {
            return linkLast(o);
        } finally {
            lock.unlock();
        }
    }

    public void addFirst(Object e) {
        if (!offerFirst(e))
            throw new IllegalStateException("Deque full");
    }

    public void addLast(Object e) {
        if (!offerLast(e))
            throw new IllegalStateException("Deque full");
    }

    public Object pollFirst() {
        lock.lock();
        try {
            return unlinkFirst();
        } finally {
            lock.unlock();
        }
    }

    public Object pollLast() {
        lock.lock();
        try {
            return unlinkLast();
        } finally {
            lock.unlock();
        }
    }

    public Object removeFirst() {
        Object x = pollFirst();
        if (x == null) throw new NoSuchElementException();
        return x;
    }

    public Object removeLast() {
        Object x = pollLast();
        if (x == null) throw new NoSuchElementException();
        return x;
    }

    public Object peekFirst() {
        lock.lock();
        try {
            return (first == null) ? null : first.item;
        } finally {
            lock.unlock();
        }
    }

    public Object peekLast() {
        lock.lock();
        try {
            return (last == null) ? null : last.item;
        } finally {
            lock.unlock();
        }
    }

    public Object getFirst() {
        Object x = peekFirst();
        if (x == null) throw new NoSuchElementException();
        return x;
    }

    public Object getLast() {
        Object x = peekLast();
        if (x == null) throw new NoSuchElementException();
        return x;
    }

    // BlockingDeque methods

    public void putFirst(Object o) throws InterruptedException {
        if (o == null) throw new NullPointerException();
        lock.lock();
        try {
            while (!linkFirst(o))
                notFull.await();
        } finally {
            lock.unlock();
        }
    }

    public void putLast(Object o) throws InterruptedException {
        if (o == null) throw new NullPointerException();
        lock.lock();
        try {
            while (!linkLast(o))
                notFull.await();
        } finally {
            lock.unlock();
        }
    }

    public Object takeFirst() throws InterruptedException {
        lock.lock();
        try {
            Object x;
            while ( (x = unlinkFirst()) == null)
                notEmpty.await();
            return x;
        } finally {
            lock.unlock();
        }
    }

    public Object takeLast() throws InterruptedException {
        lock.lock();
        try {
            Object x;
            while ( (x = unlinkLast()) == null)
                notEmpty.await();
            return x;
        } finally {
            lock.unlock();
        }
    }

    public boolean offerFirst(Object o, long timeout, TimeUnit unit)
        throws InterruptedException {
        if (o == null) throw new NullPointerException();
        lock.lockInterruptibly();
        try {
            long nanos = unit.toNanos(timeout);
            long deadline = Utils.nanoTime() + nanos;
            for (;;) {
                if (linkFirst(o))
                    return true;
                if (nanos <= 0)
                    return false;
                notFull.await(nanos, TimeUnit.NANOSECONDS);
                nanos = deadline - Utils.nanoTime();
            }
        } finally {
            lock.unlock();
        }
    }

    public boolean offerLast(Object o, long timeout, TimeUnit unit)
        throws InterruptedException {
        if (o == null) throw new NullPointerException();
        lock.lockInterruptibly();
        try {
            long nanos = unit.toNanos(timeout);
            long deadline = Utils.nanoTime() + nanos;
            for (;;) {
                if (linkLast(o))
                    return true;
                if (nanos <= 0)
                    return false;
                notFull.await(nanos, TimeUnit.NANOSECONDS);
                nanos = deadline - Utils.nanoTime();
            }
        } finally {
            lock.unlock();
        }
    }

    public Object pollFirst(long timeout, TimeUnit unit)
        throws InterruptedException {
        lock.lockInterruptibly();
        try {
            long nanos = unit.toNanos(timeout);
            long deadline = Utils.nanoTime() + nanos;
            for (;;) {
                Object x = unlinkFirst();
                if (x != null)
                    return x;
                if (nanos <= 0)
                    return null;
                notEmpty.await(nanos, TimeUnit.NANOSECONDS);
                nanos = deadline - Utils.nanoTime();
            }
        } finally {
            lock.unlock();
        }
    }

    public Object pollLast(long timeout, TimeUnit unit)
        throws InterruptedException {
        lock.lockInterruptibly();
        try {
            long nanos = unit.toNanos(timeout);
            long deadline = Utils.nanoTime() + nanos;
            for (;;) {
                Object x = unlinkLast();
                if (x != null)
                    return x;
                if (nanos <= 0)
                    return null;
                notEmpty.await(nanos, TimeUnit.NANOSECONDS);
                nanos = deadline - Utils.nanoTime();
            }
        } finally {
            lock.unlock();
        }
    }

    // Queue and stack methods

    public boolean offer(Object e)       { return offerLast(e); }
    public boolean add(Object e)         { addLast(e); return true; }
    public void push(Object e)           { addFirst(e); }
    public Object poll()                 { return pollFirst(); }
    public Object remove()               { return removeFirst(); }
    public Object pop()                  { return removeFirst(); }
    public Object peek()                 { return peekFirst(); }
    public Object element()              { return getFirst(); }
    public boolean remove(Object o) { return removeFirstOccurrence(o); }

    // BlockingQueue methods

    public void put(Object o) throws InterruptedException  { putLast(o);  }
    public Object take() throws InterruptedException       { return takeFirst(); }
    public boolean offer(Object o, long timeout, TimeUnit unit)
        throws InterruptedException    { return offerLast(o, timeout, unit); }
    public Object poll(long timeout, TimeUnit unit)
        throws InterruptedException    { return pollFirst(timeout, unit); }

    /**
     * Returns the number of elements in this deque.
     *
     * @return  the number of elements in this deque.
     */
    public int size() {
        lock.lock();
        try {
            return count;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Returns the number of elements that this deque can ideally (in
     * the absence of memory or resource constraints) accept without
     * blocking. This is always equal to the initial capacity of this deque
     * less the current <tt>size</tt> of this deque.
     * <p>Note that you <em>cannot</em> always tell if
     * an attempt to <tt>add</tt> an element will succeed by
     * inspecting <tt>remainingCapacity</tt> because it may be the
     * case that a waiting consumer is ready to <tt>take</tt> an
     * element out of an otherwise full deque.
     */
    public int remainingCapacity() {
        lock.lock();
        try {
            return capacity - count;
        } finally {
            lock.unlock();
        }
    }

    public boolean contains(Object o) {
        if (o == null) return false;
        lock.lock();
        try {
            for (Node p = first; p != null; p = p.next)
                if (o.equals(p.item))
                    return true;
            return false;
        } finally {
            lock.unlock();
        }
    }

    public boolean removeFirstOccurrence(Object e) {
        if (e == null) throw new NullPointerException();
        lock.lock();
        try {
            for (Node p = first; p != null; p = p.next) {
                if (e.equals(p.item)) {
                    unlink(p);
                    return true;
                }
            }
            return false;
        } finally {
            lock.unlock();
        }
    }

    public boolean removeLastOccurrence(Object e) {
        if (e == null) throw new NullPointerException();
        lock.lock();
        try {
            for (Node p = last; p != null; p = p.prev) {
                if (e.equals(p.item)) {
                    unlink(p);
                    return true;
                }
            }
            return false;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Variant of removeFirstOccurrence needed by iterator.remove.
     * Searches for the node, not its contents.
     */
   boolean removeNode(Node e) {
        lock.lock();
        try {
            for (Node p = first; p != null; p = p.next) {
                if (p == e) {
                    unlink(p);
                    return true;
                }
            }
            return false;
        } finally {
            lock.unlock();
        }
    }

    public Object[] toArray() {
        lock.lock();
        try {
            Object[] a = new Object[count];
            int k = 0;
            for (Node p = first; p != null; p = p.next)
                a[k++] = p.item;
            return a;
        } finally {
            lock.unlock();
        }
    }

    public Object[] toArray(Object[] a) {
        lock.lock();
        try {
            if (a.length < count)
                a = (Object[])java.lang.reflect.Array.newInstance(
                    a.getClass().getComponentType(),
                    count
                    );

            int k = 0;
            for (Node p = first; p != null; p = p.next)
                a[k++] = (Object)p.item;
            if (a.length > k)
                a[k] = null;
            return a;
        } finally {
            lock.unlock();
        }
    }

    public String toString() {
        lock.lock();
        try {
            return super.toString();
        } finally {
            lock.unlock();
        }
    }

    /**
     * Atomically removes all of the elements from this deque.
     * The deque will be empty after this call returns.
     */
    public void clear() {
        lock.lock();
        try {
            first = last = null;
            count = 0;
            notFull.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public int drainTo(Collection c) {
        if (c == null)
            throw new NullPointerException();
        if (c == this)
            throw new IllegalArgumentException();
        lock.lock();
        try {
            for (Node p = first; p != null; p = p.next)
                c.add(p.item);
            int n = count;
            count = 0;
            first = last = null;
            notFull.signalAll();
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
        lock.lock();
        try {
            int n = 0;
            while (n < maxElements && first != null) {
                c.add(first.item);
                first.prev = null;
                first = first.next;
                --count;
                ++n;
            }
            if (first == null)
                last = null;
            notFull.signalAll();
            return n;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Returns an iterator over the elements in this deque in proper sequence.
     * The returned <tt>Iterator</tt> is a "weakly consistent" iterator that
     * will never throw {@link java.util.ConcurrentModificationException},
     * and guarantees to traverse elements as they existed upon
     * construction of the iterator, and may (but is not guaranteed to)
     * reflect any modifications subsequent to construction.
     *
     * @return an iterator over the elements in this deque in proper sequence.
     */
    public Iterator iterator() {
        return new Itr();
    }

    /**
     * Iterator for LinkedBlockingDeque
     */
    private class Itr implements Iterator {
        private Node next;

        /**
         * nextItem holds on to item fields because once we claim that
         * an element exists in hasNext(), we must return item read
         * under lock (in advance()) even if it was in the process of
         * being removed when hasNext() was called.
         **/
        private Object nextItem;

        /**
         * Node returned by most recent call to next. Needed by remove.
         * Reset to null if this element is deleted by a call to remove.
         */
        private Node last;

        Itr() {
            advance();
        }

        /**
         * Advance next, or if not yet initialized, set to first node.
         */
        private void advance() {
            final ReentrantLock lock = LinkedBlockingDeque.this.lock;
            lock.lock();
            try {
                next = (next == null)? first : next.next;
                nextItem = (next == null)? null : next.item;
            } finally {
                lock.unlock();
            }
        }

        public boolean hasNext() {
            return next != null;
        }

        public Object next() {
            if (next == null)
                throw new NoSuchElementException();
            last = next;
            Object x = nextItem;
            advance();
            return x;
        }

        public void remove() {
            Node n = last;
            if (n == null)
                throw new IllegalStateException();
            last = null;
            // Note: removeNode rescans looking for this node to make
            // sure it was not already removed. Otherwwise, trying to
            // re-remove could corrupt list.
            removeNode(n);
        }
    }

    /**
     * Save the state to a stream (that is, serialize it).
     *
     * @serialData The capacity (int), followed by elements (each an
     * <tt>Object</tt>) in the proper order, followed by a null
     * @param s the stream
     */
    private void writeObject(java.io.ObjectOutputStream s)
        throws java.io.IOException {
        lock.lock();
        try {
            // Write out capacity and any hidden stuff
            s.defaultWriteObject();
            // Write out all elements in the proper order.
            for (Node p = first; p != null; p = p.next)
                s.writeObject(p.item);
            // Use trailing null as sentinel
            s.writeObject(null);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Reconstitute this deque instance from a stream (that is,
     * deserialize it).
     * @param s the stream
     */
    private void readObject(java.io.ObjectInputStream s)
        throws java.io.IOException, ClassNotFoundException {
        s.defaultReadObject();
        count = 0;
        first = null;
        last = null;
        // Read in all elements and place in queue
        for (;;) {
            Object item = (Object)s.readObject();
            if (item == null)
                break;
            add(item);
        }
    }

}
