/*
 * Written by Doug Lea with assistance from members of JCP JSR-166
 * Expert Group and released to the public domain, as explained at
 * http://creativecommons.org/licenses/publicdomain
 */

package edu.emory.mathcs.backport.java.util.concurrent;
import java.util.*;
import edu.emory.mathcs.backport.java.util.*;

/**
 * A {@link ConcurrentMap} supporting {@link NavigableMap} operations.
 *
 * @author Doug Lea
 */
public interface ConcurrentNavigableMap extends ConcurrentMap, NavigableMap {
    /**
     * Returns a view of the portion of this map whose keys range from
     * <tt>fromKey</tt>, inclusive, to <tt>toKey</tt>, exclusive.  (If
     * <tt>fromKey</tt> and <tt>toKey</tt> are equal, the returned sorted map
     * is empty.)  The returned sorted map is backed by this map, so changes
     * in the returned sorted map are reflected in this map, and vice-versa.

     * @param fromKey low endpoint (inclusive) of the subMap.
     * @param toKey high endpoint (exclusive) of the subMap.
     *
     * @return a view of the portion of this map whose keys range from
     * <tt>fromKey</tt>, inclusive, to <tt>toKey</tt>, exclusive.
     *
     * @throws ClassCastException if <tt>fromKey</tt> and
     * <tt>toKey</tt> cannot be compared to one another using this
     * map's comparator (or, if the map has no comparator, using
     * natural ordering).
     * @throws IllegalArgumentException if <tt>fromKey</tt> is greater
     * than <tt>toKey</tt>.
     * @throws NullPointerException if <tt>fromKey</tt> or
     * <tt>toKey</tt> is <tt>null</tt> and this map does not support
     * <tt>null</tt> keys.
     */
    public SortedMap subMap(Object fromKey, Object toKey);

    /**
     * Returns a view of the portion of this map whose keys are strictly less
     * than <tt>toKey</tt>.  The returned sorted map is backed by this map, so
     * changes in the returned sorted map are reflected in this map, and
     * vice-versa.
     * @param toKey high endpoint (exclusive) of the headMap.
     * @return a view of the portion of this map whose keys are strictly
     *                less than <tt>toKey</tt>.
     *
     * @throws ClassCastException if <tt>toKey</tt> is not compatible
     *         with this map's comparator (or, if the map has no comparator,
     *         if <tt>toKey</tt> does not implement <tt>Comparable</tt>).
     * @throws NullPointerException if <tt>toKey</tt> is <tt>null</tt>
     * and this map does not support <tt>null</tt> keys.
     */
    public SortedMap headMap(Object toKey);

    /**
     * Returns a view of the portion of this map whose keys are
     * greater than or equal to <tt>fromKey</tt>.  The returned sorted
     * map is backed by this map, so changes in the returned sorted
     * map are reflected in this map, and vice-versa.
     * @param fromKey low endpoint (inclusive) of the tailMap.
     * @return a view of the portion of this map whose keys are greater
     *                than or equal to <tt>fromKey</tt>.
     * @throws ClassCastException if <tt>fromKey</tt> is not compatible
     *         with this map's comparator (or, if the map has no comparator,
     *         if <tt>fromKey</tt> does not implement <tt>Comparable</tt>).
     * @throws NullPointerException if <tt>fromKey</tt> is <tt>null</tt>
     * and this map does not support <tt>null</tt> keys.
     */
    public SortedMap tailMap(Object fromKey);
}
