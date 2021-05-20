package com.cannontech.common.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

public class RangeTest {
    
    final Range<Integer> unbounded = Range.unbounded();
    final Range<Integer> from5Inclusive_toUnbounded = (Range<Integer>)Range.inclusiveExclusive(5, null);
    final Range<Integer> from5Exclusive_toUnbounded = Range.exclusive(5, null);
    final Range<Integer> fromUnbounded_to5Inclusive = Range.exclusiveInclusive(null, 5);
    final Range<Integer> fromUnbounded_to5Exclusive = Range.exclusive(null, 5);
    final Range<Integer> from10Inclusive_toUnbounded = Range.inclusiveExclusive(10, null);
    final Range<Integer> from10Exclusive_toUnbounded = Range.exclusive(10, null);
    final Range<Integer> fromUnbounded_to10Inclusive = Range.exclusiveInclusive(null, 10);
    final Range<Integer> fromUnbounded_to10Exclusive = Range.exclusive(null, 10);
    final Range<Integer> from5Inclusive_to10Inclusive = Range.inclusive(5, 10);
    final Range<Integer> from5Exclusive_to10Exclusive = Range.exclusive(5, 10);
    final Range<Integer> from5Exclusive_to10Inclusive = Range.exclusiveInclusive(5, 10);
    final Range<Integer> from5Inclusive_to10Exclusive = Range.inclusiveExclusive(5, 10);
    final Range<Integer> from5Inclusive_to5Inclusive = Range.inclusive(5, 5);
    final Range<Integer> from5Inclusive_to5Exclusive = Range.inclusiveExclusive(5, 5);
    final Range<Integer> from5Exclusive_to5Exclusive = Range.exclusive(5, 5);
    final Range<Integer> from5Exclusive_to5Inclusive = Range.exclusiveInclusive(5, 5);
    
    final Range<Integer> inverted = Range.exclusive(10, 5);

    final Range<Float> unbounded_float = Range.unbounded();
    final Range<Float> from5Inclusive_toUnbounded_float = Range.inclusiveExclusive(5f, null);
    final Range<Float> from5Exclusive_toUnbounded_float = Range.exclusive(5f, null);
    final Range<Float> fromUnbounded_to5Inclusive_float = Range.exclusiveInclusive(null, 5f);
    final Range<Float> fromUnbounded_to5Exclusive_float = Range.exclusive(null, 5f);
    final Range<Float> from10Inclusive_toUnbounded_float = Range.inclusiveExclusive(10f, null);
    final Range<Float> from10Exclusive_toUnbounded_float = Range.exclusive(10f, null);
    final Range<Float> fromUnbounded_to10Inclusive_float = Range.exclusiveInclusive(null, 10f);
    final Range<Float> fromUnbounded_to10Exclusive_float = Range.exclusive(null, 10f);
    final Range<Float> from5Inclusive_to10Inclusive_float = Range.inclusive(5f, 10f);
    final Range<Float> from5Exclusive_to10Exclusive_float = Range.exclusive(5f, 10f);
    final Range<Float> from5Exclusive_to10Inclusive_float = Range.exclusiveInclusive(5f, 10f);
    final Range<Float> from5Inclusive_to10Exclusive_float = Range.inclusiveExclusive(5f, 10f);
    final Range<Float> from5Inclusive_to5Inclusive_float = Range.inclusive(5f, 5f);
    final Range<Float> from5Inclusive_to5Exclusive_float = Range.inclusiveExclusive(5f, 5f);
    final Range<Float> from5Exclusive_to5Exclusive_float = Range.exclusive(5f, 5f);
    final Range<Float> from5Exclusive_to5Inclusive_float = Range.exclusiveInclusive(5f, 5f);

    final Range<Float> inverted_float = Range.exclusive(10f, 5f);

    final Range<Integer> unbounded_neg = Range.unbounded();
    final Range<Integer> from5Inclusive_toUnbounded_neg = Range.inclusiveExclusive(-5, null);
    final Range<Integer> from5Exclusive_toUnbounded_neg = Range.exclusive(-5, null);
    final Range<Integer> fromUnbounded_to5Inclusive_neg = Range.exclusiveInclusive(null, -5);
    final Range<Integer> fromUnbounded_to5Exclusive_neg = Range.exclusive(null, -5);
    final Range<Integer> from10Inclusive_toUnbounded_neg = Range.inclusiveExclusive(-10, null);
    final Range<Integer> from10Exclusive_toUnbounded_neg = Range.exclusive(-10, null);
    final Range<Integer> fromUnbounded_to10Inclusive_neg = Range.exclusiveInclusive(null, -10);
    final Range<Integer> fromUnbounded_to10Exclusive_neg = Range.exclusive(null, -10);
    final Range<Integer> from10Inclusive_to5Inclusive_neg = Range.inclusive(-10, -5);
    final Range<Integer> from10Exclusive_to5Exclusive_neg = Range.exclusive(-10, -5);
    final Range<Integer> from10Exclusive_to5Inclusive_neg = Range.exclusiveInclusive(-10, -5);
    final Range<Integer> from10Inclusive_to5Exclusive_neg = Range.inclusiveExclusive(-10, -5);
    final Range<Integer> from5Inclusive_to5Inclusive_neg = Range.inclusive(-5, -5);
    final Range<Integer> from5Inclusive_to5Exclusive_neg = Range.inclusiveExclusive(-5, -5);
    final Range<Integer> from5Exclusive_to5Exclusive_neg = Range.exclusive(-5, -5);
    final Range<Integer> from5Exclusive_to5Inclusive_neg = Range.exclusiveInclusive(-5, -5);

    final Range<Integer> inverted_neg = Range.exclusive(-5, -10);

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
        Range<Integer> from5Inclusive_toUnbounded_2 = Range.inclusiveExclusive(5, null);
        assertEquals(from5Inclusive_toUnbounded, from5Inclusive_toUnbounded_2);
        Range<Integer> fromUnbounded_to5Inclusive_2 = Range.exclusiveInclusive(null, 5);
        assertEquals(fromUnbounded_to5Inclusive, fromUnbounded_to5Inclusive_2);

        assertNotSame(unbounded, from5Inclusive_toUnbounded);
        assertNotSame(from5Inclusive_toUnbounded, from5Exclusive_toUnbounded);
    }

    @Test
    public void testBoundedFloat() {

        assertTrue(unbounded_float.isUnbounded());
        assertFalse(from5Inclusive_toUnbounded_float.isUnbounded());

        assertNull(unbounded_float.getMin());
        assertNull(unbounded_float.getMax());

        assertNull(from5Inclusive_toUnbounded_float.getMax());
        assertNull(from5Exclusive_toUnbounded_float.getMax());
        assertNull(fromUnbounded_to5Inclusive_float.getMin());
        assertNull(fromUnbounded_to5Exclusive_float.getMin());

        assertNull(from10Inclusive_toUnbounded_float.getMax());
        assertNull(from10Exclusive_toUnbounded_float.getMax());
        assertNull(fromUnbounded_to10Inclusive_float.getMin());
        assertNull(fromUnbounded_to10Exclusive_float.getMin());

        // Exclusivity shouldn't matter when not bounded.
        Range<Float> from5Inclusive_toUnbounded_2_float = Range.inclusiveExclusive(5f, null);
        assertEquals(from5Inclusive_toUnbounded_float, from5Inclusive_toUnbounded_2_float);
        Range<Float> fromUnbounded_to5Inclusive_2_float = Range.exclusiveInclusive(null, 5f);
        assertEquals(fromUnbounded_to5Inclusive_float, fromUnbounded_to5Inclusive_2_float);

        assertNotSame(unbounded_float, from5Inclusive_toUnbounded_float);
        assertNotSame(from5Inclusive_toUnbounded_float, from5Exclusive_toUnbounded_float);
    }

    @Test
    public void testBoundedNeg() {

        assertTrue(unbounded_neg.isUnbounded());
        assertFalse(from5Inclusive_toUnbounded_neg.isUnbounded());

        assertNull(unbounded_neg.getMin());
        assertNull(unbounded_neg.getMax());

        assertNull(from5Inclusive_toUnbounded_neg.getMax());
        assertNull(from5Exclusive_toUnbounded_neg.getMax());
        assertNull(fromUnbounded_to5Inclusive_neg.getMin());
        assertNull(fromUnbounded_to5Exclusive_neg.getMin());

        assertNull(from10Inclusive_toUnbounded_neg.getMax());
        assertNull(from10Exclusive_toUnbounded_neg.getMax());
        assertNull(fromUnbounded_to10Inclusive_neg.getMin());
        assertNull(fromUnbounded_to10Exclusive_neg.getMin());

        // Exclusivity shouldn't matter when not bounded.
        Range<Integer> from5Inclusive_toUnbounded_2_neg = Range.inclusiveExclusive(-5, null);
        assertEquals(from5Inclusive_toUnbounded_neg, from5Inclusive_toUnbounded_2_neg);
        Range<Integer> fromUnbounded_to5Inclusive_2_neg = Range.exclusiveInclusive(null, -5);
        assertEquals(fromUnbounded_to5Inclusive_neg, fromUnbounded_to5Inclusive_2_neg);

        assertNotSame(unbounded_neg, from5Inclusive_toUnbounded_neg);
        assertNotSame(from5Inclusive_toUnbounded_neg, from5Exclusive_toUnbounded_neg);
    }

    @Test
    public void testValid() {
        assertFalse(inverted.isValid());

        assertTrue(from5Inclusive_toUnbounded.isValid());
        assertTrue(from5Inclusive_toUnbounded.isValid() &&
                   !from5Inclusive_toUnbounded.isEmpty());
        assertTrue(from5Exclusive_toUnbounded.isValid());
        assertTrue(from5Exclusive_toUnbounded.isValid() &&
                   !from5Exclusive_toUnbounded.isEmpty());
        assertTrue(fromUnbounded_to5Inclusive.isValid());
        assertTrue(fromUnbounded_to5Inclusive.isValid() &&
                   !fromUnbounded_to5Inclusive.isEmpty());
        assertTrue(fromUnbounded_to5Exclusive.isValid());
        assertTrue(fromUnbounded_to5Exclusive.isValid() &&
                   !fromUnbounded_to5Exclusive.isEmpty());

        assertTrue(from5Inclusive_to5Inclusive.isValid());
        assertTrue(from5Inclusive_to5Inclusive.isValid() &&
                   !from5Inclusive_to5Inclusive.isEmpty());
        assertTrue(from5Inclusive_to5Exclusive.isValid());
        assertTrue(!from5Inclusive_to5Exclusive.isValid() ||
                   from5Inclusive_to5Exclusive.isEmpty());
        assertTrue(from5Exclusive_to5Exclusive.isValid());
        assertTrue(!from5Exclusive_to5Exclusive.isValid() ||
                   from5Exclusive_to5Exclusive.isEmpty());
        assertTrue(from5Exclusive_to5Inclusive.isValid());
        assertTrue(!from5Exclusive_to5Inclusive.isValid() ||
                   from5Exclusive_to5Inclusive.isEmpty());

        try {
            inverted.isEmpty();
            fail(); // we should never get here b/c we should throw an exception
        } catch (IllegalStateException e) {
            // we should get into here
        }
    }

    @Test
    public void testValidFloat() {
        assertFalse(inverted_float.isValid());

        assertTrue(from5Inclusive_toUnbounded_float.isValid());
        assertTrue(from5Inclusive_toUnbounded_float.isValid() &&
                   !from5Inclusive_toUnbounded_float.isEmpty());
        assertTrue(from5Exclusive_toUnbounded_float.isValid());
        assertTrue(from5Exclusive_toUnbounded_float.isValid() &&
                   !from5Exclusive_toUnbounded_float.isEmpty());
        assertTrue(fromUnbounded_to5Inclusive_float.isValid());
        assertTrue(fromUnbounded_to5Inclusive_float.isValid() &&
                   !fromUnbounded_to5Inclusive_float.isEmpty());
        assertTrue(fromUnbounded_to5Exclusive_float.isValid());
        assertTrue(fromUnbounded_to5Exclusive_float.isValid() &&
                   !fromUnbounded_to5Exclusive_float.isEmpty());

        assertTrue(from5Inclusive_to5Inclusive_float.isValid());
        assertTrue(from5Inclusive_to5Inclusive_float.isValid() &&
                   !from5Inclusive_to5Inclusive_float.isEmpty());
        assertTrue(from5Inclusive_to5Exclusive_float.isValid());
        assertTrue(!from5Inclusive_to5Exclusive_float.isValid() ||
                   from5Inclusive_to5Exclusive_float.isEmpty());
        assertTrue(from5Exclusive_to5Exclusive_float.isValid());
        assertTrue(!from5Exclusive_to5Exclusive_float.isValid() ||
                   from5Exclusive_to5Exclusive_float.isEmpty());
        assertTrue(from5Exclusive_to5Inclusive_float.isValid());
        assertTrue(!from5Exclusive_to5Inclusive_float.isValid() ||
                   from5Exclusive_to5Inclusive_float.isEmpty());

        try {
            inverted_float.isEmpty();
            fail(); // we should never get here b/c we should throw an exception
        } catch (IllegalStateException e) {
            // we should get into here
        }
    }

    @Test
    public void testValidNeg() {
        assertFalse(inverted.isValid());

        assertTrue(from5Inclusive_toUnbounded_neg.isValid());
        assertTrue(from5Inclusive_toUnbounded_neg.isValid() &&
                   !from5Inclusive_toUnbounded_neg.isEmpty());
        assertTrue(from5Exclusive_toUnbounded_neg.isValid());
        assertTrue(from5Exclusive_toUnbounded_neg.isValid() &&
                   !from5Exclusive_toUnbounded_neg.isEmpty());
        assertTrue(fromUnbounded_to5Inclusive_neg.isValid());
        assertTrue(fromUnbounded_to5Inclusive_neg.isValid() &&
                   !fromUnbounded_to5Inclusive_neg.isEmpty());
        assertTrue(fromUnbounded_to5Exclusive_neg.isValid());
        assertTrue(fromUnbounded_to5Exclusive_neg.isValid() &&
                   !fromUnbounded_to5Exclusive_neg.isEmpty());

        assertTrue(from5Inclusive_to5Inclusive_neg.isValid());
        assertTrue(from5Inclusive_to5Inclusive_neg.isValid() &&
                   !from5Inclusive_to5Inclusive_neg.isEmpty());
        assertTrue(from5Inclusive_to5Exclusive_neg.isValid());
        assertTrue(!from5Inclusive_to5Exclusive_neg.isValid() ||
                   from5Inclusive_to5Exclusive_neg.isEmpty());
        assertTrue(from5Exclusive_to5Exclusive_neg.isValid());
        assertTrue(!from5Exclusive_to5Exclusive_neg.isValid() ||
                   from5Exclusive_to5Exclusive_neg.isEmpty());
        assertTrue(from5Exclusive_to5Inclusive_neg.isValid());
        assertTrue(!from5Exclusive_to5Inclusive_neg.isValid() ||
                   from5Exclusive_to5Inclusive_neg.isEmpty());

        try {
            inverted_neg.isEmpty();
            fail(); // we should never get here b/c we should throw an exception
        } catch (IllegalStateException e) {
            // we should get into here
        }
    }

    @Test
    public void testIntersects() {
        assertTrue(unbounded.intersects(5));

        // Nothing should intersect an empty range.
        assertFalse(inverted.intersects(0));
        assertFalse(inverted.intersects(5));
        assertFalse(inverted.intersects(7));
        assertFalse(inverted.intersects(10));
        assertFalse(inverted.intersects(15));

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

    @Test
    public void testIntersectsFloat() {
        assertTrue(unbounded_float.intersects(5f));

        // Nothing should intersect an empty range.
        assertFalse(inverted_float.intersects(0f));
        assertFalse(inverted_float.intersects(5f));
        assertFalse(inverted_float.intersects(7f));
        assertFalse(inverted_float.intersects(10f));
        assertFalse(inverted_float.intersects(15f));

        assertFalse(from5Exclusive_toUnbounded_float.intersects(0f));
        assertTrue(from5Inclusive_toUnbounded_float.intersects(5f));
        assertFalse(from5Exclusive_toUnbounded_float.intersects(5f));
        assertTrue(from5Inclusive_toUnbounded_float.intersects(10f));

        assertTrue(fromUnbounded_to5Inclusive_float.intersects(0f));
        assertTrue(fromUnbounded_to5Inclusive_float.intersects(5f));
        assertFalse(fromUnbounded_to5Exclusive_float.intersects(5f));
        assertFalse(fromUnbounded_to5Exclusive_float.intersects(10f));

        assertFalse(from5Inclusive_to10Inclusive_float.intersects(0f));
        assertTrue(from5Inclusive_to10Inclusive_float.intersects(5f));
        assertTrue(from5Inclusive_to10Inclusive_float.intersects(7f));
        assertTrue(from5Inclusive_to10Inclusive_float.intersects(10f));
        assertFalse(from5Inclusive_to10Inclusive_float.intersects(15f));

        assertFalse(from5Exclusive_to10Exclusive_float.intersects(0f));
        assertFalse(from5Exclusive_to10Exclusive_float.intersects(5f));
        assertTrue(from5Exclusive_to10Exclusive_float.intersects(7f));
        assertFalse(from5Exclusive_to10Exclusive_float.intersects(10f));
        assertFalse(from5Exclusive_to10Exclusive_float.intersects(15f));

        assertFalse(from5Exclusive_to10Inclusive_float.intersects(0f));
        assertFalse(from5Exclusive_to10Inclusive_float.intersects(5f));
        assertTrue(from5Exclusive_to10Inclusive_float.intersects(7f));
        assertTrue(from5Exclusive_to10Inclusive_float.intersects(10f));
        assertFalse(from5Exclusive_to10Inclusive_float.intersects(15f));

        assertFalse(from5Inclusive_to10Exclusive_float.intersects(0f));
        assertTrue(from5Inclusive_to10Exclusive_float.intersects(5f));
        assertTrue(from5Inclusive_to10Exclusive_float.intersects(7f));
        assertFalse(from5Inclusive_to10Exclusive_float.intersects(10f));
        assertFalse(from5Inclusive_to10Exclusive_float.intersects(15f));
    }

    @Test
    public void testIntersectsNeg() {
        assertTrue(unbounded_neg.intersects(5));

        // Nothing should intersect an empty range.
        assertFalse(inverted_neg.intersects(0));
        assertFalse(inverted_neg.intersects(-5));
        assertFalse(inverted_neg.intersects(-7));
        assertFalse(inverted_neg.intersects(-10));
        assertFalse(inverted_neg.intersects(-15));

        assertTrue(from5Exclusive_toUnbounded_neg.intersects(0));
        assertTrue(from5Inclusive_toUnbounded_neg.intersects(-5));
        assertFalse(from5Exclusive_toUnbounded_neg.intersects(-5));
        assertFalse(from5Inclusive_toUnbounded_neg.intersects(-10));

        assertFalse(fromUnbounded_to5Inclusive_neg.intersects(0));
        assertTrue(fromUnbounded_to5Inclusive_neg.intersects(-5));
        assertFalse(fromUnbounded_to5Exclusive_neg.intersects(-5));
        assertTrue(fromUnbounded_to5Exclusive_neg.intersects(-10));

        assertFalse(from10Inclusive_to5Inclusive_neg.intersects(0));
        assertTrue(from10Inclusive_to5Inclusive_neg.intersects(-5));
        assertTrue(from10Inclusive_to5Inclusive_neg.intersects(-7));
        assertTrue(from10Inclusive_to5Inclusive_neg.intersects(-10));
        assertFalse(from10Inclusive_to5Inclusive_neg.intersects(-15));

        assertFalse(from10Exclusive_to5Exclusive_neg.intersects(0));
        assertFalse(from10Exclusive_to5Exclusive_neg.intersects(-5));
        assertTrue(from10Exclusive_to5Exclusive_neg.intersects(-7));
        assertFalse(from10Exclusive_to5Exclusive_neg.intersects(-10));
        assertFalse(from10Exclusive_to5Exclusive_neg.intersects(-15));

        assertFalse(from10Exclusive_to5Inclusive_neg.intersects(0));
        assertTrue(from10Exclusive_to5Inclusive_neg.intersects(-5));
        assertTrue(from10Exclusive_to5Inclusive_neg.intersects(-7));
        assertFalse(from10Exclusive_to5Inclusive_neg.intersects(-10));
        assertFalse(from10Exclusive_to5Inclusive_neg.intersects(-15));

        assertFalse(from10Inclusive_to5Exclusive_neg.intersects(0));
        assertFalse(from10Inclusive_to5Exclusive_neg.intersects(-5));
        assertTrue(from10Inclusive_to5Exclusive_neg.intersects(-7));
        assertTrue(from10Inclusive_to5Exclusive_neg.intersects(-10));
        assertFalse(from10Inclusive_to5Exclusive_neg.intersects(-15));
    }
}
