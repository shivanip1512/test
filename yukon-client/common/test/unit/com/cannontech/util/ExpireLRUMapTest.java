package com.cannontech.util;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Calendar;
import java.util.Date;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ExpireLRUMapTest {
    private Calendar now;
    private Calendar yesterday;
    private ExpireLRUMap<String,SimpleReadDate> map;
    private SimpleReadDate nowRead;
    private SimpleReadDate yesterdayRead;
    
    @BeforeEach
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
    
    @AfterEach
    public void tearDown() {
        map = null;
        now = null;
        yesterday = null;
        nowRead = null;
        yesterdayRead = null;
    }
    
    @Test
    public void test_get() {
        assertNull( map.get("yesterday"), "has expired, should be null");
        assertNull(map.get("yesterday"), "has already been expired, should be null");
        assertNotNull(map.get("now"), "has not expired yet, should not be null");
    }
    
    @Test
    public void test_containsKey() {
        assertTrue(map.containsKey("now"), "has not expired yet, should be true");
        assertNotNull(map.get("now"),"has not expired yet, should not be null");
        assertFalse(map.containsKey("yesterday"), "has expired, should be false");
        assertNull(map.get("yesterday"), "has expired, should be null");
    }
    
    
    @Test 
    public void test_containsValue() {
        assertTrue(map.containsValue(nowRead), "has not expired yet, should be true");
        assertFalse( map.containsValue(yesterdayRead), "has expired, should be false");
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
