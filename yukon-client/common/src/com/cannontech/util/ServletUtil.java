package com.cannontech.util;

import java.awt.Color;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.StringTokenizer;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;

/**
 * The junk drawer for servlets.
 * Added static Attribute values from stars for reference in all web applications. (04/15/2004 SN)
 * Creation date: (3/28/00 11:19:23 AM)
 * @author: 
 */

public class ServletUtil {
	
	//Session attributes.
	public static final String ATT_ERROR_MESSAGE = "ERROR_MESSAGE";
	public static final String ATT_CONFIRM_MESSAGE = "CONFIRM_MESSAGE";
	public static final String ATT_YUKON_USER = "YUKON_USER";
	
	public static final String ATT_REDIRECT = "REDIRECT";
	public static final String ATT_REDIRECT2 = "REDIRECT2";
	public static final String ATT_REFERRER = "REFERRER";
	public static final String ATT_REFERRER2 = "REFERRER2";


	public static final String ATT_GRAPH_BEAN = "GRAPH_BEAN";	
	
	// Valid periods
	public static final String ONEDAY = "1 Day";
	public static final String THREEDAYS = "3 Days";
	public static final String FIVEDAYS = "5 Days";
	public static final String ONEWEEK = "1 Week";
	public static final String ONEMONTH = "1 Month";	
	public static final String FOURWEEKS = "4 Weeks";
	public static final String FIVEWEEKS = "5 Weeks";

	public static final String TODAY = "Today";
	public static final String YESTERDAY = "Yesterday";
	public static final String PREVTWODAYS= "Prev 2 Days";
	public static final String PREVTHREEDAYS= "Prev 3 Days";
	public static final String PREVFIVEDAYS = "Prev 5 Days";
	public static final String PREVSEVENDAYS= "Prev 7 Days";
	public static final String PREVONEWEEK= "Prev 1 Week";

	public static String[] validPeriods =
	{
		ONEDAY,
		THREEDAYS,
		FIVEDAYS,
		ONEWEEK,
		ONEMONTH,
		FOURWEEKS,
		FIVEWEEKS,
		TODAY,
		PREVTWODAYS,
		PREVTHREEDAYS,
		PREVFIVEDAYS,
		PREVSEVENDAYS,
		PREVONEWEEK
		
	};
/*
	// The int representation of these values are the indexes used in the time Period drop down box.
	//  The values in the drop down box MUST match up to these final ints.
	public static final int ONE_DAY = 1;
	public static final int THREE_DAYS = 3;
	public static final int ONE_WEEK = 7;
	public static final int FOUR_WEEKS = 28;
//	public static final int ONE_MONTH = 28, 29, 30, 31;

	// the following are all covered with "NO_WEEK" status
	public static final int TODAY = 1;
	public static final int PREV_TWO_DAYS = 2;
	public static final int PREV_THREE_DAYS = 3;
	public static final int PREV_SEVEN_DAYS = 7;
	// new time periods need to be added here in the appropriate order of represention
	// of the time period's index in the drop down box.
*/
	// if periods is modified, final ints representing the periods index need to be updated also, in Graph class
	public static String[] historicalPeriods = 
	{
		ONEDAY,
		THREEDAYS,
		ONEWEEK,
		FOURWEEKS,
		FIVEWEEKS,
		ONEMONTH
	};

	public static String[] currentPeriods =
	{
		TODAY,
		PREVTWODAYS,
		PREVTHREEDAYS,
		//PREVFIVEDAYS,
		PREVSEVENDAYS,
	};

		
	// Date/Time pattern strings that will be tried when
	// attempting to interprate starting dates
	private static final java.text.SimpleDateFormat[] dateFormat =
	{
		new java.text.SimpleDateFormat("MM:dd:yyyy:HH:mm:ss"),
		new java.text.SimpleDateFormat("MM/dd/yy"),
		new java.text.SimpleDateFormat("MM-dd-yy"),
		new java.text.SimpleDateFormat("MM.dd.yy"),
		new java.text.SimpleDateFormat("MM/dd/yyyy"),
		new java.text.SimpleDateFormat("MM-dd-yyyy"),
		new java.text.SimpleDateFormat("MM.dd.yyyy"),
		new java.text.SimpleDateFormat("HH:mm:ss"),
		new java.text.SimpleDateFormat("HH:mm")		
	};

	//Ever seen this before? hehe
	//this static initializer sets all the simpledateformat to lenient
	static
	{
		for( int i = 0; i < dateFormat.length; i++ )
			dateFormat[i].setLenient(true);
	}
	
	// Values of the "format" property of the <cti:getProperty> tag
	public static final String FORMAT_UPPER = "upper";
	public static final String FORMAT_LOWER = "lower";
	public static final String FORMAT_CAPITAL = "capital";
	public static final String FORMAT_ALL_CAPITAL = "all_capital";
	
/**
 * Creation date: (6/7/2001 3:09:18 PM)
 * @return java.lang.Object[][]
 * @param dbAlias java.lang.String
 * @param query java.lang.String
 */
public static Object[][] executeSQL(String dbAlias, String query) {
	
	java.sql.Connection connection = null;
	java.sql.Statement statement = null;
	java.sql.ResultSet resultSet = null;
	java.sql.ResultSetMetaData metaData = null;
	Object[][] data = null;
	
	try
	{
		connection = com.cannontech.database.PoolManager.getInstance().getConnection( dbAlias );
		statement = connection.createStatement();
		resultSet = statement.executeQuery( query );
		metaData = resultSet.getMetaData();
		java.util.Vector rows = new java.util.Vector();
		int columnCount = 0;		
		columnCount = resultSet.getMetaData().getColumnCount();
		
		while( resultSet.next() )
		{
			java.util.Vector rowData = new java.util.Vector();
			boolean nonNullRow = false;
						
			for( int i = 1; i <= columnCount; i++ )
			{
				Object o = resultSet.getObject(i);

				if( o != null )
					nonNullRow = true; // at least 1 value in the row is not null
						
				rowData.addElement( o );							
			}
											
			if( rowData.size() > 0 && nonNullRow )
				rows.addElement( rowData );
		}
		

		data = new Object[ rows.size() ][columnCount];
		for( int i = 0; i < rows.size(); i++ )
		{
			java.util.Vector temp = (java.util.Vector) rows.elementAt(i);
			data[i] = temp.toArray();//temp.copyInto( data[i] );
		}
	}
	catch( java.sql.SQLException e )
	{
		com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
	}
	finally
	{
		try
		{
			if( resultSet != null )
				resultSet.close();
		}
		catch( java.sql.SQLException e )
		{}

		try
		{
			if( statement != null )
				statement.close();
		}
		catch( java.sql.SQLException e )
		{}

		try
		{
			if( connection != null )
				connection.close();
		}
		catch( java.sql.SQLException e )
		{
			// didnt work
		}
	}
	
	return data;
}
/**
 * Creation date: (6/7/2001 3:09:18 PM)
 * @return java.lang.Object[][]
 * @param dbAlias java.lang.String
 * @param query java.lang.String
 */
public static Object[][] executeSQL(String dbAlias, String query, Class[] types) {
	java.sql.Connection connection = null;
	java.sql.Statement statement = null;
	java.sql.ResultSet resultSet = null;
	java.sql.ResultSetMetaData metaData = null;
	Object[][] data = null;
	
	try
	{
		connection = com.cannontech.database.PoolManager.getInstance().getConnection( dbAlias );
		statement = connection.createStatement();
		resultSet = statement.executeQuery( query );
		metaData = resultSet.getMetaData();
		java.util.Vector rows = new java.util.Vector();
		int columnCount = 0;		
		columnCount = resultSet.getMetaData().getColumnCount();
		
		while( resultSet.next() )
		{
			java.util.Vector rowData = new java.util.Vector();
			boolean nonNullRow = false;
						
			for( int i = 1; i <= columnCount; i++ )
			{
				Class thisColumn = types[i-1];
				Object o;
				
				if( thisColumn == Integer.class )
				{
					o = new Integer( resultSet.getInt(i) );
				}
				else
				if( thisColumn == Double.class )
				{
					o = new Double( resultSet.getDouble(i) );
				}
				else
				if( thisColumn == Float.class )
				{
					o = new Float( resultSet.getFloat(i) );
				}
				else
				if( thisColumn == java.util.Date.class )
				{
					java.sql.Timestamp t = resultSet.getTimestamp(i);

					if( t != null )					
						o = new java.util.Date( t.getTime() );
					else
						o = null;
					
				}
				else
				if( thisColumn == String.class )
				{
					o = resultSet.getString(i);
				}
				else
				{
					o = resultSet.getObject(i);
				}

				if( o != null )
					nonNullRow = true; // at least 1 value in the row is not null
						
				rowData.addElement( o );							
			}
											
			if( rowData.size() > 0 && nonNullRow )
				rows.addElement( rowData );
		}
		

		data = new Object[ rows.size() ][columnCount];
		for( int i = 0; i < rows.size(); i++ )
		{
			java.util.Vector temp = (java.util.Vector) rows.elementAt(i);
			data[i] = temp.toArray();//temp.copyInto( data[i] );
		}
	}
	catch( java.sql.SQLException e )
	{
		com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
	}
	finally
	{
		try
		{
			if( resultSet != null )
				resultSet.close();
		}
		catch( java.sql.SQLException e )
		{}

		try
		{
			if( statement != null )
				statement.close();
		}
		catch( java.sql.SQLException e )
		{}

		try
		{
			if( connection != null )
				connection.close();
		}
		catch( java.sql.SQLException e )
		{
			// didnt work
		}
	}
	
	return data;
}
/**
 * Insert the method's description here.
 * Creation date: (5/1/00 10:57:30 AM)
 * Version: <version>
 * @return java.lang.Object[][]
 * @param session javax.servlet.http.HttpSession
 */
public static Object[][] executeSQL(javax.servlet.http.HttpSession session, String query ) 
{
	if( session == null )
		return null;
			
	java.sql.Connection connection = null;
	java.sql.Statement statement = null;
	java.sql.ResultSet resultSet = null;
	java.sql.ResultSetMetaData metaData = null;
	Object[][] data = null;
	
	try
	{
		connection = com.cannontech.database.PoolManager.getInstance().getConnection("yukon");
		statement = connection.createStatement();
		resultSet = statement.executeQuery( query );
		metaData = resultSet.getMetaData();
		java.util.Vector rows = new java.util.Vector();
		int columnCount = 0;		
		columnCount = resultSet.getMetaData().getColumnCount();
		
		while( resultSet.next() )
		{
			java.util.Vector rowData = new java.util.Vector();
			boolean nonNullRow = false;
						
			for( int i = 1; i <= columnCount; i++ )
			{
				Object o = resultSet.getObject(i);

				if( o != null )
					nonNullRow = true; // at least 1 value in the row is not null
						
				rowData.addElement( o );							
			}
											
			if( rowData.size() > 0 && nonNullRow )
				rows.addElement( rowData );
		}
		

		data = new Object[ rows.size() ][columnCount];
		for( int i = 0; i < rows.size(); i++ )
		{
			java.util.Vector temp = (java.util.Vector) rows.elementAt(i);
			data[i] = temp.toArray();//temp.copyInto( data[i] );
		}
	}
	catch( java.sql.SQLException e )
	{
		com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
	}
	finally
	{
		try
		{
			if( resultSet != null )
				resultSet.close();
		}
		catch( java.sql.SQLException e )
		{}

		try
		{
			if( statement != null )
				statement.close();
		}
		catch( java.sql.SQLException e )
		{}

		try
		{
			if( connection != null )
				connection.close();
		}
		catch( java.sql.SQLException e )
		{
			// didnt work
		}
	}
	
	return data;
}
/**
 * Insert the method's description here.
 * Creation date: (5/1/00 10:57:30 AM)
 * Version: <version>
 * @return java.lang.Object[][]
 * @param session javax.servlet.http.HttpSession
 */
public static Object[][] executeSQL(javax.servlet.http.HttpSession session, String query, Class[] types ) 
{
	if( session == null )
		return null;
	
	java.sql.Connection connection = null;
	java.sql.Statement statement = null;
	java.sql.ResultSet resultSet = null;
	java.sql.ResultSetMetaData metaData = null;
	Object[][] data = null;
	
	try
	{
		connection = com.cannontech.database.PoolManager.getInstance().getConnection("yukon");
		statement = connection.createStatement();
		resultSet = statement.executeQuery( query );
		metaData = resultSet.getMetaData();
		java.util.Vector rows = new java.util.Vector();
		int columnCount = 0;		
		columnCount = resultSet.getMetaData().getColumnCount();
		
		while( resultSet.next() )
		{
			java.util.Vector rowData = new java.util.Vector();
			boolean nonNullRow = false;
						
			for( int i = 1; i <= columnCount; i++ )
			{
				Class thisColumn = types[i-1];
				Object o;
				
				if( thisColumn == Integer.class )
				{
					o = new Integer( resultSet.getInt(i) );
				}
				else
				if( thisColumn == Double.class )
				{
					o = new Double( resultSet.getDouble(i) );
				}
				else
				if( thisColumn == Float.class )
				{
					o = new Float( resultSet.getFloat(i) );
				}
				else
				if( thisColumn == java.util.Date.class )
				{
					java.sql.Timestamp t = resultSet.getTimestamp(i);

					if( t != null )					
						o = new java.util.Date( t.getTime() );
					else
						o = null;
					
				}
				else
				if( thisColumn == String.class )
				{
					o = resultSet.getString(i);
				}
				else
				{
					o = resultSet.getObject(i);
				}

				if( o != null )
					nonNullRow = true; // at least 1 value in the row is not null
						
				rowData.addElement( o );							
			}
											
			if( rowData.size() > 0 && nonNullRow )
				rows.addElement( rowData );
		}
		

		data = new Object[ rows.size() ][columnCount];
		for( int i = 0; i < rows.size(); i++ )
		{
			java.util.Vector temp = (java.util.Vector) rows.elementAt(i);
			data[i] = temp.toArray();//temp.copyInto( data[i] );
		}
	}
	catch( java.sql.SQLException e )
	{
		com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
	}
	finally
	{
		try
		{
			if( resultSet != null )
				resultSet.close();
		}
		catch( java.sql.SQLException e )
		{}

		try
		{
			if( statement != null )
				statement.close();
		}
		catch( java.sql.SQLException e )
		{}

		try
		{
			if( connection != null )
				connection.close();
		}
		catch( java.sql.SQLException e )
		{
			// didnt work
		}
	}
	
	return data;
}
/**
 * Insert the method's description here.
 * Creation date: (3/28/00 4:28:13 PM)
 * @return java.lang.String
 * @param d java.util.Date
 */
public static String formatDateString(java.util.Date d) {

	return dateFormat[0].format(d);
}
/**
 * Return a date a given number of days from today.
 * Can be + or - offset from today.
 * -1 would be yesterday and +1 would be tomorrow
 * Creation date: (3/28/00 4:15:54 PM)
 * @return java.util.Date
 */
public static java.util.Date getDate(int dayOffset) {

	java.util.GregorianCalendar cal = new java.util.GregorianCalendar();
	cal.setTime(new java.util.Date());
	
	cal.set( java.util.Calendar.DAY_OF_YEAR, cal.get( java.util.Calendar.DAY_OF_YEAR ) + dayOffset ); 
 	cal.set( java.util.Calendar.HOUR_OF_DAY, 0 );
	cal.set( java.util.Calendar.MINUTE, 0 );
	cal.set( java.util.Calendar.SECOND, 0 );

	return cal.getTime();
}
/**
 * Insert the method's description here.
 * Creation date: (3/28/00 4:03:38 PM)
 * @return java.util.Date
 * @param startingDate java.util.Date
 * @param period java.lang.String
 */
public static java.util.Date getEndingDateOfInterval(java.util.Date startingDate, String period) {
	
	int numDays;
	period = period.trim();
		
	if( period.equalsIgnoreCase(ONEDAY) 		||
		period.equalsIgnoreCase(TODAY)			||		
		period.equalsIgnoreCase(PREVTWODAYS) 	||
		period.equalsIgnoreCase(PREVTHREEDAYS) 	||
		period.equalsIgnoreCase(PREVFIVEDAYS) 	||
		period.equalsIgnoreCase(PREVSEVENDAYS) 	||
		period.equalsIgnoreCase(PREVONEWEEK) )		
	{
		numDays = 1;	
	}
	else
	if( period.equalsIgnoreCase(YESTERDAY) )
	{
		numDays = 0;
	}
	else
	if( period.equalsIgnoreCase(THREEDAYS) )
	{
		numDays = 3;
	}
	else
	if( period.equalsIgnoreCase(FIVEDAYS) )
	{
		numDays = 5;
	}
	else
	if( period.equalsIgnoreCase(ONEWEEK) )
	{
		numDays = 7;
	}
	else
	if( period.equalsIgnoreCase(FOURWEEKS) )
	{
		numDays = 28;
	}
	else
	if( period.equalsIgnoreCase(FIVEWEEKS) )
	{
		numDays = 35;
	}
	else
	if( period.equalsIgnoreCase(ONEMONTH) )
	{
		java.util.GregorianCalendar c = new java.util.GregorianCalendar();
		c.setTime(startingDate);
		c.set( java.util.Calendar.MONTH, c.get( java.util.Calendar.MONTH) + 1 );

		java.util.Date endOfInterval = c.getTime();

		numDays = com.cannontech.common.util.TimeUtil.differenceInDays(startingDate, endOfInterval);		
	}
	else
		return null;
		
	java.util.GregorianCalendar cal = new java.util.GregorianCalendar();
	cal.setTime(startingDate);
	cal.set( java.util.Calendar.DAY_OF_YEAR, cal.get( java.util.Calendar.DAY_OF_YEAR ) + numDays );

	return cal.getTime();
}


/**
 * Returns the HTML string definition for a Java Color object.
 * @return String
 * @param Color
 */
public static synchronized String getHTMLColor( Color c )
{
	String r = Integer.toHexString(c.getRed());
	String g = Integer.toHexString(c.getGreen());
	String b = Integer.toHexString(c.getBlue());

	return
			(r.length() <= 1 ? "0"+r : r) + 
			(g.length() <= 1 ? "0"+g : g) +
			(b.length() <= 1 ? "0"+b : b);
}


/**
 * Insert the method's description here.
 * Creation date: (7/11/2001 12:56:06 PM)
 * @return int
 * @param StringValue java.lang.String
 */
public static int getIntValue(String stringValue)
{
	if( stringValue.equalsIgnoreCase(ONEDAY) )
		return 1;
	else
	if( stringValue.equalsIgnoreCase(THREEDAYS) )
		return 3;
	else
	if( stringValue.equalsIgnoreCase(FIVEDAYS) )
		return 5;
	else
	if( stringValue.equalsIgnoreCase(ONEWEEK) )
		return 7;
	else
	if( stringValue.equalsIgnoreCase(ONEMONTH) )
	{
		java.util.GregorianCalendar c = new java.util.GregorianCalendar();
		c.setTime(getToday());
		c.set( java.util.Calendar.MONTH, c.get( java.util.Calendar.MONTH) + 1 );
		java.util.Date endOfInterval = c.getTime();
		return com.cannontech.common.util.TimeUtil.differenceInDays(getToday(), endOfInterval);		
	}
	else
	if( stringValue.equalsIgnoreCase(FOURWEEKS) )
		return 28;
	else
	if( stringValue.equalsIgnoreCase(FIVEWEEKS) )
		return 35;	
	else
	if( stringValue.equalsIgnoreCase(TODAY) )
		return 0;
	else
	if( stringValue.equalsIgnoreCase(PREVTWODAYS) )
		return -1;			
	else
	if( stringValue.equalsIgnoreCase(PREVTHREEDAYS) )
		return -2;
	else
	if( stringValue.equalsIgnoreCase(PREVFIVEDAYS) )
		return -4;
	else 
	if( stringValue.equalsIgnoreCase(PREVSEVENDAYS) )
		return -6;
	else
		return 1;	//default...for lack of better int values.
}
/**
 * Insert the method's description here.
 * Creation date: (3/28/00 11:20:36 AM)
 * @return java.lang.String
 * @param start java.util.Date
 * @param end java.util.Date
 */
public static String getPeriodFromDates(java.util.Date start, java.util.Date end) {

	long startTime = start.getTime();
	long endTime = end.getTime();

	int numDays = (int) Math.round(((double) (endTime-startTime)) / (double) 86400000 );

	//figure out the closest/most reasonable period we have to numDays
	if( numDays <= -6 )
		return PREVSEVENDAYS;
	else
	if( numDays <= -4)
		return PREVFIVEDAYS;
	else
	if( numDays <= -2)
		return PREVTHREEDAYS;
	else
	if( numDays <= -1)
		return PREVTWODAYS;
	else
	if( numDays <= 1 )
		return ONEDAY;	//or return TODAY;
	else
	if( numDays <= 3 )
		return THREEDAYS;
	else
	if( numDays <= 5 )
		return FIVEDAYS;
	else
	if( numDays <= 7 )
		return ONEWEEK;
	else
	if( numDays <=28 )
		return FOURWEEKS;
	else
	if( numDays <= 35 )
		return FIVEWEEKS;
	else
		return ONEDAY;
}
/**
 * Insert the method's description here.
 * Creation date: (3/28/00 4:03:38 PM)
 * @return java.util.Date
 * @param startingDate java.util.Date
 * @param period java.lang.String
 */
public static java.util.Date getStartingDateOfInterval(java.util.Date startingDate, String period) {
	
	int numDays;
	period = period.trim();
	
	if( period.equalsIgnoreCase(TODAY) )
	{
		return startingDate;
	}
	else
	if( period.equalsIgnoreCase(PREVTWODAYS) ||
		period.equalsIgnoreCase(YESTERDAY) )
	{
		numDays = -1;
	}
	else
	if( period.equalsIgnoreCase(PREVTHREEDAYS) )
	{
		numDays = -2;	//we want the previous three days including today so we really only need 2 previous ones.
	}
	else
	if( period.equalsIgnoreCase(PREVFIVEDAYS) )
	{
		numDays = -4;
	}
	else
	if( period.equalsIgnoreCase(PREVSEVENDAYS)  || period.equalsIgnoreCase(PREVONEWEEK))
	{
		numDays = -6;
	}

	else	//Don't change the starting date
		return startingDate;
		
	java.util.GregorianCalendar cal = new java.util.GregorianCalendar();
	cal.setTime(startingDate);
	cal.set( java.util.Calendar.DAY_OF_YEAR, cal.get( java.util.Calendar.DAY_OF_YEAR ) + numDays );

	return cal.getTime();
}
/**
 * Returns a Date that represents the beginning of today.
 * Creation date: (3/28/00 4:15:54 PM)
 * @return java.util.Date
 */
public static java.util.Date getToday() {
	return getToday(TimeZone.getDefault());
}

/**
 * Returns a Date that represents the beginning of today in the given TimeZone
 * @param tz
 * @return
 */
public static Date getToday(TimeZone tz) {
	GregorianCalendar cal = new GregorianCalendar();
	cal.setTimeZone(tz);

	cal.set(Calendar.HOUR_OF_DAY, 0);
	cal.set(Calendar.MINUTE, 0);
	cal.set(Calendar.SECOND, 0);
	return cal.getTime();
}
/**
 * Returns a Date that represents the beginning of tomorrow.
 * Creation date: (3/28/00 4:15:54 PM)
 * @return java.util.Date
 */
public static java.util.Date getTomorrow() {
	return getTomorrow(TimeZone.getDefault());
	
}

/**
 * Returns a Date that represents the beginning of tomorrow in the given TimeZone.
 * @param tz
 * @return
 */
public static Date getTomorrow(TimeZone tz) {
	GregorianCalendar cal = new GregorianCalendar();
	cal.setTimeZone(tz);
	cal.setTime(new Date());
	
	cal.set( Calendar.DAY_OF_YEAR, cal.get( Calendar.DAY_OF_YEAR ) + 1 );
	cal.set( Calendar.HOUR_OF_DAY, 0 );
	cal.set( Calendar.MINUTE, 0 );
	cal.set( Calendar.SECOND, 0 );
	return cal.getTime();
}

/**
 * Returns a Date that represents the beginning of yesterday.
 * Creation date: (3/28/00 4:22:52 PM)
 * @return java.util.Date
 */
public static java.util.Date getYesterday() {
	return getYesterday(TimeZone.getDefault());
}

/**
 * Returns a Date that represents the beginning of yesterday in the given TimeZone.
 * @param tz
 * @return
 */
public static Date getYesterday(TimeZone tz) {
	java.util.GregorianCalendar cal = new java.util.GregorianCalendar();
	cal.setTimeZone(tz);
	cal.setTime(new Date());
	
	cal.set( Calendar.DAY_OF_YEAR, cal.get( Calendar.DAY_OF_YEAR ) - 1 ); 
	cal.set( Calendar.HOUR_OF_DAY, 0 );
	cal.set( Calendar.MINUTE, 0 );
	cal.set( Calendar.SECOND, 0 );

	return cal.getTime();
}
/**
 * Create a Date object using the given String and TimeZone
 * Creation date: (3/28/00 11:28:32 AM)
 * @return java.util.Date
 * @param str java.lang.String
 */
public static synchronized java.util.Date parseDateStringLiberally(String dateStr) {
	return parseDateStringLiberally(dateStr, TimeZone.getDefault());
}

/**
 * Method parseDateStringLiberally.
 * Create a Date object using the given String and TimeZone
 * @param dateStr
 * @param tz
 * @return Date
 */
public static synchronized Date parseDateStringLiberally(String dateStr, TimeZone tz) {
	java.util.Date retVal = null;
	
	for( int i = 0; i < dateFormat.length; i++ )
	{
		try
		{
			DateFormat df = dateFormat[i];
			df.setTimeZone(tz);
			retVal = df.parse(dateStr);
			break;
		}
		catch( java.text.ParseException pe )
		{
		}
	}

	return retVal;	
}
/**
 * Insert the method's description here.
 * Creation date: (7/10/00 4:08:59 PM)
 * @return java.util.Date
 * @param toRound java.util.Date
 */
public static java.util.Date roundToMinute(java.util.Date toRound) {
	java.util.GregorianCalendar cal = new java.util.GregorianCalendar();
	cal.setTime(toRound);

	if( cal.get( java.util.Calendar.SECOND ) < 30 )
	{
		cal.set( java.util.Calendar.SECOND, 0 );
	}
	else
	{
		cal.set( java.util.Calendar.SECOND, 60 );
	}

	return cal.getTime();
}

	/**
	 * Used to return NULL for a param if it is not found OR if it is not set
	 * @param req_
	 * @param name_
	 * @return
	 */
	public synchronized static String getParm( HttpServletRequest req_, String name_ )
	{
		String s = req_.getParameter(name_);
			
		return 
			( s == null 
			? null : 
				(s.length() <= 0 ? null : s) );		
	}

    /**
     * Convert a string into the capitalized format.
     * @return String
     * @param word String
     */
    public static String capitalize(String word) {
    	return word.substring(0,1).toUpperCase().concat( word.substring(1).toLowerCase() );
    }
    
    /**
     * Capitalize every word in a phrase.
     * @return String
     * @param phrase String
     */
    public static String capitalizeAll(String phrase) {
    	StringTokenizer st = new StringTokenizer( phrase, " ", true );
    	StringBuffer sb = new StringBuffer();
    	while (st.hasMoreTokens()) {
    		String word = st.nextToken();
    		if (word.equals(" "))
    			sb.append( word );
    		else
    			sb.append( capitalize(word) );
    	}
    	
    	return sb.toString();
    }

}
