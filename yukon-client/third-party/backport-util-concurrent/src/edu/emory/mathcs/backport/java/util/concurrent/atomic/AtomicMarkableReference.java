/*
 * Written by Doug Lea with assistance from members of JCP JSR-166
 * Expert Group and released to the public domain, as explained at
 * http://creativecommons.org/licenses/publicdomain
 */

package edu.emory.mathcs.backport.java.util.concurrent.atomic;

/**
 * An <tt>AtomicMarkableReference</tt> maintains an object reference
 * along with a mark bit, that can be updated atomically.
 * <p>
 * <p> Implementation note. This implementation maintains markable
 * references by creating internal objects representing "boxed"
 * [reference, boolean] pairs.
 *
 * @since 1.5
 * @author Doug Lea
 */
public class AtomicMarkableReference  {

    private static class ReferenceBooleanPair {
        private final Object reference;
        private final boolean bit;
        ReferenceBooleanPair(Object r, boolean i) {
            reference = r; bit = i;
        }
    }

    private final AtomicReference atomicRef;

    /**
     * Creates a new <tt>AtomicMarkableReference</tt> with the given
     * initial values.
     *
     * @param initialRef the initial reference
     * @param initialMark the initial mark
     */
    public AtomicMarkableReference(Object initialRef, boolean initialMark) {
        atomicRef = new AtomicReference(new ReferenceBooleanPair(initialRef, initialMark));
    }

    private ReferenceBooleanPair getPair() {
        return (ReferenceBooleanPair)atomicRef.get();
    }

    /**
     * Returns the current value of the reference.
     *
     * @return the current value of the reference
     */
    public Object getReference() {
        return getPair().reference;
    }

    /**
     * Returns the current value of the mark.
     *
     * @return the current value of the mark
     */
    public boolean isMarked() {
        return getPair().bit;
    }

    /**
     * Returns the current values of both the reference and the mark.
     * Typical usage is <tt>boolean[1] holder; ref = v.get(holder); </tt>.
     *
     * @param markHolder an array of size of at least one. On return,
     * <tt>markholder[0]</tt> will hold the value of the mark.
     * @return the current value of the reference
     */
    public Object get(boolean[] markHolder) {
        ReferenceBooleanPair p = getPair();
        markHolder[0] = p.bit;
        return p.reference;
    }

    /**
     * Atomically sets the value of both the reference and mark
     * to the given update values if the
     * current reference is <tt>==</tt> to the expected reference
     * and the current mark is equal to the expected mark.  Any given
     * invocation of this operation may fail (return
     * <tt>false</tt>) spuriously, but repeated invocation when
     * the current value holds the expected value and no other thread
     * is also attempting to set the value will eventually succeed.
     *
     * @param expectedReference the expected value of the reference
     * @param newReference the new value for the reference
     * @param expectedMark the expected value of the mark
     * @param newMark the new value for the mark
     * @return true if successful
     */
    public boolean weakCompareAndSet(Object  expectedReference,
                                     Object  newReference,
                                     boolean expectedMark,
                                     boolean newMark) {
        ReferenceBooleanPair current = getPair();
        return  expectedReference == current.reference &&
            expectedMark == current.bit &&
            ((newReference == current.reference && newMark == current.bit) ||
             atomicRef.weakCompareAndSet(current,
                                     new ReferenceBooleanPair(newReference,
                                                              newMark)));
    }

    /**
     * Atomically sets the value of both the reference and mark
     * to the given update values if the
     * current reference is <tt>==</tt> to the expected reference
     * and the current mark is equal to the expected mark.
     *
     * @param expectedReference the expected value of the reference
     * @param newReference the new value for the reference
     * @param expectedMark the expected value of the mark
     * @param newMark the new value for the mark
     * @return true if successful
     */
    public boolean compareAndSet(Object       expectedReference,
                                 Object       newReference,
                                 boolean expectedMark,
                                 boolean newMark) {
        ReferenceBooleanPair current = getPair();
        return  expectedReference == current.reference &&
            expectedMark == current.bit &&
            ((newReference == current.reference && newMark == current.bit) ||
             atomicRef.compareAndSet(current,
                                     new ReferenceBooleanPair(newReference,
                                                              newMark)));
    }

    /**
     * Unconditionally sets the value of both the reference and mark.
     *
     * @param newReference the new value for the reference
     * @param newMark the new value for the mark
     */
    public void set(Object newReference, boolean newMark) {
        ReferenceBooleanPair current = getPair();
        if (newReference != current.reference || newMark != current.bit)
            atomicRef.set(new ReferenceBooleanPair(newReference, newMark));
    }

    /**
     * Atomically sets the value of the mark to the given update value
     * if the current reference is <tt>==</tt> to the expected
     * reference.  Any given invocation of this operation may fail
     * (return <tt>false</tt>) spuriously, but repeated invocation
     * when the current value holds the expected value and no other
     * thread is also attempting to set the value will eventually
     * succeed.
     *
     * @param expectedReference the expected value of the reference
     * @param newMark the new value for the mark
     * @return true if successful
     */
    public boolean attemptMark(Object expectedReference, boolean newMark) {
        ReferenceBooleanPair current = getPair();
        return  expectedReference == current.reference &&
            (newMark == current.bit ||
             atomicRef.compareAndSet
             (current, new ReferenceBooleanPair(expectedReference,
                                                newMark)));
    }
}
