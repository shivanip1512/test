/*
 * Written by Doug Lea with assistance from members of JCP JSR-166
 * Expert Group and released to the public domain, as explained at
 * http://creativecommons.org/licenses/publicdomain
 */

package edu.emory.mathcs.backport.java.util.concurrent.atomic;

/**
 * An <tt>int</tt> value that may be updated atomically. See the
 * {@link edu.emory.mathcs.backport.java.util.concurrent.atomic} package specification for
 * description of the properties of atomic variables. An
 * <tt>AtomicInteger</tt> is used in applications such as atomically
 * incremented counters, and cannot be used as a replacement for an
 * {@link java.lang.Integer}. However, this class does extend
 * <tt>Number</tt> to allow uniform access by tools and utilities that
 * deal with numerically-based classes.
 *
 *
 * @since 1.5
 * @author Doug Lea
*/
public class AtomicInteger extends Number implements java.io.Serializable {
    private static final long serialVersionUID = 6214790243416807050L;

    private int value;

    /**
     * Create a new AtomicInteger with the given initial value.
     *
     * @param initialValue the initial value
     */
    public AtomicInteger(int initialValue) {
        value = initialValue;
    }

    /**
     * Create a new AtomicInteger with initial value <tt>0</tt>.
     */
    public AtomicInteger() {
    }

    /**
     * Get the current value.
     *
     * @return the current value
     */
    public final synchronized int get() {
        return value;
    }

    /**
     * Set to the given value.
     *
     * @param newValue the new value
     */
    public final synchronized void set(int newValue) {
        value = newValue;
    }

    /**
     * Set to the give value and return the old value.
     *
     * @param newValue the new value
     * @return the previous value
     */
    public final synchronized int getAndSet(int newValue) {
        int old = value;
        value = newValue;
        return old;
    }


    /**
     * Atomically set the value to the given updated value
     * if the current value <tt>==</tt> the expected value.
     * @param expect the expected value
     * @param update the new value
     * @return true if successful. False return indicates that
     * the actual value was not equal to the expected value.
     */
    public final synchronized boolean compareAndSet(int expect, int update) {
        boolean success = (expect == value);
        if (success)
            value = update;
        return success;
    }

    /**
     * Atomically set the value to the given updated value
     * if the current value <tt>==</tt> the expected value.
     * May fail spuriously.
     * @param expect the expected value
     * @param update the new value
     * @return true if successful.
     */
    public final synchronized boolean weakCompareAndSet(int expect, int update) {
        boolean success = (expect == value);
        if (success)
            value = update;
        return success;
    }


    /**
     * Atomically increment by one the current value.
     * @return the previous value
     */
    public final synchronized int getAndIncrement() {
        return value++;
    }


    /**
     * Atomically decrement by one the current value.
     * @return the previous value
     */
    public final synchronized int getAndDecrement() {
        return value--;
    }


    /**
     * Atomically add the given value to current value.
     * @param delta the value to add
     * @return the previous value
     */
    public final synchronized int getAndAdd(int delta) {
        int old = value;
        value += delta;
        return old;
    }

    /**
     * Atomically increment by one the current value.
     * @return the updated value
     */
    public final synchronized int incrementAndGet() {
        return ++value;
    }

    /**
     * Atomically decrement by one the current value.
     * @return the updated value
     */
    public final synchronized int decrementAndGet() {
        return --value;
    }


    /**
     * Atomically add the given value to current value.
     * @param delta the value to add
     * @return the updated value
     */
    public final synchronized int addAndGet(int delta) {
        return value += delta;
    }

    /**
     * Returns the String representation of the current value.
     * @return the String representation of the current value.
     */
    public String toString() {
        return Integer.toString(get());
    }


    public int intValue() {
        return get();
    }

    public long longValue() {
        return (long)get();
    }

    public float floatValue() {
        return (float)get();
    }

    public double doubleValue() {
        return (double)get();
    }

}
