package com.cannontech.util;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.GregorianCalendar;
import java.util.Date;

public class ExpireLRUMapTest {
    private static final int KEY_COUNT = 20;
    private GregorianCalendar now;
    private ExpireLRUMap<Integer,SimpleReadDate> map;
    
    @Before
    public void setUp() {
        now = new GregorianCalendar();
        map = new ExpireLRUMap<Integer,SimpleReadDate>();
        for (int x = 1; x < KEY_COUNT; x++) {
            SimpleReadDate readDate = new SimpleReadDate();
            readDate.setReadDate(new GregorianCalendar(now.get(now.YEAR), now.get(now.MONTH), now.get(now.DAY_OF_MONTH) - x).getTime());
            map.put(x, readDate);
        }
    }
    
    @After
    public void tearDown() {
        map = null;
    }
    
    @Test
    public void test_get() {
        for (int x = 1; x < KEY_COUNT; x++) {
            assertNotNull("has not expired yet, should not be null", map.get(x, x + 1));
            assertNull("has expired, should be null", map.get(x));
            assertNull("has already been expired, should be null", map.get(x));
        }
    }
    
    @Test
    public void test_containsKey() {
        for (int x = 1; x < KEY_COUNT; x++) {
            assertTrue("has not expired yet, should be true", map.containsKey(x, x + 1));
            assertNotNull("has not expired yet, should not be null", map.get(x, x + 1));
            assertFalse("has expired, should be false", map.containsKey(x));
            assertNull("has expired, should be null", map.get(x));
            assertNull("has already been expired, should be null", map.get(x));
        }
    }
    
    @Test 
    public void test_containsValue() {
        int days = 2;
        for (final SimpleReadDate readDate : map.values()) {
            assertTrue("has not expired yet, should be true", map.containsValue(readDate, days));
            assertFalse("has expired, should be false", map.containsValue(readDate));
            days++;
        }
    }
    
    public static class SimpleReadDate implements ExpireLRUMap.ReadDate {
        private Date readDate;
        
        public void setReadDate(final Date readDate) {
            this.readDate = readDate;
        }
        
        public Date getReadDate() {
            return readDate;
        }
        
        @Override
        public boolean equals(Object o) {
            if (o == this) return true;
            if (o != null && o instanceof SimpleReadDate) {
                return readDate.equals(((SimpleReadDate) o).readDate);
            }
            return false;
        }
    }
    
}
