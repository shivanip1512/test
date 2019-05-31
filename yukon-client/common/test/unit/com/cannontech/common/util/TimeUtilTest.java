package com.cannontech.common.util;

import static org.junit.Assert.assertEquals;

import java.text.ParseException;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.joda.time.DateTimeZone;
import org.joda.time.Instant;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
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
    
    @Test
    public void test_printNormalizedStandard() {
        assertEquals("00:00:00.683", TimeUtil.convertSecondsToNormalizedStandard(.683));
        assertEquals("00:00:01.000", TimeUtil.convertSecondsToNormalizedStandard(1));
        assertEquals("00:30:00.000", TimeUtil.convertSecondsToNormalizedStandard(1800));
        assertEquals("01:30:01.000", TimeUtil.convertSecondsToNormalizedStandard(5401));
        assertEquals("1 day 00:00:01.000", TimeUtil.convertSecondsToNormalizedStandard(86401));
        assertEquals("1 day 23:59:59.000", TimeUtil.convertSecondsToNormalizedStandard(172799));
        assertEquals("2 days 00:00:01.000", TimeUtil.convertSecondsToNormalizedStandard(172801));
        assertEquals("25 days 10:41:00.000", TimeUtil.convertSecondsToNormalizedStandard(2198460));
        assertEquals("33 days 10:41:00.000", TimeUtil.convertSecondsToNormalizedStandard(2889660));
        assertEquals("367 days 00:30:01.450", TimeUtil.convertSecondsToNormalizedStandard(31710601.45));
        
    }
    
    @Test
    public void test_buildCronExpression_24Hour_everyDay() throws ParseException {
        CronExprOption cronOption = CronExprOption.EVERYDAY;
        int time = 420;
        String days = "YYYYYYY"; // does not check for this if cronOption= EVERYDAY
        assertEquals("0 0 7 ? * *", TimeUtil.buildCronExpression(cronOption, time, days, 'Y'));
    }
    
    @Test
    public void test_buildCronExpression_24Hour_selectedDays() throws ParseException {
        CronExprOption cronOption = CronExprOption.WEEKDAYS;
        int time = 420;
        String days = "NYYYNYY";
        assertEquals("0 0 7 ? * 2,3,4,6,7", TimeUtil.buildCronExpression(cronOption, time, days, 'Y'));
    }

    @Test
    public void test_buildCronExpression_with48Hours_everyDay() throws ParseException {
        CronExprOption cronOption = CronExprOption.EVERYDAY;
        int time = 1860;
        String days = "NYYYNYY"; // does not check for this if cronOption= EVERYDAY
        assertEquals("0 0 7 ? * *", TimeUtil.buildCronExpression(cronOption, time, days, 'Y'));
    }

    @Test
    public void test_buildCronExpression_with48Hours_selectedDays() throws ParseException {
        CronExprOption cronOption = CronExprOption.WEEKDAYS;
        int time = 1860;
        String days = "NYYYNYY";
        assertEquals("0 0 7 ? * 3,4,5,7,1", TimeUtil.buildCronExpression(cronOption, time, days, 'Y'));
    }
    
    @Test(expected = ParseException.class)
    public void test_buildCronExpression_with48Hours_selectedDays_invalidTime() throws ParseException {
        CronExprOption cronOption = CronExprOption.WEEKDAYS;
        int time = 5660; // Invalid time value
        String days = "NYYYNYY";
        TimeUtil.buildCronExpression(cronOption, time, days, 'Y');
    }

    private boolean isLeapYear(final Calendar cal) {
        if (cal instanceof GregorianCalendar) {
            GregorianCalendar tempCal = (GregorianCalendar) cal;
            return tempCal.isLeapYear(tempCal.get(Calendar.YEAR));
        }
        return false;
    }

    @Test
    public void test_hoursRemainingAfterConveritngToDays_validHours_withExtraHours() {
        long totalHours = 25;
        long expectedResult = 1;
        assertEquals("Remaining hours should have been " + expectedResult, expectedResult,
            TimeUtil.hoursRemainingAfterConveritngToDays(totalHours));
    }

    @Test
    public void test_hoursRemainingAfterConveritngToDays_validHours_withoutExtraHours() {
        long totalHours = 48;
        long expectedResult = 0;
        assertEquals("Remaining hours should have been " + expectedResult, expectedResult,
            TimeUtil.hoursRemainingAfterConveritngToDays(totalHours));
    }

    @Test
    public void test_hoursRemainingAfterConveritngToDays_inValidHours() {
        long totalHours = -25;
        long expectedResult = 0;
        assertEquals("Remaining hours should have been " + expectedResult, expectedResult,
            TimeUtil.hoursRemainingAfterConveritngToDays(totalHours));
    }

    @Test
    public void test_convertToLocalInstant_IST() {
        DateTimeZone.setDefault(DateTimeZone.forID("Asia/Kolkata"));
        DateTimeFormatter timeFormater = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss").withZoneUTC();
        Instant instantInUTC = timeFormater.parseDateTime("2010-01-01 10:10:00").toInstant();
        Instant instantInIST = TimeUtil.convertToLocalInstant(instantInUTC);
        Instant expectedInstant = timeFormater.parseDateTime("2010-01-01 15:40:00").toInstant();
        assertEquals("UTC Instant " + instantInUTC + " conversion to IST Time Zone is " + instantInIST, expectedInstant,
            instantInIST);
        DateTimeZone.setDefault(DateTimeZone.forID("America/Chicago"));
    }

    @Test
    public void test_convertToLocalInstant_CDT() {
        DateTimeZone.setDefault(DateTimeZone.forID("America/Chicago"));
        DateTimeFormatter timeFormater = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss").withZoneUTC();
        Instant instantInUTC = timeFormater.parseDateTime("2010-01-01 10:10:00").toInstant();
        Instant instantInCDT = TimeUtil.convertToLocalInstant(instantInUTC);
        Instant expectedInstant = timeFormater.parseDateTime("2010-01-01 04:10:00").toInstant();
        assertEquals("Date in UTC" + instantInUTC.toDate() + " conversion to CDT Date is " + instantInCDT.toDate(),
            expectedInstant.toDate(), instantInCDT.toDate());
    }

    @Test
    public void test_convertToLocalInstant_WithoutZone() {
        DateTimeFormatter timeFormater = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss").withZoneUTC();
        Instant instantInUTC = timeFormater.parseDateTime("2010-01-01 10:10:00").toInstant();
        Instant instantInlocal = TimeUtil.convertToLocalInstant(instantInUTC);
        long serverTimeZone = DateTimeZone.getDefault().convertLocalToUTC(instantInlocal.getMillis(), true);
        Instant instantInServerTimezone = new Instant(serverTimeZone);
        assertEquals("UTC Instant " + instantInUTC + " to local time zone conversion is " + instantInlocal
            + " and local to UTC conversion is " + instantInServerTimezone, instantInUTC, instantInServerTimezone);
    }
}
