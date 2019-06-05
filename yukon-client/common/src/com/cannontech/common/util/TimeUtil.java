package com.cannontech.common.util;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Duration;
import org.joda.time.IllegalFieldValueException;
import org.joda.time.Instant;
import org.joda.time.Interval;
import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.joda.time.ReadableInstant;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;
import org.quartz.CronExpression;

import com.google.common.collect.Lists;

/**
 * This type really needs to be looked at before it is used
 */
public class TimeUtil
{
	private static java.util.GregorianCalendar c1 = new java.util.GregorianCalendar();
	private static java.util.GregorianCalendar c2 = new java.util.GregorianCalendar();

/**
 * This method bases its result on 24 hour blocks and does not take into
 * account for Calendar.DAY_OF_YEAR.  If two Date Objects are within 24 hours
 * but across Calendar dates the result will be 0.
 *
 * @return int
 * @param d1 java.util.Date
 * @param d2 java.util.Date
 */
public static int differenceInDays(java.util.Date d1, java.util.Date d2 )
{
	c1.setTime( d1 );
	c2.setTime( d2 );
	//NEEDS TO BE DOUBLE SO WE GET THE PRECISION DURING DST CALCS (sn)
	//ROUND THE DIFFINDAYS TO THE NEAREST WHOLE DAY FOR THE SAKE OF DST
	int count = (int) Math.round(((double) (c1.getTimeInMillis() - c2.getTimeInMillis())) / (double) 86400000 );
	return count;
}
    /**
     * This method assumes that both Calendar Objects already have time zones set.
     * When two Calendar Objects are within 24 hours of one another but across calendar
     * dates the result will be 1 day.
     *
     *  NOTE: - Calendars with different time zones but within the same calendar date
     *          could lead to a result of 1 day.
     *        - This method does take into account for Leap Years.
     *        - This method does *NOT* take into account for DST.
     *
     * @param cal1 java.util.Calendar
     * @param cal2 java.util.Calendar
     * @return int Difference in Days between cal1 and cal2
     */
    public static int differenceInDays(final Calendar cal1, final Calendar cal2) {
        Calendar cal1Temp = Calendar.getInstance();
        cal1Temp.setTimeZone(cal1.getTimeZone());
        cal1Temp.setTimeInMillis(cal1.getTimeInMillis());

        Calendar cal2Temp = Calendar.getInstance();
        cal2Temp.setTimeZone(cal2.getTimeZone());
        cal2Temp.setTimeInMillis(cal2.getTimeInMillis());

        if (cal1Temp.after(cal2Temp)) {
            Calendar swap = cal1Temp;
            cal1Temp = cal2Temp;
            cal2Temp = swap;
        }

        int days = cal2Temp.get(Calendar.DAY_OF_YEAR) - cal1Temp.get(Calendar.DAY_OF_YEAR);
        int y2 = cal2Temp.get(Calendar.YEAR);

        if (cal1Temp.get(Calendar.YEAR) != y2) {
            cal1Temp = (Calendar) cal1Temp.clone();

            do {
                days += cal1Temp.getActualMaximum(Calendar.DAY_OF_YEAR);
                cal1Temp.add(Calendar.YEAR, 1);
            } while (cal1Temp.get(Calendar.YEAR) != y2);

        }

        return days;
    }

/**
 * This method was created in VisualAge.
 * @return int
 * @param d1 java.util.Date
 * @param d2 java.util.Date
 */
public static int absDifferenceInDays(java.util.Date d1, java.util.Date d2 )
{
	int count = differenceInDays(d1, d2);
	return Math.abs(count);
}

/**
 * Will "round up" the time associated with a Calendar object so
 * that the minute part of the time representation is a multiple of
 * minuteInterval. To round to the nearest hour, set minuteInterval
 * to 60. In addition, all fields less than a minute will be set to
 * zero.
 * @param date (in/out) A Calendar object representing the date to be rounded
 * @param minuteInterval An int where 0 < minuteInterval <= 60 is true
 */
public static void roundDateUp(Calendar date, int minuteInterval) {
    Validate.isTrue(minuteInterval <= 60, "minuteInterval must be less than or equal to 60");
    Validate.isTrue(minuteInterval > 0, "minuteInterval must be greater than 0");
    int minutePart = date.get(Calendar.MINUTE);
    int minutesOverInterval = minutePart % minuteInterval;
    date.add(Calendar.MINUTE, minuteInterval - minutesOverInterval);
    date.set(Calendar.SECOND, 0);
    date.set(Calendar.MILLISECOND, 0);
}

/**
 * See roundDateUp(Calendar date, int minuteInterval).
 * This is a helper method to work with a Date.
 * @param date
 * @param minuteInterval
 */
public static void roundDateUp(Date date, int minuteInterval) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    roundDateUp(calendar, minuteInterval);
    date.setTime(calendar.getTimeInMillis());
}

public static DateTime roundDateTimeUp(DateTime dateTime, int minuteInterval) {
    Validate.isTrue(minuteInterval <= 60, "minuteInterval must be less than or equal to 60");
    Validate.isTrue(minuteInterval > 0, "minuteInterval must be greater than 0");
    int minutePart = dateTime.getMinuteOfHour();
    int minutesOverInterval = minutePart % minuteInterval;
    return dateTime.plusMinutes(minuteInterval - minutesOverInterval)
                   .withSecondOfMinute(0)
                   .withMillisOfSecond(0);
}

public static Date addMinutes(Date date, int minutes) {
    return TimeUtil.addUnit(date, Calendar.MINUTE, minutes);
}

public static Date addDays(Date date, int days) {
    return TimeUtil.addUnit(date, Calendar.DAY_OF_YEAR, days);
}

public static Date addHours(Date date, int hours) {
    return TimeUtil.addUnit(date, Calendar.HOUR_OF_DAY, hours);
}

/**
 * Method to add a given amount of the time unit passed in to the date passed in.
 * (this will also work for subtraction if you pass in a negative amount)
 * @param date - Date to add to
 * @param timeUnit - Calendar time unit to increment. ex: Calendar.MINUTE
 * @param amount - Amount to add to the time
 * @return Date with updated time
 */
public static Date addUnit(Date date, int timeUnit, int amount) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    calendar.add(timeUnit, amount);
    return calendar.getTime();
}

public static int differenceMinutes(Date from, Date to) {
    long diffMillis = to.getTime() - from.getTime();
    int millisPerMinute = (60 * 1000);
    return (int) (diffMillis / millisPerMinute);
}

    /**
     * Convert seconds of time into hh:mm:ss string.
     * @param int seconds
     * @return String in format hh:mm:ss
     */
    public static String convertSecondsToTimeString(double seconds)
    {
        int iSeconds = (int)seconds;
        DecimalFormat format = new DecimalFormat();
        format.setMaximumFractionDigits(0);
        format.setMinimumIntegerDigits(2);
        DecimalFormat format2 = new DecimalFormat();
        format2.setMaximumIntegerDigits(0);
        format2.setMinimumFractionDigits(3);

        int hour = iSeconds / 3600;
        int temp = iSeconds % 3600;
        int min = temp / 60;
        int sec = temp % 60;

        return format.format(hour) + ":" + format.format(min) + ":" + format.format(Math.floor(sec))+  format2.format(seconds).toString();
    }

    /**
     * Convert seconds of time into dd days[s] hh:mm:ss string.
     * @param long seconds
     * @return String in format "dd day[s] HH:mm:ss.SSS (if 0 days, then return only "HH:mm:ss.SSS")
     * This is not i18n'd. Matches string returned from porter.
     */
    public static String convertSecondsToNormalizedStandard(double seconds) {

        double millis = seconds * 1000;
        Period period = new Period((long)millis);

        PeriodFormatter formatter = new PeriodFormatterBuilder()
        .appendDays().appendSuffix(" day ", " days ")
        .printZeroAlways()
        .minimumPrintedDigits(2).appendHours().appendSuffix(":")
        .appendMinutes().appendSuffix(":")
        .appendSecondsWithMillis()
        .toFormatter();

        return formatter.print(period.normalizedStandard(PeriodType.yearDayTime()));
    }

    /**
     * Method to get a date which represents numDays (+/-) from "now".toDateMidnight
     * Example:
     *      Now: Wed Aug 04 16:51:41 CDT 2010
     *      NumDays=1: Thu Aug 05 00:00:00 CDT 2010
     *      NumDays=0: Wed Aug 04 00:00:00 CDT 2010
     *      NumDays=-3: Sun Aug 01 00:00:00 CDT 2010
     * @param timeZone - Time zone to get midnight for
     * @return Midnight date
     */
    public static Date getMidnight(TimeZone timeZone, int numDays) {

        // Get midnight (this morning) using Joda
        DateTimeZone dateTimeZone = DateTimeZone.forTimeZone(timeZone);
        DateTime date = new DateTime(dateTimeZone);
        DateMidnight dateMidnight = date.toDateMidnight();

        DateMidnight midnightTonight = dateMidnight.plusDays(numDays);
        Date midnight = new Date(midnightTonight.getMillis());

        return midnight;
    }

    /**
     * Method to get a date which represents midnight tonight (00:00:00.000 tomorrow)
     * @param timeZone - Time zone to get midnight for
     * Example:
     *      Now: Wed Aug 04 16:51:41 CDT 2010
     *      MidnightTonight: Thu Aug 05 00:00:00 CDT 2010
     * @return Midnight date
     */
    public static Date getMidnightTonight(TimeZone timeZone) {
        return getMidnight(timeZone, 1);
    }

    /**
     * Method to get a date which represents midnight today (last night at 00:00:00.000 today)
     * @param date - Date to get midnight for
     * @param timeZone - Time zone for the date
     * @return Midnight date
     */
    public static Date getMidnight(Date date, TimeZone timeZone) {

    	// Get midnight last night using Joda
    	DateTimeZone dateTimeZone = DateTimeZone.forTimeZone(timeZone);
    	DateTime dateTime = new DateTime(date.getTime(), dateTimeZone);
    	DateMidnight dateMidnight = dateTime.toDateMidnight();
    	Date midnight = new Date(dateMidnight.getMillis());

    	return midnight;
    }

    public static List<OpenInterval> getOverlap(List<OpenInterval> intervalListOne, List<OpenInterval> intervalListTwo) {
        List<OpenInterval> resultList = Lists.newArrayList();

        for (OpenInterval intervalOne : intervalListOne) {
            for (OpenInterval intervalTwo : intervalListTwo) {
                OpenInterval overlapInterval = intervalOne.overlap(intervalTwo);

                if (overlapInterval != null) {
                    resultList.add(overlapInterval);
                }
            }
        }

        return resultList;
    }

    public static final Instant UTC_2000_EPOCH = new DateTime(2000,1,1,0,0,0,0,DateTimeZone.UTC).toInstant();

    /**
     * Takes UTC-2000 seconds and converts to an Instant.
     *
     * UTC-2000 seconds is the number of seconds from January 1, 2000 00:00:00
     *
     * @param seconds
     * @return
     */
    public static Instant convertUtc2000ToInstant(long seconds) {
        return UTC_2000_EPOCH.plus(Duration.standardSeconds(seconds));
    }

    /**
     * Returns the number of milliseconds from UTC 2000 to the instant.
     *
     * @param instant
     * @return
     */
    public static long convertInstantToUtc2000Seconds(Instant instant) {
        Interval interval = new Interval(new Instant(UTC_2000_EPOCH),instant);

        return interval.toDurationMillis();
    }

    public static Instant toMidnightAtBeginningOfDay(LocalDate date, DateTimeZone dateTimeZone) {
        return date.toDateTimeAtStartOfDay(dateTimeZone).toInstant();
    }

    public static Instant toMidnightAtEndOfDay(LocalDate date, DateTimeZone dateTimeZone) {
        return date.plusDays(1).toDateTimeAtStartOfDay(dateTimeZone).toInstant();
    }
    
    public static DateTime roundUpToNextHour(DateTime dateTime) {
        int hourOfDay = dateTime.getHourOfDay();
        if (hourOfDay == 23) {
            return dateTime.plusDays(1)
                            .withTimeAtStartOfDay();
        }
        try {
            return dateTime.withTime(hourOfDay + 1, 0, 0, 0);
        } catch (IllegalFieldValueException e) {
            //Next hour falls within daylight savings time "gap", so we have to skip an hour.
            return dateTime.withTime(hourOfDay + 2, 0, 0, 0);
        }
    }
    
    public static DateTime getStartOfHour(DateTime dateTime) {
        int hourOfDay = dateTime.getHourOfDay();
        return dateTime.withTime(hourOfDay, 0, 0, 0);
    }
    
    /**
     * @return The date that comes first, chronologically. If they represent the same date, the first object is returned.
     */
    public static DateTime getLeastRecent(DateTime time1, DateTime time2) {
        if (time1.isAfter(time2)) {
            return time2;
        }
        return time1;
    }
    
    /**
     * @return The date that comes last, chronologically. If they represent the same date, the first object is returned.
     */
    public static DateTime getMostRecent(DateTime time1, DateTime time2) {
        if (time1.isBefore(time2)) {
            return time2;
        }
        return time1;
    }
    
    /**
     * @return True if the value is at least the specified number of minutes before now.
     */
    public static boolean isXMinutesBeforeNow(int minutes, ReadableInstant value) {
        return value.isBefore(Instant.now().minus(Duration.standardMinutes(minutes)));
    }
    
    /**
     * @return True if the value is at least the specified number of seconds before now.
     */
    public static boolean isXSecondsBeforeNow(int seconds, ReadableInstant value) {
        return value.isBefore(Instant.now().minus(Duration.standardSeconds(seconds)));
    }

    /**
     * @return Cron expression for the given time and selected days.
     */
    public static String buildCronExpression(CronExprOption cronOption, int time, String days, char checkChar) throws ParseException {

        String[] parts = new String[] { "*", "*", "*", "*", "*", "*" };

        // time
        boolean isNextDay = false;
        parts[0] = "0";
        if (time == 1440) { // If time is 1440(which is 24) use 11:59PM
            time--;
        } else if (time > 1440) {
            time = time - 1440;
            isNextDay = true;
        }
        parts[1] = String.valueOf(time % 60);
        parts[2] = String.valueOf(time / 60);

        // weekly
        parts[3] = "?";
        parts[4] = "*";
        if (cronOption == CronExprOption.EVERYDAY) {
            parts[5] = "*";
        } else {
            List<String> selectedDays = new ArrayList<>();

            for (int i = 0; i < days.length(); i++) {
                if (days.charAt(i) == checkChar) {
                    int dayIndex = i;
                    // Check if the time exceeds 1440 (possible only in case of 48 hours slider)
                    if (isNextDay) {
                        dayIndex = i + 1;
                    }
                    // dayIndex >= days.length() , this condition will only come when Saturday is 
                    // selected and stopTime will cross Saturday (possible only in case of 48 hours slider)
                    if (dayIndex >= days.length() & isNextDay) {
                        // If above condition is true i.e stopTime for Saturday will reach Sunday.
                        // Set dayIndex = 0 i.e Sunday
                        dayIndex = 0;
                    }
                    selectedDays.add(String.valueOf(dayIndex + 1));
                }
            }

            if (selectedDays.size() > 0) {
                parts[5] = StringUtils.join(selectedDays, ",");
            }
        }

        String cronExpression = StringUtils.join(parts, " ").trim();
        try {
            new CronExpression(cronExpression);
        } catch (ParseException e) {
            throw e;
        }
        return cronExpression;
    }

    /**
     * @return Next runtime from the given date as per the Cron expression provided.
     * @throws ParseException 
     */
    public static Date getNextRuntime(Date from, String cron, TimeZone timeZone) throws ParseException {
        Date nextValidTimeAfter = null;
        try {
            CronExpression cronExpression = new CronExpression(cron);
            cronExpression.setTimeZone(timeZone);
            nextValidTimeAfter = cronExpression.getNextValidTimeAfter(from);
        } catch (ParseException e) {
            throw e;
        } catch (UnsupportedOperationException e) {
            throw e;
        }
        return nextValidTimeAfter;
    }

    /**
      * @return True , if first date is equal or coming after second date. 
    */
    public static boolean isDateEqualOrAfter(DateTime d1, DateTime d2) {
        if (d1.isAfter(d2) || d1.isEqual(d2)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * @param totalHours
     * @return : Hours remaining after converting total hours to days.
     */
    public static long hoursRemainingAfterConveritngToDays(Long totalHours) {
        if (totalHours > 0) {
            return totalHours % 24;
        } else
            return 0;
    }
}
