package com.cannontech.common.util;

import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TimeUtilTest {
    private static final long oneDayInMillis = 86400000;
    private Calendar baseTime;
    private Calendar sameDay;
    private Calendar halfDayAgo;
    private Calendar oneDayAgo;
    private Calendar twoDaysAgo;
    
    @Before
    public void setUp() {
        baseTime = Calendar.getInstance();
        baseTime.set(2007, 8, 27, 10, 0, 0);
        
        sameDay = (Calendar) baseTime.clone();
        sameDay.set(Calendar.HOUR_OF_DAY, 11);
        
        halfDayAgo = (Calendar) baseTime.clone(); 
        halfDayAgo.setTimeInMillis(baseTime.getTimeInMillis() - (oneDayInMillis / 2));
        
        oneDayAgo = (Calendar) baseTime.clone();
        oneDayAgo.setTimeInMillis(baseTime.getTimeInMillis() - oneDayInMillis);
        
        twoDaysAgo = (Calendar) baseTime.clone();
        twoDaysAgo.setTimeInMillis(baseTime.getTimeInMillis() - (oneDayInMillis * 2));
    }
    
    @After
    public void tearDown() {
        baseTime = null;
        oneDayAgo = null;
        twoDaysAgo = null;
    }
    
    @Test
    public void test_differenceInDays_sameDay() {
        int expectedResult = 0;
        int days = TimeUtil.differenceInDays(baseTime, sameDay);
        boolean result = (days == expectedResult);
        assertTrue("difference in days should have been " + expectedResult, result);
    }

    @Test
    public void test_differenceInDays_halfDayAgo() {
        int baseDay = baseTime.get(Calendar.DAY_OF_YEAR);
        int halfDayAgoDay = halfDayAgo.get(Calendar.DAY_OF_YEAR);
        
        int days = TimeUtil.differenceInDays(halfDayAgo, baseTime);
        
        if (baseDay == halfDayAgoDay) {
            int expectedResult = 0;
            boolean result = (days == expectedResult);
            assertTrue("difference in days should have been " + expectedResult, result);
        } else {
            int expectedResult = 1;
            boolean result = (days == expectedResult);
            assertTrue("difference in days should have been " + expectedResult, result);
        }
    }
    
    @Test
    public void test_differenceInDays_oneDayAgo() {
        int days = TimeUtil.differenceInDays(oneDayAgo, baseTime);
        int expectedResult = 1;
        boolean result = (days == expectedResult);
        assertTrue("difference in days should have been " + expectedResult, result);
    }
    
    @Test
    public void test_differenceInDays_twoDaysAgo() {
        int days = TimeUtil.differenceInDays(twoDaysAgo, baseTime);
        int expectedResult = 2;
        boolean result = (days == expectedResult);
        assertTrue("difference in days should have been " + expectedResult, result);
    }
    
    @Test
    public void test_differenceInDays_acrossYears() {
        Calendar oneYearAgo = (Calendar) baseTime.clone();
        oneYearAgo.set(Calendar.YEAR, baseTime.get(Calendar.YEAR) - 1);
        
        boolean leapYear = this.isLeapYear(oneYearAgo) || this.isLeapYear(baseTime);
        int yearDayCount = (leapYear) ? 366 : 365;
        
        int days = TimeUtil.differenceInDays(oneYearAgo, baseTime);
        boolean result = (days == yearDayCount);
        assertTrue("difference in days should have been " + yearDayCount, result);
    }
    
    @Test
    public void test_differenceInDays_within24hours_acrossYear() {
        oneDayAgo.set(2007, 11, 31, 22, 0, 0);
        baseTime.set(2008, 0, 1, 7, 0, 0);
        
        int expectedResult = 1;
        int days = TimeUtil.differenceInDays(oneDayAgo, baseTime);
        boolean result = (days == expectedResult);
        assertTrue("difference in days should have been " + expectedResult, result);
    }
    
    private boolean isLeapYear(final Calendar cal) {
        if (cal instanceof GregorianCalendar) {
            GregorianCalendar tempCal = (GregorianCalendar) cal;
            return tempCal.isLeapYear(tempCal.get(Calendar.YEAR));
        }
        return false;
    }
    
}
