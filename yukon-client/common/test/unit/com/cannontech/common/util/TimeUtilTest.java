package com.cannontech.common.util;

import static org.junit.Assert.assertEquals;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TimeUtilTest {
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
        halfDayAgo.set(2007, 8, 26, 22, 0, 0);
        
        oneDayAgo = (Calendar) baseTime.clone();
        oneDayAgo.set(2007, 8, 26, 10, 0, 0);
        
        twoDaysAgo = (Calendar) baseTime.clone();
        twoDaysAgo.set(2007, 8, 25, 10, 0, 0);
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
        assertEquals("difference in days should have been " + expectedResult, expectedResult, days);
    }

    @Test
    public void test_differenceInDays_halfDayAgo() {
        int days = TimeUtil.differenceInDays(halfDayAgo, baseTime);
        int expectedResult = 1;
        assertEquals("difference in days should have been " + expectedResult, expectedResult, days);
    }
    
    @Test
    public void test_differenceInDays_oneDayAgo() {
        int days = TimeUtil.differenceInDays(oneDayAgo, baseTime);
        int expectedResult = 1;
        assertEquals("difference in days should have been " + expectedResult, expectedResult, days);
    }
    
    @Test
    public void test_differenceInDays_twoDaysAgo() {
        int days = TimeUtil.differenceInDays(twoDaysAgo, baseTime);
        int expectedResult = 2;
        assertEquals("difference in days should have been " + expectedResult, expectedResult, days);
    }
    
    @Test
    public void test_differenceInDays_acrossYears() {
        Calendar oneYearAgo = (Calendar) baseTime.clone();
        oneYearAgo.set(Calendar.YEAR, baseTime.get(Calendar.YEAR) - 1);
        
        boolean leapYear = this.isLeapYear(oneYearAgo) || this.isLeapYear(baseTime);
        int yearDayCount = (leapYear) ? 366 : 365;
        
        int days = TimeUtil.differenceInDays(oneYearAgo, baseTime);
        assertEquals("difference in days should have been " + yearDayCount, yearDayCount, days);
    }
    
    @Test
    public void test_differenceInDays_within24hours_acrossYear() {
        oneDayAgo.set(2007, 11, 31, 22, 0, 0);
        baseTime.set(2008, 0, 1, 7, 0, 0);
        
        int expectedResult = 1;
        int days = TimeUtil.differenceInDays(oneDayAgo, baseTime);
        assertEquals("difference in days should have been " + expectedResult, expectedResult, days);
    }
    
    private boolean isLeapYear(final Calendar cal) {
        if (cal instanceof GregorianCalendar) {
            GregorianCalendar tempCal = (GregorianCalendar) cal;
            return tempCal.isLeapYear(tempCal.get(Calendar.YEAR));
        }
        return false;
    }
    
}
