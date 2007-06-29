package com.cannontech.common.util;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang.time.DateUtils;

/**
 * This type really needs to be looked at before it is used
 */
public class TimeUtil 
{
	private static java.util.GregorianCalendar c1 = new java.util.GregorianCalendar();
	private static java.util.GregorianCalendar c2 = new java.util.GregorianCalendar();
/**
 * This method was created in VisualAge.
 * @return boolean
 * @param d1 java.util.Date
 * @param d2 java.util.Date
 */
public static boolean compareDate( java.util.Date d1, java.util.Date d2 ) 
{
	c1.setTime( d1 );
	c2.setTime( d2 );

	//Compares dates to the date only - i.e. 12/27/98 at 13:41 == 12/27/98 at 10:33
	return( c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR)
			  && c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH)
			  && c1.get(Calendar.DATE) == c2.get(Calendar.DATE) );
	
/*	return( d1.getYear()	== 	d2.getYear() 	&&
			d1.getMonth() 	== 	d2.getMonth() 	&&
			d1.getDate() 	== 	d2.getDate()		);
*/

}
/**
 * This method was created in VisualAge.
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
	/*
	java.util.GregorianCalendar calTemp = new java.util.GregorianCalendar();
	calTemp.setTime( new java.util.Date(d1.getTime()) );
	
	//abs(d2 - d1) wow nice efficiency - ouch
	if( c1.get(c1.DATE) >= c2.get(c2.DATE) )
	{
		java.util.GregorianCalendar temp = c1;
		c1 = c2;
		c2 = temp;
	}	

	int count = 0;	
	while( compareDate( c1.getTime(), c2.getTime() ) == false )
	{
		c1.set( c1.DATE, c1.get(c1.DATE) + 1 );
		//d1.setDate( d1.getDate() + 1 );
		count++;
	}
*/
	return count;
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
 * This method checks for a valid date string of the format mm/dd/yy.
 * @return boolean
 * @param dateString java.lang.String
 */
public static boolean isValidDateString(String dateString) {

	
	try
	{
		//Just an attempt to see if an exception is thrown if the input is crazy
		// - the exact format here doesn't seem to matter
		java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("M/dd/yy");
		java.util.Date parsedDate = formatter.parse( dateString );

		//so far so good - lets weed out the obvious errors
		//if the MM/dd/yy they give us back is different than the one
		//we gave it then we know its wrong!
		String newDateString = formatter.format(parsedDate);
		com.cannontech.clientutils.CTILogger.info(newDateString);
		
		String patterns[] = { "MM/dd/yy", "M/dd/yy", 
							  "MM/d/yy", "M/d/yy",
							  "MM/d/yyyy", "M/d/yyyy",
							  "MM/dd/yyyy", "M/dd/yyyy" };

		//search for a pattern
		for( int i = 0; i < patterns.length; i++ )
		{
			formatter.applyPattern( patterns[i] );
			if( formatter.format(parsedDate).equals(dateString) )
				return true;
		}

		//no match
		return false;
		
		
	}
	catch(Exception e)
	{
		com.cannontech.clientutils.CTILogger.info(e.getMessage());
		return false;
	}

	
		
}
/**
 * This method was created in VisualAge.
 * @return java.util.Date
 * @param mSecs long
 */
public static java.util.Date roundTime(long mSecs) 
{
	//java.util.Date coarseDate = new java.util.Date(mSecs);
	c1.setTime( new java.util.Date(mSecs) );

	if( c1.get(Calendar.HOUR) >= 12 )
		c1.add( Calendar.DATE, 1 );
		//coarseDate.setDate( coarseDate.getDate() + 1 );  //round the date up

	//java.util.Date smoothDate = new java.util.Date( 
		//coarseDate.getYear(), 
		//coarseDate.getMonth(), 
		//coarseDate.getDate() );

	//return smoothDate;
	return c1.getTime();
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 * @param d java.util.Date
 */
public static String toSimpleDateString(java.util.Date d) {
	
	java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("MM/dd/yy");
	String retStr;
	try
	{
	 retStr = formatter.format(d);
	}
	catch(Exception e )
	{
		com.cannontech.clientutils.CTILogger.info(e.getMessage());
		return new String();
	}
	
	return retStr;
	
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

public static Date addMinutes(Date date, int minutes) {
    return TimeUtil.addUnit(date, Calendar.MINUTE, minutes);
}

public static Date addDays(Date date, int days) {
    return TimeUtil.addUnit(date, Calendar.DAY_OF_YEAR, days);
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

    static DateFormatSymbols apSymbols;
    static {
        apSymbols = new DateFormatSymbols();
        apSymbols.setAmPmStrings(new String[] {"A","P"});
    }
    private static DateFormat dateTimeFormat[] = {
        new SimpleDateFormat("MM/dd/yy hh:mma", apSymbols),
        new SimpleDateFormat("MM/dd/yyyy hh:mma", apSymbols),
        new SimpleDateFormat("MM/dd/yy hh:mma"),
        new SimpleDateFormat("MM/dd/yyyy hh:mma"),
        new SimpleDateFormat("MM/dd/yy hh:mm a", apSymbols),
        new SimpleDateFormat("MM/dd/yyyy hh:mm a", apSymbols),
        new SimpleDateFormat("MM/dd/yy hh:mm a"),
        new SimpleDateFormat("MM/dd/yyyy hh:mm a"),
        new SimpleDateFormat("MM/dd/yy HH:mm"),
        new SimpleDateFormat("MM/dd/yyyy HH:mm"),
    };
    
    private static DateFormat dateFormat[] = {
        new SimpleDateFormat("MM/dd/yy"),
        new SimpleDateFormat("MM/dd/yyyy"),
    };
    
    static {
        for (DateFormat format : dateTimeFormat) {
            format.setLenient(false);
        }
        for (DateFormat format : dateFormat) {
            format.setLenient(false);
        }
    }
    
    public static enum NO_TIME_MODE {START_OF_DAY,END_OF_DAY};

    /**
     * Parses a String into a Date. This code tries several build in date formats
     * to try and parse the the string into a date. In general, the dateStr must
     * have a pattern similar to "date [time]" where date is in "m/d/y" form 
     * and time is is in "h:m" form with an optional am/pm designator.
     * 
     * The following inputs are all valid:
     *   12/12/2007 5:15 AM
     *   12/12/2007 5:15 A
     *   12/12/2007 5:15 am
     *   12/12/2007 5:15a
     *   12/12/2007 5:15AM
     *   12/12/2007 5:15 (assumes AM)
     *   12/12/2007 15:15 (assumes 24-hour clock)
     *   12/12/07 15:15p
     *   12/12/07
     *   
     * The mode argument can be used to control what time is associated with a 
     * date for which no time was entered. If START_OF_DAY is specified, the date 
     * will correspond to midnight on the morning of the specified date. If 
     * END_OF_DAY is specified, the date will correspond to midnight on the morning 
     * after the specified date. The mode makes no difference when the time is included
     * in the dateStr.
     * 
     * If the date does not match any of the built-in formats, a ParseException
     * is thrown.
     *   
     * @param dateStr the string to parse
     * @param mode controls how dates without times are treated
     * @param timeZone Time zone to use to parse the date
     * @return
     * @throws ParseException if the dateStr cannot be parsed into a Java date
     */
    public synchronized static Date flexibleDateParser(String dateStr, NO_TIME_MODE mode, TimeZone timeZone) 
        throws ParseException {
        if (StringUtils.isBlank(dateStr)) return null;
        for (DateFormat format : dateTimeFormat) {
            try {
                format.setTimeZone(timeZone);
                Date date = format.parse(dateStr);
                return date;
            } catch (ParseException e) {
            }
        }
        for (DateFormat format : dateFormat) {
            try {
                format.setTimeZone(timeZone);
                Date date = format.parse(dateStr);
                if (mode == NO_TIME_MODE.END_OF_DAY) {
                    return DateUtils.addDays(date, 1);
                }
                return date;
            } catch (ParseException e) {
            }
        }
        throw new ParseException(dateStr, 0);
    }
    
    /**
     * Calls flexibleDateParser(dateStr, NO_TIME_MODE.START_OF_DAY)
     * @param dateStr
     * @return
     * @throws ParseException
     */
    public static Date flexibleDateParser(String dateStr, TimeZone timeZone) throws ParseException {
        return flexibleDateParser(dateStr, NO_TIME_MODE.START_OF_DAY, timeZone);
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
}
