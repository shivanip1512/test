package com.cannontech.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.Date;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ExpireLRUMapTest {
    private Calendar now;
    private Calendar yesterday;
    private ExpireLRUMap<String,SimpleReadDate> map;
    private SimpleReadDate nowRead;
    private SimpleReadDate yesterdayRead;
    
    @Before
    public void setUp() {
        now = Calendar.getInstance();
        
        yesterday = Calendar.getInstance();
        yesterday.set(Calendar.DAY_OF_YEAR, now.get(Calendar.DAY_OF_YEAR) - 1);
        
        nowRead = new SimpleReadDate();
        nowRead.setReadDate(now.getTime());
        
        yesterdayRead = new SimpleReadDate();
        yesterdayRead.setReadDate(yesterday.getTime());
        
        map = new ExpireLRUMap<String,SimpleReadDate>();
        map.put("now", nowRead);
        map.put("yesterday", yesterdayRead);
    }
    
    @After
    public void tearDown() {
        map = null;
        now = null;
        yesterday = null;
        nowRead = null;
        yesterdayRead = null;
    }
    
    @Test
    public void test_get() {
        assertNull("has expired, should be null", map.get("yesterday"));
        assertNull("has already been expired, should be null", map.get("yesterday"));
        assertNotNull("has not expired yet, should not be null", map.get("now"));
    }
    
    @Test
    public void test_containsKey() {
        assertTrue("has not expired yet, should be true", map.containsKey("now"));
        assertNotNull("has not expired yet, should not be null", map.get("now"));
        assertFalse("has expired, should be false", map.containsKey("yesterday"));
        assertNull("has expired, should be null", map.get("yesterday"));
    }
    
    
    @Test 
    public void test_containsValue() {
        assertTrue("has not expired yet, should be true", map.containsValue(nowRead));
        assertFalse("has expired, should be false", map.containsValue(yesterdayRead));
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
