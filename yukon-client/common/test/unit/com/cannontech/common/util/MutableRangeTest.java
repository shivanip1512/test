package com.cannontech.common.util;

import static org.junit.Assert.*;

import org.junit.Test;

public class MutableRangeTest {
    private MutableRange<Integer> unbounded = new MutableRange<Integer>();
    private MutableRange<Integer> from5Inclusive_toUnbounded = new MutableRange<Integer>(5, null);
    private MutableRange<Integer> from5Exclusive_toUnbounded = new MutableRange<Integer>(5, false, null, false);
    private MutableRange<Integer> fromUnbounded_to5Inclusive = new MutableRange<Integer>(null, 5);
    private MutableRange<Integer> fromUnbounded_to5Exclusive = new MutableRange<Integer>(null, false, 5, false);
    private MutableRange<Integer> from10Inclusive_toUnbounded = new MutableRange<Integer>(10, null);
    private MutableRange<Integer> from10Exclusive_toUnbounded = new MutableRange<Integer>(10, false, null, false);
    private MutableRange<Integer> fromUnbounded_to10Inclusive = new MutableRange<Integer>(null, 10);
    private MutableRange<Integer> fromUnbounded_to10Exclusive = new MutableRange<Integer>(null, false, 10, false);
    private MutableRange<Integer> from5Inclusive_to10Inclusive = new MutableRange<Integer>(5, 10);
    private MutableRange<Integer> from5Exclusive_to10Exclusive = new MutableRange<Integer>(5, false, 10, false);
    private MutableRange<Integer> from5Exclusive_to10Inclusive = new MutableRange<Integer>(5, false, 10, true);
    private MutableRange<Integer> from5Inclusive_to10Exclusive = new MutableRange<Integer>(5, true, 10, false);
    private MutableRange<Integer> from5Inclusive_to5Inclusive = new MutableRange<Integer>(5, true, 5, true);
    private MutableRange<Integer> from5Inclusive_to5Exclusive = new MutableRange<Integer>(5, true, 5, false);
    private MutableRange<Integer> from5Exclusive_to5Exclusive = new MutableRange<Integer>(5, false, 5, false);
    private MutableRange<Integer> from5Exclusive_to5Inclusive = new MutableRange<Integer>(5, false, 5, true);

    private MutableRange<Integer> inverted = new MutableRange<Integer>(10, false, 5, false);

    private MutableRange<Float> unbounded_float = new MutableRange<Float>();
    private MutableRange<Float> from5Inclusive_toUnbounded_float = new MutableRange<Float>(5f, null);
    private MutableRange<Float> from5Exclusive_toUnbounded_float = new MutableRange<Float>(5f, false, null, false);
    private MutableRange<Float> fromUnbounded_to5Inclusive_float = new MutableRange<Float>(null, 5f);
    private MutableRange<Float> fromUnbounded_to5Exclusive_float = new MutableRange<Float>(null, false, 5f, false);
    private MutableRange<Float> from10Inclusive_toUnbounded_float = new MutableRange<Float>(10f, null);
    private MutableRange<Float> from10Exclusive_toUnbounded_float = new MutableRange<Float>(10f, false, null, false);
    private MutableRange<Float> fromUnbounded_to10Inclusive_float = new MutableRange<Float>(null, 10f);
    private MutableRange<Float> fromUnbounded_to10Exclusive_float = new MutableRange<Float>(null, false, 10f, false);
    private MutableRange<Float> from5Inclusive_to10Inclusive_float = new MutableRange<Float>(5f, 10f);
    private MutableRange<Float> from5Exclusive_to10Exclusive_float = new MutableRange<Float>(5f, false, 10f, false);
    private MutableRange<Float> from5Exclusive_to10Inclusive_float = new MutableRange<Float>(5f, false, 10f, true);
    private MutableRange<Float> from5Inclusive_to10Exclusive_float = new MutableRange<Float>(5f, true, 10f, false);
    private MutableRange<Float> from5Inclusive_to5Inclusive_float = new MutableRange<Float>(5f, true, 5f, true);
    private MutableRange<Float> from5Inclusive_to5Exclusive_float = new MutableRange<Float>(5f, true, 5f, false);
    private MutableRange<Float> from5Exclusive_to5Exclusive_float = new MutableRange<Float>(5f, false, 5f, false);
    private MutableRange<Float> from5Exclusive_to5Inclusive_float = new MutableRange<Float>(5f, false, 5f, true);

    private MutableRange<Float> inverted_float = new MutableRange<Float>(10f, false, 5f, false);
    
    private MutableRange<Integer> unbounded_neg = new MutableRange<Integer>();
    private MutableRange<Integer> from5Inclusive_toUnbounded_neg = new MutableRange<Integer>(-5, null);
    private MutableRange<Integer> from5Exclusive_toUnbounded_neg = new MutableRange<Integer>(-5, false, null, false);
    private MutableRange<Integer> fromUnbounded_to5Inclusive_neg = new MutableRange<Integer>(null, -5);
    private MutableRange<Integer> fromUnbounded_to5Exclusive_neg = new MutableRange<Integer>(null, false, -5, false);
    private MutableRange<Integer> from10Inclusive_toUnbounded_neg = new MutableRange<Integer>(-10, null);
    private MutableRange<Integer> from10Exclusive_toUnbounded_neg = new MutableRange<Integer>(-10, false, null, false);
    private MutableRange<Integer> fromUnbounded_to10Inclusive_neg = new MutableRange<Integer>(null, -10);
    private MutableRange<Integer> fromUnbounded_to10Exclusive_neg = new MutableRange<Integer>(null, false, -10, false);
    private MutableRange<Integer> from10Inclusive_to5Inclusive_neg = new MutableRange<Integer>(-10, -5);
    private MutableRange<Integer> from10Exclusive_to5Exclusive_neg = new MutableRange<Integer>(-10, false, -5, false);
    private MutableRange<Integer> from10Exclusive_to5Inclusive_neg = new MutableRange<Integer>(-10, false, -5, true);
    private MutableRange<Integer> from10Inclusive_to5Exclusive_neg = new MutableRange<Integer>(-10, true, -5, false);
    private MutableRange<Integer> from5Inclusive_to5Inclusive_neg = new MutableRange<Integer>(-5, true, -5, true);
    private MutableRange<Integer> from5Inclusive_to5Exclusive_neg = new MutableRange<Integer>(-5, true, -5, false);
    private MutableRange<Integer> from5Exclusive_to5Exclusive_neg = new MutableRange<Integer>(-5, false, -5, false);
    private MutableRange<Integer> from5Exclusive_to5Inclusive_neg = new MutableRange<Integer>(-5, false, -5, true);

    private MutableRange<Integer> inverted_neg = new MutableRange<Integer>(-5, false, -10, false);
    
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
        MutableRange<Integer> from5Inclusive_toUnbounded_2 = new MutableRange<Integer>(5, null);
        assertEquals(from5Inclusive_toUnbounded, from5Inclusive_toUnbounded_2);
        MutableRange<Integer> fromUnbounded_to5Inclusive_2 = new MutableRange<Integer>(null, 5);
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
        MutableRange<Float> from5Inclusive_toUnbounded_2_float = new MutableRange<Float>(5f, null);
        assertEquals(from5Inclusive_toUnbounded_float, from5Inclusive_toUnbounded_2_float);
        MutableRange<Float> fromUnbounded_to5Inclusive_2_float = new MutableRange<Float>(null, 5f);
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
        MutableRange<Integer> from5Inclusive_toUnbounded_2_neg = new MutableRange<Integer>(-5, null);
        assertEquals(from5Inclusive_toUnbounded_neg, from5Inclusive_toUnbounded_2_neg);
        MutableRange<Integer> fromUnbounded_to5Inclusive_2_neg = new MutableRange<Integer>(null, -5);
        assertEquals(fromUnbounded_to5Inclusive_neg, fromUnbounded_to5Inclusive_2_neg);

        assertNotSame(unbounded_neg, from5Inclusive_toUnbounded_neg);
        assertNotSame(from5Inclusive_toUnbounded_neg, from5Exclusive_toUnbounded_neg);
    }

    @Test
    public void testValid() {
        assertFalse(inverted.isValid());

        assertTrue(from5Inclusive_toUnbounded.isValid());
        assertTrue(from5Inclusive_toUnbounded.isValid() && !from5Inclusive_toUnbounded.isEmpty());
        assertTrue(from5Exclusive_toUnbounded.isValid());
        assertTrue(from5Exclusive_toUnbounded.isValid() && !from5Exclusive_toUnbounded.isEmpty());
        assertTrue(fromUnbounded_to5Inclusive.isValid());
        assertTrue(fromUnbounded_to5Inclusive.isValid() && !fromUnbounded_to5Inclusive.isEmpty());
        assertTrue(fromUnbounded_to5Exclusive.isValid());
        assertTrue(fromUnbounded_to5Exclusive.isValid() && !fromUnbounded_to5Exclusive.isEmpty());

        assertTrue(from5Inclusive_to5Inclusive.isValid());
        assertTrue(from5Inclusive_to5Inclusive.isValid() && !from5Inclusive_to5Inclusive.isEmpty());
        assertTrue(from5Inclusive_to5Exclusive.isValid());
        assertTrue(!from5Inclusive_to5Exclusive.isValid() || from5Inclusive_to5Exclusive.isEmpty());
        assertTrue(from5Exclusive_to5Exclusive.isValid());
        assertTrue(!from5Exclusive_to5Exclusive.isValid() || from5Exclusive_to5Exclusive.isEmpty());
        assertTrue(from5Exclusive_to5Inclusive.isValid());
        assertTrue(!from5Exclusive_to5Inclusive.isValid() || from5Exclusive_to5Inclusive.isEmpty());

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
