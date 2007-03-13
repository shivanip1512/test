package com.cannontech.common.util;

import static org.junit.Assert.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

public class MapQueueTest {
    private MapQueue<String,String> mq;

    @Before
    public void setUp() throws Exception {
        mq = new MapQueue<String, String>();
        mq.offer("bob", "1");
        mq.offer("jim", "1");
        mq.offer("bob", "2");
    }

    @Test
    public void testOffer() {
        assertFalse(mq.offer("asdf", null));
        
        try {
            mq.offer(null, "whatever");
            fail("null keys should cause exception");
        } catch (IllegalArgumentException e) {
        }
        
    }

    @Test
    public void testContainsKey() {
        assertTrue(mq.containsKey("jim"));
        assertFalse(mq.containsKey("ed"));
        mq.remove("jim");
        assertFalse(mq.containsKey("jim"));
    }

    @Test
    public void testContainsValue() {
        assertTrue(mq.containsValue("bob", "1"));
        assertTrue(mq.containsValue("jim", "1"));
        assertFalse(mq.containsValue("bob", "3"));
        assertFalse(mq.containsValue("jim", "2"));
    }

    @Test
    public void testGet() {
        List<String> list = mq.get("bob");
        assertEquals(2, list.size());
        assertEquals("1", list.get(0));
        assertEquals("2", list.get(1));
        
        try {
            list.add("asdf");
            fail("list should be unmodifiable");
        } catch (UnsupportedOperationException e) {
        }
    }

    @Test
    public void testRemoveKey() {
        Queue<String> q = mq.removeKey("bob");
        assertEquals("1", q.remove());
        assertEquals("2", q.remove());
        
        assertEquals(0, mq.removeKey("asdf").size());
        
    }

    @Test
    public void testRemoveValue() {
        mq.offer("bob", "3");
        assertTrue(mq.removeValue("bob", "2"));
        assertFalse(mq.removeValue("bob", "asdf"));
        assertTrue(mq.containsValue("bob", "1"));
        assertFalse(mq.containsValue("bob", "2"));
        assertTrue(mq.containsValue("bob", "3"));
        
        assertEquals("1", mq.remove("bob"));
        assertEquals("3", mq.remove("bob"));
    }

    @Test
    public void testPoll() {
        assertEquals("1", mq.poll("bob"));
        assertEquals("2", mq.poll("bob"));
        assertEquals(null, mq.poll("bob"));
        assertEquals("1", mq.poll("jim"));
        mq.offer("bob", "9");
        mq.offer("tom", "5");
        mq.offer("bob", "10");
        assertEquals("9", mq.poll("bob"));
        assertEquals("10", mq.poll("bob"));
        assertEquals(null, mq.poll("bob"));
        assertEquals(null, mq.poll("jim"));
        assertEquals("5", mq.poll("tom"));
        assertEquals(null, mq.poll("tom"));
        
        assertEquals(null, mq.poll("asdf"));
        
        assertEquals(0, mq.size());
    }

    @Test
    public void testSizeA() {
        assertEquals(2, mq.size("bob"));
        assertEquals(1, mq.size("jim"));
        assertEquals(0, mq.size("asdf"));
    }

    @Test
    public void testSize() {
        assertEquals(2, mq.size());
    }
    
    @Test
    public void testRemove() {
        assertEquals("1", mq.remove("bob"));
        assertEquals("2", mq.remove("bob"));
        try {
            assertEquals(null, mq.remove("bob"));
            fail("should have thrown exception");
        } catch (NoSuchElementException e) {
        }
        mq.offer("bob", "9");
        mq.offer("tom", "5");
        mq.offer("bob", "10");
        assertEquals("9", mq.remove("bob"));
        assertEquals("10", mq.remove("bob"));
        try {
            assertEquals(null, mq.remove("bob"));
            fail("should have thrown exception");
        } catch (NoSuchElementException e) {
        }
        assertEquals("5", mq.remove("tom"));
        try {
            assertEquals(null, mq.remove("tom"));
            fail("should have thrown exception");
        } catch (NoSuchElementException e) {
        }
    }
    
    @Test
    public void testKeySet() {
        Set<String> set = mq.keySet();
        assertEquals(2, set.size());
        assertTrue(set.contains("bob"));
        assertTrue(set.contains("jim"));
        
        try {
            set.add("whatever");
            fail("should have thrown an exception");
        } catch (UnsupportedOperationException e) {
        }
    }

}
