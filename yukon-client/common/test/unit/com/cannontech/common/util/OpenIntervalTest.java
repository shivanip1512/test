package com.cannontech.common.util;

import static junit.framework.Assert.fail;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Instant;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableList;

public class OpenIntervalTest {
    
    /*
     *       000000000111111111122222
     *       123456789012345678901234
     *    A  |-----------------|
     *    B       |-------|
     *    C    |-----|
     *    D       |--|
     *    E             |------|
     *    F  <-------|
     *    G       |----------------->
     *    H  <---------------------->
     *    I  <------------|
     *    J                    |---->
     *    K               |--------->
     *    L  <----|
     *    Z   [null]
     */
    
    OpenInterval a = OpenInterval.createClosed(hour(1), hour(19));
    OpenInterval b = OpenInterval.createClosed(hour(6), hour(14));
    OpenInterval c = OpenInterval.createClosed(hour(3), hour(9));
    OpenInterval d = OpenInterval.createClosed(hour(6), hour(9));
    OpenInterval e = OpenInterval.createClosed(hour(12), hour(19));
    OpenInterval f = OpenInterval.createOpenStart(hour(9));
    OpenInterval g = OpenInterval.createOpenEnd(hour(6));
    OpenInterval h = OpenInterval.createUnBounded();
    OpenInterval i = OpenInterval.createOpenStart(hour(14));
    OpenInterval j = OpenInterval.createOpenEnd(hour(19));
    OpenInterval k = OpenInterval.createOpenEnd(hour(14));
    OpenInterval l = OpenInterval.createOpenStart(hour(6));
    OpenInterval z = null;

    @Before
    public void setUp() throws Exception {
    }
    
    private Instant hour(int hour) {
        return new DateTime(2010,10,10,hour,0,0,0, DateTimeZone.UTC).toInstant();
    }

    @Test
    public void testIsBefore() {
        assertTrue(d.isBefore(e));
        assertTrue(i.isBefore(j));
        assertTrue(c.isBefore(j));
        
        assertFalse(e.isBefore(d));
        assertFalse(j.isBefore(i));
        assertFalse(j.isBefore(c));
        
        assertFalse(h.isBefore(i));
        assertFalse(f.isBefore(g));
        assertFalse(g.isBefore(f));
        
    }

    @Test
    public void testIsAfter() {
        assertTrue(e.isAfter(d));
        assertTrue(j.isAfter(i));
        assertTrue(j.isAfter(c));
        
        assertFalse(d.isAfter(e));
        assertFalse(i.isAfter(j));
        assertFalse(c.isAfter(j));
        
        assertFalse(h.isAfter(i));
        assertFalse(f.isAfter(g));
        assertFalse(g.isAfter(f));
        
    }

    @Test
    public void testContainsReadableInstant() {
        assertTrue(a.contains(hour(9)));
        assertTrue(j.contains(hour(23)));
        assertTrue(f.contains(hour(4)));
        assertTrue(h.contains(hour(12)));
    }

    @Test
    public void testOverlaps() {
        assertTrue(a.overlaps(b));
        assertTrue(b.overlaps(a));
        
        assertTrue(f.overlaps(g));
        assertTrue(g.overlaps(f));
        
        assertTrue(b.overlaps(e));
        assertTrue(e.overlaps(b));
        
        assertTrue(h.overlaps(i));
        assertTrue(i.overlaps(h));
        
        assertFalse(i.overlaps(j));
        assertFalse(j.overlaps(i));
    }

    @Test
    public void testOverlap() {
        assertEquals(d, b.overlap(c));
        assertEquals(d, c.overlap(b));
        
        assertEquals(d, f.overlap(g));
        assertEquals(d, g.overlap(f));
        
        assertEquals(j, h.overlap(j));
        assertEquals(j, j.overlap(h));
        
    }

    @Test
    public void testContainsOpenInterval() {
        assertTrue(a.contains(b));
        assertFalse(b.contains(a));
        assertTrue(a.contains(a));
        
        assertTrue(i.contains(c));
        assertFalse(c.contains(i));
        
        assertTrue(g.contains(e));
        assertFalse(e.contains(g));
        
        assertTrue(h.contains(b));
        assertFalse(b.contains(h));
        
        assertTrue(i.contains(f));
        assertFalse(f.contains(i));
        
    }
    
    @Test
    public void testInvert() {
        // K+L is the inverse of B
        assertEquals(ImmutableList.of(l, k), b.invert());
        
        // K is the inverse of I
        assertEquals(ImmutableList.of(k), i.invert());
        assertEquals(ImmutableList.of(i), k.invert());
    }
    
    @Test
    public void testIntersection() {
        List<OpenInterval> one = ImmutableList.of(d, k);
        List<OpenInterval> two = ImmutableList.of(c, j);
        List<OpenInterval> three = ImmutableList.of(h);
        List<OpenInterval> four = ImmutableList.of(g);
        
        ImmutableList<OpenInterval> expected = ImmutableList.of(d, j);
        assertEquals(expected, OpenInterval.intersection(ImmutableList.of(one, two, three, four)));
        
        List<OpenInterval> bad = ImmutableList.of(f,a);
        try {
            OpenInterval.intersection(ImmutableList.of(one, two, bad, three, four));
            fail();
        } catch (Exception e) {
        }
    }

}
