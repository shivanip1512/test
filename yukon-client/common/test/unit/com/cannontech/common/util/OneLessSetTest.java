package com.cannontech.common.util;

import static org.junit.Assert.*;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

public class OneLessSetTest {
    private Set<Integer> theWholeSet;

    @Before
    public void setUp() throws Exception {
        // we'll use a linked set to control the iteration order 
        // because the implementation is dependent on that
        
        theWholeSet = new LinkedHashSet<Integer>();
        theWholeSet.add(1);
        theWholeSet.add(2);
        theWholeSet.add(3);
        theWholeSet.add(4);
        theWholeSet.add(5);
    }

    @Test
    public void testSize() {
        assertEquals(5, theWholeSet.size());
        
        Set<Integer> oneLess = new OneLessSet<Integer>(theWholeSet, 3);
        
        assertEquals(4, oneLess.size());
    }

    @Test
    public void testIteratorBegin() {
        Set<Integer> oneLess = new OneLessSet<Integer>(theWholeSet, 1);
        Iterator<Integer> it = oneLess.iterator();
        assertTrue(it.hasNext());
        assertEquals(2, (int)it.next());
        assertTrue(it.hasNext());
        assertEquals(3, (int)it.next());
        assertTrue(it.hasNext());
        assertEquals(4, (int)it.next());
        assertTrue(it.hasNext());
        assertEquals(5, (int)it.next());
        assertFalse(it.hasNext());
    }

    @Test
    public void testIteratorMiddle() {
        Set<Integer> oneLess = new OneLessSet<Integer>(theWholeSet, 3);
        Iterator<Integer> it = oneLess.iterator();
        assertTrue(it.hasNext());
        assertEquals(1, (int)it.next());
        assertTrue(it.hasNext());
        assertEquals(2, (int)it.next());
        assertTrue(it.hasNext());
        assertEquals(4, (int)it.next());
        assertTrue(it.hasNext());
        assertEquals(5, (int)it.next());
        assertFalse(it.hasNext());
    }
    
    @Test
    public void testIteratorEnd() {
        Set<Integer> oneLess = new OneLessSet<Integer>(theWholeSet, 5);
        Iterator<Integer> it = oneLess.iterator();
        assertTrue(it.hasNext());
        assertEquals(1, (int)it.next());
        assertTrue(it.hasNext());
        assertEquals(2, (int)it.next());
        assertTrue(it.hasNext());
        assertEquals(3, (int)it.next());
        assertTrue(it.hasNext());
        assertEquals(4, (int)it.next());
        assertFalse(it.hasNext());
    }
    
    @Test
    public void testHashCode() {
        Set<Integer> oneLess = new OneLessSet<Integer>(theWholeSet, 3);
        Set<Integer> normal = new HashSet<Integer>();
        normal.add(1);
        normal.add(2);
        normal.add(4);
        normal.add(5);
        assertTrue(oneLess.hashCode() == normal.hashCode());
    }

    @Test
    public void testEquals() {
        Set<Integer> oneLess = new OneLessSet<Integer>(theWholeSet, 3);
        Set<Integer> normal = new HashSet<Integer>();
        normal.add(1);
        normal.add(2);
        normal.add(4);
        normal.add(5);
        assertEquals(oneLess, normal);
    }

    @Test
    public void testIsEmpty() {
        Set<Integer> oneLess = new OneLessSet<Integer>(theWholeSet, 3);
        assertFalse(oneLess.isEmpty());
        
        oneLess = new OneLessSet<Integer>(Collections.singleton(99), 99);
        assertTrue(oneLess.isEmpty());
    }

    @Test
    public void testContains() {
        Set<Integer> oneLess = new OneLessSet<Integer>(theWholeSet, 3);
        assertTrue(oneLess.contains(5));
        assertFalse(oneLess.contains(3));
    }

}
