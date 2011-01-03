package com.cannontech.common.util;

import static org.junit.Assert.*;

import org.junit.Test;

public class RangeTest {
    private Range<Integer> unbounded = new Range<Integer>();
    private Range<Integer> from5Inclusive_toUnbounded = new Range<Integer>(5, null);
    private Range<Integer> from5Exclusive_toUnbounded = new Range<Integer>(5, false, null, false);
    private Range<Integer> fromUnbounded_to5Inclusive = new Range<Integer>(null, 5);
    private Range<Integer> fromUnbounded_to5Exclusive = new Range<Integer>(null, false, 5, false);
    private Range<Integer> from10Inclusive_toUnbounded = new Range<Integer>(10, null);
    private Range<Integer> from10Exclusive_toUnbounded = new Range<Integer>(10, false, null, false);
    private Range<Integer> fromUnbounded_to10Inclusive = new Range<Integer>(null, 10);
    private Range<Integer> fromUnbounded_to10Exclusive = new Range<Integer>(null, false, 10, false);
    private Range<Integer> from5Inclusive_to10Inclusive = new Range<Integer>(5, 10);
    private Range<Integer> from5Exclusive_to10Exclusive = new Range<Integer>(5, false, 10, false);
    private Range<Integer> from5Exclusive_to10Inclusive = new Range<Integer>(5, false, 10, true);
    private Range<Integer> from5Inclusive_to10Exclusive = new Range<Integer>(5, true, 10, false);
    private Range<Integer> from5Inclusive_to5Inclusive = new Range<Integer>(5, true, 5, true);
    private Range<Integer> from5Inclusive_to5Exclusive = new Range<Integer>(5, true, 5, false);
    private Range<Integer> from5Exclusive_to5Exclusive = new Range<Integer>(5, false, 5, false);
    private Range<Integer> from5Exclusive_to5Inclusive = new Range<Integer>(5, false, 5, true);

    private Range<Integer> inverted = new Range<Integer>(10, false, 5, false);

    @Test
    public void testBounded() {

        assertTrue(unbounded.isUnbounded());
        assertFalse(from5Inclusive_toUnbounded.isUnbounded());

        assertNull(unbounded.getMin());
        assertNull(unbounded.getMax());

        assertNull(from5Inclusive_toUnbounded.getMax());
        assertNull(from5Exclusive_toUnbounded.getMax());
        assertNull(fromUnbounded_to5Inclusive.getMin());
        assertNull(fromUnbounded_to5Exclusive.getMin());

        assertNull(from10Inclusive_toUnbounded.getMax());
        assertNull(from10Exclusive_toUnbounded.getMax());
        assertNull(fromUnbounded_to10Inclusive.getMin());
        assertNull(fromUnbounded_to10Exclusive.getMin());

        // Exclusivity shouldn't matter when not bounded.
        Range<Integer> from5Inclusive_toUnbounded_2 = new Range<Integer>(5, true, null, false);
        assertEquals(from5Inclusive_toUnbounded, from5Inclusive_toUnbounded_2);
        Range<Integer> fromUnbounded_to5Inclusive_2 = new Range<Integer>(null, false, 5, true);
        assertEquals(fromUnbounded_to5Inclusive, fromUnbounded_to5Inclusive_2);

        assertNotSame(unbounded, from5Inclusive_toUnbounded);
        assertNotSame(from5Inclusive_toUnbounded, from5Exclusive_toUnbounded);
    }

    @Test
    public void testEmpty() {
        assertTrue(inverted.isEmpty());
        assertFalse(from5Inclusive_toUnbounded.isEmpty());
        assertFalse(from5Exclusive_toUnbounded.isEmpty());
        assertFalse(fromUnbounded_to5Inclusive.isEmpty());
        assertFalse(fromUnbounded_to5Exclusive.isEmpty());

        assertFalse(from5Inclusive_to5Inclusive.isEmpty());
        assertTrue(from5Inclusive_to5Exclusive.isEmpty());
        assertTrue(from5Exclusive_to5Exclusive.isEmpty());
        assertTrue(from5Exclusive_to5Inclusive.isEmpty());

        // Nothing should intersect an empty range.
        assertFalse(inverted.intersects(0));
        assertFalse(inverted.intersects(5));
        assertFalse(inverted.intersects(7));
        assertFalse(inverted.intersects(10));
        assertFalse(inverted.intersects(15));
    }

    @Test
    public void testIntersects() {
        assertTrue(unbounded.intersects(5));

        assertFalse(from5Exclusive_toUnbounded.intersects(0));
        assertTrue(from5Inclusive_toUnbounded.intersects(5));
        assertFalse(from5Exclusive_toUnbounded.intersects(5));
        assertTrue(from5Inclusive_toUnbounded.intersects(10));

        assertTrue(fromUnbounded_to5Inclusive.intersects(0));
        assertTrue(fromUnbounded_to5Inclusive.intersects(5));
        assertFalse(fromUnbounded_to5Exclusive.intersects(5));
        assertFalse(fromUnbounded_to5Exclusive.intersects(10));

        assertFalse(from5Inclusive_to10Inclusive.intersects(0));
        assertTrue(from5Inclusive_to10Inclusive.intersects(5));
        assertTrue(from5Inclusive_to10Inclusive.intersects(7));
        assertTrue(from5Inclusive_to10Inclusive.intersects(10));
        assertFalse(from5Inclusive_to10Inclusive.intersects(15));

        assertFalse(from5Exclusive_to10Exclusive.intersects(0));
        assertFalse(from5Exclusive_to10Exclusive.intersects(5));
        assertTrue(from5Exclusive_to10Exclusive.intersects(7));
        assertFalse(from5Exclusive_to10Exclusive.intersects(10));
        assertFalse(from5Exclusive_to10Exclusive.intersects(15));

        assertFalse(from5Exclusive_to10Inclusive.intersects(0));
        assertFalse(from5Exclusive_to10Inclusive.intersects(5));
        assertTrue(from5Exclusive_to10Inclusive.intersects(7));
        assertTrue(from5Exclusive_to10Inclusive.intersects(10));
        assertFalse(from5Exclusive_to10Inclusive.intersects(15));

        assertFalse(from5Inclusive_to10Exclusive.intersects(0));
        assertTrue(from5Inclusive_to10Exclusive.intersects(5));
        assertTrue(from5Inclusive_to10Exclusive.intersects(7));
        assertFalse(from5Inclusive_to10Exclusive.intersects(10));
        assertFalse(from5Inclusive_to10Exclusive.intersects(15));
    }
}
