package com.cannontech.common.util;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.Validate;

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
        cal1Temp.setTimeInMillis(cal1.getTimeInMillis());
        
        Calendar cal2Temp = Calendar.getInstance();
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
