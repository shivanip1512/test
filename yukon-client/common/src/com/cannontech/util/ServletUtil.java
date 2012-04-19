package com.cannontech.util;

import java.awt.Color;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.sql.ResultSetMetaData;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.EnumSet;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.Vector;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletRequest;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.util.UrlPathHelper;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.exception.NotLoggedInException;
import com.cannontech.common.util.TimeUtil;
import com.cannontech.database.SqlUtils;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.google.common.collect.Multimap;

/**
 * The junk drawer for servlets.
 * Added static Attribute values from stars for reference in all web applications. (04/15/2004 SN)
 * Creation date: (3/28/00 11:19:23 AM)
 * @author: 
 */

public class ServletUtil {

    private static final PathMatcher pathMatcher = new AntPathMatcher();
    private static final UrlPathHelper urlPathHelper = new UrlPathHelper();

	//Session attributes.
	public static final String ATT_ERROR_MESSAGE = "ERROR_MESSAGE";
	public static final String ATT_CONFIRM_MESSAGE = "CONFIRM_MESSAGE";
	public static final String ATT_YUKON_USER = "YUKON_USER";
	
	public static final String ATT_REDIRECT = "REDIRECT";
	public static final String ATT_REDIRECT2 = "REDIRECT2";
	public static final String ATT_REFERRER = "REFERRER";
	public static final String ATT_REFERRER2 = "REFERRER2";

	/**
     * if used in session, this attribute should be passed a CtiNavObject
	 */
	public static final String NAVIGATE = "CtiNavObject";
	
    /**
     * if used in session, this attribute should be passed an ArrayList of FilterWrappers
     */
    public static final String FILTER_INVEN_LIST = "InventoryFilters";
    public static final String FILTER_WORKORDER_LIST = "WorkOrderFilters";
    
	public static final String ATT_GRAPH_BEAN = "GRAPH_BEAN";
	public static final String ATT_REPORT_BEAN = "REPORT_BEAN";
	public static final String ATT_BILLING_BEAN = "BILLING_BEAN";
	public static final String ATT_YC_BEAN = "YC_BEAN";
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
    public static final String PREVTHIRTYDAYS= "Prev 30 Days";
    public static final String EVENT = "Event";
    public static final String SESSION_INFO = "Session Info";
    

//	private static String[] validPeriods =
//	{
//		ONEDAY,
//		THREEDAYS,
//		FIVEDAYS,
//		ONEWEEK,
//		ONEMONTH,
//		FOURWEEKS,
//		FIVEWEEKS,
//		TODAY,
//		PREVTWODAYS,
//		PREVTHREEDAYS,
//		PREVFIVEDAYS,
//		PREVSEVENDAYS,
//		PREVONEWEEK,
//        PREVTHIRTYDAYS
//		
//	};
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
		ONEMONTH,
        EVENT
	};

	public static String[] currentPeriods =
	{
		TODAY,
		PREVTWODAYS,
		PREVTHREEDAYS,
		//PREVFIVEDAYS,
		PREVSEVENDAYS,
        EVENT
	};

		
	// Date/Time pattern strings that will be tried when
	// attempting to interprate starting dates
	private static final SimpleDateFormat[] dateFormat =
	{
		new SimpleDateFormat("MM:dd:yyyy:HH:mm:ss"),
		new SimpleDateFormat("MM/dd/yyyy HH:mm:ss"),
		new SimpleDateFormat("MM/dd/yyyy HH:mm"),
		new SimpleDateFormat("MM/dd/yy"),
		new SimpleDateFormat("MM-dd-yy"),
		new SimpleDateFormat("MM.dd.yy"),
		new SimpleDateFormat("MM/dd/yyyy"),
		new SimpleDateFormat("MM-dd-yyyy"),
		new SimpleDateFormat("MM.dd.yyyy"),
		new SimpleDateFormat("HH:mm:ss"),
		new SimpleDateFormat("HH:mm")		
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
	public static final String FORMAT_ADD_ARTICLE = "add_article";
	
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
	Object[][] data = null;
	
	try
	{
		connection = com.cannontech.database.PoolManager.getInstance().getConnection( dbAlias );
		statement = connection.createStatement();
		resultSet = statement.executeQuery( query );
		Vector<Vector<Object>> rows = new Vector<Vector<Object>>();
		int columnCount = 0;		
		ResultSetMetaData metaData = resultSet.getMetaData();		
		columnCount = metaData.getColumnCount();
		
		while( resultSet.next() )
		{
			Vector<Object> rowData = new Vector<Object>();
			boolean nonNullRow = false;
						
			for( int i = 1; i <= columnCount; i++ )
			{
                Object o = SqlUtils.getResultObject(resultSet, i);;               

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
			Vector<Object> temp = rows.elementAt(i);
			data[i] = temp.toArray();//temp.copyInto( data[i] );
		}
	}
	catch( java.sql.SQLException e )
	{
		CTILogger.error( e.getMessage(), e );
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
public static Object[][] executeSQL(String dbAlias, String query, Class<? extends Object>[] types) {
	java.sql.Connection connection = null;
	java.sql.Statement statement = null;
	java.sql.ResultSet resultSet = null;
	Object[][] data = null;
	
	try
	{
		connection = com.cannontech.database.PoolManager.getInstance().getConnection( dbAlias );
		statement = connection.createStatement();
		resultSet = statement.executeQuery( query );
		Vector<Vector<Object>> rows = new Vector<Vector<Object>>();
		int columnCount = 0;		
		ResultSetMetaData metaData = resultSet.getMetaData();		
		columnCount = metaData.getColumnCount();
		
		while( resultSet.next() )
		{
			Vector<Object> rowData = new Vector<Object>();
			boolean nonNullRow = false;
						
			for( int i = 1; i <= columnCount; i++ )
			{
				Class<? extends Object> thisColumn = types[i-1];
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
                    o = SqlUtils.getResultObject(resultSet, i);                    
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
			Vector<Object> temp = rows.elementAt(i);
			data[i] = temp.toArray();//temp.copyInto( data[i] );
		}
	}
	catch( java.sql.SQLException e )
	{
		CTILogger.error( e.getMessage(), e );
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
public static Object[][] executeSQL(HttpSession session, String query ) 
{
	if( session == null )
		return null;
			
	java.sql.Connection connection = null;
	java.sql.Statement statement = null;
	java.sql.ResultSet resultSet = null;
	Object[][] data = null;
	
	try
	{
		connection = com.cannontech.database.PoolManager.getInstance().getConnection("yukon");
		statement = connection.createStatement();
		resultSet = statement.executeQuery( query );
		Vector<Vector<Object>> rows = new Vector<Vector<Object>>();
		int columnCount = 0;
		ResultSetMetaData metaData = resultSet.getMetaData();		
		columnCount = metaData.getColumnCount();
		
		while( resultSet.next() )
		{
			Vector<Object> rowData = new Vector<Object>();
			boolean nonNullRow = false;
						
			for( int i = 1; i <= columnCount; i++ )
			{
			    Object o = SqlUtils.getResultObject(resultSet, i);;			    

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
			Vector<Object> temp = rows.elementAt(i);
			data[i] = temp.toArray();//temp.copyInto( data[i] );
		}
	}
	catch( java.sql.SQLException e )
	{
		CTILogger.error( e.getMessage(), e );
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
public static Object[][] executeSQL(HttpSession session, String query, Class<? extends Object>[] types ) 
{
	if( session == null )
		return null;
	
	java.sql.Connection connection = null;
	java.sql.Statement statement = null;
	java.sql.ResultSet resultSet = null;
	Object[][] data = null;
	
	try
	{
		connection = com.cannontech.database.PoolManager.getInstance().getConnection("yukon");
		statement = connection.createStatement();
		resultSet = statement.executeQuery( query );
		Vector<Vector<Object>> rows = new Vector<Vector<Object>>();
		int columnCount = 0;
        ResultSetMetaData metaData = resultSet.getMetaData();		
		columnCount = metaData.getColumnCount();

		while( resultSet.next() )
		{
			Vector<Object> rowData = new Vector<Object>();
			boolean nonNullRow = false;
						
			for( int i = 1; i <= columnCount; i++ )
			{
				Class<? extends Object> thisColumn = types[i-1];
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
	                o = SqlUtils.getResultObject(resultSet, i);
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
			Vector<Object> temp = rows.elementAt(i);
			data[i] = temp.toArray();//temp.copyInto( data[i] );
		}
	}
	catch( java.sql.SQLException e )
	{
		CTILogger.error( e.getMessage(), e );
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
 * @deprecated use TimeUtil.getMidnight
 */
public static Date getDate(int dayOffset) {

	GregorianCalendar cal = new GregorianCalendar();
	cal.setTime(new Date());
	
	cal.add( Calendar.DAY_OF_YEAR, dayOffset ); 
 	cal.set( Calendar.HOUR_OF_DAY, 0 );
	cal.set( Calendar.MINUTE, 0 );
	cal.set( Calendar.SECOND, 0 );

	return cal.getTime();
}
/**
 * Insert the method's description here.
 * Creation date: (3/28/00 4:03:38 PM)
 * @return java.util.Date
 * @param startingDate java.util.Date
 * @param period java.lang.String
 */
public static Date getEndingDateOfInterval(Date startingDate, String period) {
	
	int numDays;
	period = period.trim();
		
	if( period.equalsIgnoreCase(ONEDAY) 		||
		period.equalsIgnoreCase(TODAY)			||		
		period.equalsIgnoreCase(PREVTWODAYS) 	||
		period.equalsIgnoreCase(PREVTHREEDAYS) 	||
		period.equalsIgnoreCase(PREVFIVEDAYS) 	||
		period.equalsIgnoreCase(PREVSEVENDAYS) 	||
		period.equalsIgnoreCase(PREVONEWEEK)    ||
        period.equalsIgnoreCase(PREVTHIRTYDAYS) )
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
		GregorianCalendar c = new GregorianCalendar();
		c.setTime(startingDate);
		c.add( Calendar.MONTH, 1 );

		Date endOfInterval = c.getTime();

		numDays = TimeUtil.absDifferenceInDays(startingDate, endOfInterval);		
	}
	else
    if( period.equalsIgnoreCase(EVENT) )
    {
        return startingDate;
	}
	else
		return null;
		
	GregorianCalendar cal = new GregorianCalendar();
	cal.setTime(startingDate);
	cal.add( Calendar.DAY_OF_YEAR, numDays );

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
		GregorianCalendar c = new GregorianCalendar();
		c.setTime(getToday());
		c.add( Calendar.MONTH, 1 );
		Date endOfInterval = c.getTime();
		return TimeUtil.absDifferenceInDays(getToday(), endOfInterval);		
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
	if( stringValue.equalsIgnoreCase(PREVSEVENDAYS)
        || stringValue.equalsIgnoreCase(PREVONEWEEK) )
		return -6;
    else
    if( stringValue.equalsIgnoreCase(PREVTHIRTYDAYS) )
        return -29;
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
public static String getPeriodFromDates(Date start, Date end) {

	long startTime = start.getTime();
	long endTime = end.getTime();

	//TimeUtil.differenceInDays NOT used becuase it returns and abs value
	int numDays = (int) Math.round(((double) (endTime-startTime)) / (double) 86400000 );

	//figure out the closest/most reasonable period we have to numDays
    if( numDays <= -29 )
        return PREVTHIRTYDAYS;
    else
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
public static java.util.Date getStartingDateOfInterval(Date startingDate, String period) {
	
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
    else
    if( period.equalsIgnoreCase(PREVTHIRTYDAYS) )
    {
        numDays = -29;
    }
    else
    if( period.equalsIgnoreCase(EVENT) )
    {
        return new Date(0);
    }

	else	//Don't change the starting date
		return startingDate;
		
	GregorianCalendar cal = new GregorianCalendar();
	cal.setTime(startingDate);
	cal.add( Calendar.DAY_OF_YEAR, numDays );

	return cal.getTime();
}
/**
 * Returns a Date that represents the beginning of today.
 * Creation date: (3/28/00 4:15:54 PM)
 * @return java.util.Date
 * @deprecated
 */
public static java.util.Date getToday() {
	return getToday(TimeZone.getDefault());
}

/**
 * Returns a Date that represents the beginning of today in the given TimeZone
 * @param tz
 * @return
 * @deprecated use TimeUtil.getMidnight
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
 * @deprecated use TimeUtil.getMidnight
 */
public static java.util.Date getTomorrow() {
	return getTomorrow(TimeZone.getDefault());
	
}

/**
 * Returns a Date that represents the beginning of tomorrow in the given TimeZone.
 * @param tz
 * @return
 * @deprecated use TimeUtil.getMidnight
 */
public static Date getTomorrow(TimeZone tz) {
	GregorianCalendar cal = new GregorianCalendar();
	cal.setTimeZone(tz);
	cal.setTime(new Date());
	
	cal.add( Calendar.DAY_OF_YEAR, 1 );
	cal.set( Calendar.HOUR_OF_DAY, 0 );
	cal.set( Calendar.MINUTE, 0 );
	cal.set( Calendar.SECOND, 0 );
	return cal.getTime();
}

/**
 * Returns a Date that represents the beginning of yesterday.
 * Creation date: (3/28/00 4:22:52 PM)
 * @return java.util.Date
 * @deprecated use TimeUtil.getMidnight
 */
public static java.util.Date getYesterday() {
	return getYesterday(TimeZone.getDefault());
}

/**
 * Returns a Date that represents the beginning of yesterday in the given TimeZone.
 * @param tz
 * @return
 * @deprecated use TimeUtil.getMidnight
 */
public static Date getYesterday(TimeZone tz) {
	GregorianCalendar cal = new GregorianCalendar();
	cal.setTimeZone(tz);
	cal.setTime(new Date());
	
	cal.add( Calendar.DAY_OF_YEAR, -1 ); 
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
	 * Used to return NULL for a param if it is not found OR if it is not set
	 * @param req_
	 * @param name_
	 * @return
	 */
	public synchronized static String getParameter( HttpServletRequest req_, String name_ ) {
        return getParameter(req_, name_, null);
	}

    /**
     * Convenience method to get a servlet parameter.
     * @param req
     * @param parameterName
     * @param defaultValue
     * @return
     */
    public synchronized static String getParameter(HttpServletRequest req, String parameterName, String defaultValue) {
        String s= req.getParameter(parameterName);
        return (s == null || s.length() == 0 ? defaultValue : s);
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
	
	/**
	 * Add an indefinite article in front of a word
	 * @param word
	 * @return
	 */
	public static String addArticle(String word) {
		if (word.charAt(0) == 'a' || word.charAt(0) == 'A'
			|| word.charAt(0) == 'e' || word.charAt(0) == 'E'
			|| word.charAt(0) == 'i' || word.charAt(0) == 'I'
			|| word.charAt(0) == 'o' || word.charAt(0) == 'O'
			|| word.charAt(0) == 'u' || word.charAt(0) == 'U')
			return "an " + word;
		else
			return "a " + word;
	}

	/**
	 * Returns the current Yukon user object found in the session.
	 *
	 */
	public static LiteYukonUser getYukonUser( HttpSession session ) {
	    if (session == null) return null;
		return (LiteYukonUser) session.getAttribute(ATT_YUKON_USER);
	}

	/**
	 * Returns the current Yukon user object found in the request.
	 * @throws NotLoggedInException if no session exists
	 */
	public static LiteYukonUser getYukonUser(ServletRequest request) throws NotLoggedInException
	{
	    HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpSession session = httpRequest.getSession(false);
        if (session == null) {
            throw new NotLoggedInException();
        }
	    LiteYukonUser yukonUser = getYukonUser(session);
        if (yukonUser == null) {
            throw new NotLoggedInException();
        }
        return yukonUser;
	}
	

	/**
	 * Returns the fully qualified URL that was requested
	 *
	 */
	public static String getFullURL( HttpServletRequest req )
	{
	    if( req == null ) return "";
	    
	    String q = "";
	    if( req.getQueryString() != null )
	        q = "?" + req.getQueryString();
	    
	    return req.getRequestURI() + q;		
	}
	
	/**
	 * Returns the URL for the "host". Could be useful
	 * for building links that go into emails.
	 *
	 */
	public static URL getHostURL(HttpServletRequest req) {
        try {
            StringBuffer hostString = req.getRequestURL();
            URL fullHostUrl = new URL(hostString.toString());
            URL hostUrl = new URL(fullHostUrl.getProtocol(), fullHostUrl.getHost(), fullHostUrl.getPort(), "");	
            return hostUrl;
        } catch (MalformedURLException e) {
            throw new RuntimeException("Unable to build host URL", e);
        }
	}
    
	
    public static String createSafeUrl(ServletRequest request, String url) {
        if (request instanceof HttpServletRequest) {
            HttpServletRequest httpRequest = (HttpServletRequest)request;
            if (url.startsWith("/")) {
                return httpRequest.getContextPath() + url;
            } else {
                return url;
            }
        } else {
            return url;
        }
    }
    
    /**
     * Strips what could be harmful redirect information out of a URL.
     * At the very least returning "/".
     * @param request
     * @param url
     * @return a stripped version of the URL
     */
    public static String createSafeRedirectUrl(final ServletRequest request, final String url) {
        if (url == null) return "/";
        
        Matcher matcher = Pattern.compile("^\\w{3,}://.+?(/.*)$").matcher(url);
        boolean matches = matcher.matches();
        
        Matcher matcher2 = Pattern.compile("^\\w{3,}://.+$").matcher(url);
        boolean matches2 = !matches && matcher2.matches();

        String safeUrl;
        if (matches) {
            String matchedUrl = matcher.group(1);
            safeUrl = (matchedUrl != null) ? matchedUrl : "/";
        } else if (matches2) {
            safeUrl = "/";
        } else {
            safeUrl = url;
        }
        
        return ServletUtil.createSafeUrl(request, safeUrl);
    }
    
    /**
     * Using a <String, String> Map, build a name1=value1&name2=value2 style URL query string.
     * Does NOT encode parameters, assumes parameters will be appropriately encoded already by caller.
     * 
     * @param propertiesMap
     * @param escapeHtml
     * @return queryString
     */
    public static String buildQueryStringFromMap(Map<String,String> encodedParameters, boolean escapeHtml) {
        
        Set<Entry<String, String>> entrySet = encodedParameters.entrySet();
        String queryString = buildQueryStringFromEntries(entrySet, escapeHtml);
        
        return queryString;
    }

    /**
     * Using a <String, String> MultiMap, build a name1=value1&name2=value2 style URL query string.
     * Does NOT encode parameters, assumes parameters will be appropriately encoded already by caller.
     * The difference between this method and buildQueryStringFromMap(Map<String,String> encodedParameters)
     * is that the Multimap allows for multiple values per parameter name.
     * 
     * @param propertiesMap
     * @param escapeHtml
     * @return queryString
     */
    public static String buildQueryStringFromMap(Multimap<String, String> encodedParameters, boolean escapeHtml) {
        
        Collection<Entry<String, String>> entries = encodedParameters.entries();
        String queryString = buildQueryStringFromEntries(entries, escapeHtml);
        
        return queryString;
    }
    
    private static String buildQueryStringFromEntries(
                                                      Iterable<Entry<String, String>> entrySet,
                                                      boolean escapeHtml) {
        List<String> parameterPairs = new ArrayList<String>(); 
        for (Map.Entry<String, String> entry : entrySet) {
            String thisPair = entry.getKey() + "=" + entry.getValue();
            parameterPairs.add(thisPair);
        }

        String queryString = StringUtils.join(parameterPairs, "&");
        if (escapeHtml) {
            queryString = StringEscapeUtils.escapeHtml(queryString);
        }
        return queryString;
    }
    
    /**
     * Using a <String, String> Map, build a name1=value1&name2=value2 style URL query string.
     * Does NOT encode parameters, assumes parameters will be appropriately encoded already by caller.
     * The result queryString is escaped for html.
     * @param propertiesMap
     * @return queryString
     */    
    public static String buildQueryStringFromMap(Map<String,String> encodedParameters) {
        return buildQueryStringFromMap(encodedParameters, true);
    }
    
    /**
     * Using a <String, String> Map, build a name1=value1&name2=value2 style URL query string
     * using safe URL encoding.
     * 
     * @param propertiesMap
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String buildSafeQueryStringFromMap(Map<String,String> propertiesMap, boolean htmlOutput) {
        try {
            final String urlEncoding = "UTF-8"; 
            List<String> parameterPairs = new ArrayList<String>(propertiesMap.size()); 
            for (String parameter : propertiesMap.keySet()) {
                String thisPair = URLEncoder.encode(parameter, urlEncoding) + "=" + URLEncoder.encode(propertiesMap.get(parameter), urlEncoding);
                parameterPairs.add(thisPair);
            }

            String queryString = StringUtils.join(parameterPairs, htmlOutput ? "&" : "&amp;");

            return queryString;
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Unable to build query string", e);
        }
    }
    
    
    
    public static String makeWindowsSafeFileName(String fileName) {
        
        char[] badChars = {' ', '\\', '/', ':', '*', '?', '<', '>', '|'};
        
        for(char c : badChars) {
            fileName = fileName.replace(c, '_');
        }
        
        return fileName;
    }
    
    /**
     * Returns the url passed in with the parameter and value appended.
     * Note: this method does not protect against adding a parameter that is 
     * already on the query string.
     * @param url
     * @param parameterMap
     * @return a full path and query string
     */
    public static String addParameters(String url, String parameter, String value) {
        HashMap<String, String[]> parameterMap = new HashMap<String, String[]>();
        parameterMap.put(parameter, new String[] {value});
        StringBuffer fullURL = new StringBuffer();
        fullURL.append(url);
        if(!url.contains("?")) {
            fullURL.append("?");
        } else {
            fullURL.append("&");
        }

        String queryString = buildQueryStringParameters(parameterMap);
        fullURL.append(queryString);
        return fullURL.toString();
    }
    
    /**
     * Returns a URL that points to the same page as request, but has newParameter and newValue
     * either appended to the end or replaced. For example, if you requested the page 
     *    /dir/script.jsp?color=red&flavor=tart
     * and then called 
     *    tweakRequestURI(request, "flavor", "salty")
     * the String returned would be
     *    /dir/script.jsp?color=red&flavor=salty
     * 
     * Alternatively can be used to remove a parameter from a URI.
     * If newValue is null then newParameter will be removed from the
     * generated request string.
     * 
     * @param request the HttpServletRequest object for the current page
     * @param newParameter the name of the parameter to add or replace
     * @param newValue the value of the new parameter
     * @return a full path and query string
     */
	public static String tweakRequestURI(HttpServletRequest request, String newParameter, String newValue) {
        StringBuffer result = new StringBuffer();
        result.append(request.getRequestURI());
        result.append("?");
        Map<String, String[]> parameterMap = new HashMap<String, String[]>(request.getParameterMap());
        if(newValue == null) {
            parameterMap.remove(newParameter);
        }
        else {              
            parameterMap.put(newParameter, new String[] {newValue});
        }
        String queryString = buildQueryStringParameters(parameterMap);
        result.append(queryString);
        return result.toString();
    }
        
    private static String buildQueryStringParameters(Map<String, String[]> parameterMap) {
        String queryString;
        try {
            final String urlEncoding = "UTF-8"; 
            List<String> parameterPairs = new ArrayList<String>(parameterMap.size());
            for (Iterator<String> iter = parameterMap.keySet().iterator(); iter.hasNext();) {
                String thisParameter = iter.next();
                String thisSafeParameter = URLEncoder.encode(thisParameter, urlEncoding);
                String[] theseValues = parameterMap.get(thisParameter);
                for (int i = 0; i < theseValues.length; i++) {
                    String thisPair = thisSafeParameter + "=" 
                        + URLEncoder.encode(theseValues[i], urlEncoding);
                    parameterPairs.add(thisPair);
                }
            }
            queryString = StringUtils.join(parameterPairs.iterator(), "&");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        return queryString;
    }

    /**
     * Calls tweakRequestURI() with the same parameters, but escapes the result so it 
     * can be displayed on a web page (most importantly converting '&' to '&amp;').
     * @param request the HttpServletRequest object for the current page
     * @param newParameter the name of the parameter to add or replace
     * @param newValue the value of the new parameter
     * @return an HTML escaped full path and query string
     */
    public static String tweakHTMLRequestURI(HttpServletRequest request, String newParameter, String newValue) {
        return StringEscapeUtils.escapeHtml(tweakRequestURI(request, newParameter, newValue));
    }
    
    /**
     * Returns a URL that points to the same request URL passed in, but has newParameter and newValue
     * either appended to the end or replaced. For example, if you requested the page 
     *    /dir/script.jsp?color=red&flavor=tart
     * and then called 
     *    tweakRequestURI(request, "flavor", "salty")
     * the String returned would be
     *    /dir/script.jsp?color=red&flavor=salty
     * 
     * Alternatively can be used to remove a parameter from a URI.
     * If newValue is null then newParameter will be removed from the
     * generated request string.
     * 
     * @param requestUrl url of the request along with query string params
     * @param newParameter the name of the parameter to add or replace
     * @param newValue the value of the new parameter
     * @return a full path with query string
     */
    public static String tweakRequestURL(String requestUrl, String newParameter, String newValue) {
        StringBuffer result = new StringBuffer();
        result.append(getBaseUrl(requestUrl));
        result.append("?");
        Map<String, String[]> parameterMap = getQueryStringParams(requestUrl);
        if(newValue == null) {
            parameterMap.remove(newParameter);
        }
        else {              
            parameterMap.put(newParameter, new String[] {newValue});
        }
        String queryString = buildQueryStringParameters(parameterMap);
        result.append(queryString);
        return result.toString();
    }
    
    private static String getBaseUrl(String requestUrl) {
        String baseUrl = requestUrl;
        int questionMark = requestUrl.indexOf('?');
        if (questionMark > 0) {
            baseUrl = requestUrl.substring(0, questionMark);
        }
        return baseUrl;
    }
    
    private static Map<String, String[]> getQueryStringParams(String requestUrl) {
        Map<String, String[]> paramMap = new HashMap<String, String[]>();
        final String urlDecoding = "UTF-8";

        try {
            int questionMark = requestUrl.indexOf('?');
            if (questionMark > 0) {
                String queryString = requestUrl.substring(questionMark + 1);
                String[] params = StringUtils.split(queryString, "&");
                for (String param : params) {
                    String[] nameValue = StringUtils.split(param, "=");
                    if (nameValue.length == 2) {
                        paramMap.put(nameValue[0], new String[] {URLDecoder.decode(nameValue[1], urlDecoding)});
                    } else {
                        paramMap.put(nameValue[0], new String[] {""});
                    }
                }
            }
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        return paramMap;
    }
    
    /**
     * Prints out the stack trace of the Throwable. HTML characters are escaped.
     * Certain lines will be printed as bold and red. Which lines are determined
     * within this function, but is currently configured to be methods in any
     * com.cannontech package and the MyFaces method that indciates which rendering
     * phase the error was in.
     * @param t the Throwable who's stack trace will be printed
     * @param p the PrintWriter on which the stack trace will be printed
     */
    public static String printNiceHtmlStackTrace(Throwable t) {
        StringWriter sw = new StringWriter();
        PrintWriter p = new PrintWriter (sw);
        String[] keyWords = {"com.cannontech", "org.apache.myfaces.lifecycle"};
        synchronized (p) {
            p.write("<pre>");
            while ( t != null ) {
                Throwable cause = ExceptionUtils.getCause(t);
                if (cause == null) {
                    String escapedCause = StringEscapeUtils.escapeHtml(t.toString());
                    p.println(escapedCause);
                    StackTraceElement[] trace = t.getStackTrace();
                    for (StackTraceElement element : trace) {
                        String className = element.getClassName();
                        boolean specialLine = false;
                        for (String keyWord : keyWords) {
                            if (className.contains(keyWord)) {
                                specialLine = true;
                                break;
                            }
                        }
                        String thisLine = element.toString();
                        String safeLine = StringEscapeUtils.escapeHtml(thisLine);
                        if (specialLine) {
                            p.println("<span style=\"font-weight: bold;\">\tat " + safeLine + "</span>");
                        } else {
                            p.println("<span style=\"color: #666;\">\tat " + safeLine + "</span>");
                        }
                    }
                    
                } else {
                    String escapedCause = StringEscapeUtils.escapeHtml(t.toString());
                    p.println(escapedCause);
                    p.write("\n");
                }
                t = cause;
            }
            p.write("</pre>");
        }
        return sw.toString();
    }

    public static boolean isExcludedRequest(HttpServletRequest request, List<String> patterns) {
        String pathWithinApplication = urlPathHelper.getPathWithinApplication(request);

        for (String pattern : patterns) {
            if (pathMatcher.match(pattern, pathWithinApplication)) {
                return true;
            }
        }
        return false;
    }

	public static boolean isAjaxRequest(ServletRequest req) {
		if (req instanceof HttpServletRequest) {
			HttpServletRequest httpReq = (HttpServletRequest) req;
			String header = httpReq.getHeader("X-Requested-With");
			if (header != null) {
				return header.startsWith("XMLHttpRequest");
			}
		}
		return false;
	}
	
	public static void createCookie(final HttpServletRequest request, final HttpServletResponse response,
	        final String cookieName, final String cookieValue) {
	    Cookie cookie = new Cookie(cookieName, cookieValue);
        cookie.setMaxAge(Integer.MAX_VALUE);
        cookie.setPath("/" + request.getContextPath());
        response.addCookie(cookie);
	}
	
    public static Cookie getCookie(final HttpServletRequest request, final String cookieName) {
        final Cookie[] cookieArray = request.getCookies();
        if (cookieArray == null) return null;
        for (final Cookie cookie : cookieArray) {
            if (cookie.getName().equals(cookieName)) {
                return cookie;
            }
        }
        return null;
    }
    
    public static void deleteAllCookies(final HttpServletRequest request, final HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return;
        for (final Cookie cookie : cookies) {
            cookie.setMaxAge(0);
            cookie.setValue("");
            cookie.setPath("/" + request.getContextPath());
            response.addCookie(cookie);
        }
    }
    /**
     * Expose the model objects in the given map as request attributes.
     * Names will be taken from the model Map.
     * This method is suitable for all resources reachable by {@link javax.servlet.RequestDispatcher}.
     * 
     * Copied from org.springframework.web.servlet.view.AbstractView 
     * @param model Map of model objects to expose
     * @param request current request
     */
    public static <K,V> void exposeModelAsRequestAttributes(Map<K,V> model, ServletRequest request) {
        Iterator<Map.Entry<K,V>> it = model.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<K,V> entry = it.next();
            if (!(entry.getKey() instanceof String)) {
                throw new IllegalArgumentException(
                        "Invalid key [" + entry.getKey() + "] in model Map: only Strings allowed as model keys");
            }
            String modelName = (String) entry.getKey();
            Object modelValue = entry.getValue();
            if (modelValue != null) {
                request.setAttribute(modelName, modelValue);
            }
            else {
                request.removeAttribute(modelName);
            }
        }
    }
    
    /**
     * Helper method to convert the parameter map return by request.getParameterMap()
     * which is a Map<String, String[]> into a Map<string, String>. 
     * Note: Multiple values for a parameter are reduced the value of the last of them.
     * @param request
     * @return
     */
    public static Map<String, String> getParameterMap(HttpServletRequest request) {
        
        Map<String, String[]> parameterMapWithArrays = request.getParameterMap();
        
        Map<String, String> parameterMap = new HashMap<String, String>();
        
        for(String pKey : parameterMapWithArrays.keySet()) {
            String[] vals = parameterMapWithArrays.get(pKey);
            for(int i = 0; i < vals.length; i++) {
                parameterMap.put(pKey, vals[i]);
            }
        }
        
        return parameterMap;
    }
    
    public static <T> Map<T, Boolean> convertSetToMap(Set<T> allExistingAttributes) {
        Map<T, Boolean> existingMap = new HashMap<T, Boolean>();
        
        // convert to a map of true's because JSP EL can use this to check "contains"
        for (T attribute : allExistingAttributes) {
            existingMap.put(attribute, Boolean.TRUE);
        }
        
        return existingMap;
    }
    
    public static <T> Map<T, Boolean> convertListToMap(List<T> allExistingAttributes) {
        Map<T, Boolean> existingMap = new HashMap<T, Boolean>();
        
        // convert to a map of true's because JSP EL can use this to check "contains"
        for (T attribute : allExistingAttributes) {
            existingMap.put(attribute, Boolean.TRUE);
        }
        
        return existingMap;
    }
    
    public static String urlEncode(String input) {
        try {
            return URLEncoder.encode(input, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("could not encode string", e);
        }
    }
    
    /**
     * Helper method to put all String parameters with a given prefix into a map
     */
    public static Map<String, String> getStringParameters(HttpServletRequest request,
            String prefix) {

        Map<String, String> returnMap = new HashMap<String, String>();

        Map<String, String[]> parameterMap = request.getParameterMap();
        for (String key : parameterMap.keySet()) {

            if (key.startsWith(prefix)) {
                String paramKey = key.substring(prefix.length());
                String[] value = parameterMap.get(key);

                returnMap.put(paramKey, value[0]);
            }

        }

        return returnMap;
    }

    /**
     * Helper method to put all Integer parameters with a given prefix into a
     * map
     */
    public static Map<String, Integer> getIntegerParameters(
            HttpServletRequest request, String prefix) {

        Map<String, Integer> returnMap = new HashMap<String, Integer>();

        Map<String, String[]> parameterMap = request.getParameterMap();
        for (String key : parameterMap.keySet()) {

            if (key.startsWith(prefix)) {
                String paramKey = key.substring(prefix.length());
                String[] object = parameterMap.get(key);
                Integer value = Integer.valueOf(object[0]);

                returnMap.put(paramKey, value);
            }

        }

        return returnMap;
    }
    /**
     * Helper method to convert a string of Integers into a list
     * @param integerStringList - String of comma separated paoIds
     * @return Integer list of paoIds
     */
    public static List<Integer> getIntegerListFromString(String integerStringList) {

        List<Integer> idList = new ArrayList<Integer>();

        if (integerStringList != null && integerStringList.length() > 0) {
            String[] ids = integerStringList.split(",");
            for (String id : ids) {
                idList.add(Integer.valueOf(id.trim()));
            }
        }
        return idList;

    }
    
    public static <T extends Enum<T>> EnumSet<T> convertStringArrayToEnums(String[] enumStrings, Class<T> enumClass) {
        EnumSet<T> result = EnumSet.noneOf(enumClass);
        if (enumStrings != null) {
            for (String enumStr : enumStrings) {
                T value = Enum.valueOf(enumClass, enumStr);
                result.add(value);
            }
        }
        return result;
    }
}