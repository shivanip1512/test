package com.cannontech.common.util;

import java.util.Calendar;

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
}
