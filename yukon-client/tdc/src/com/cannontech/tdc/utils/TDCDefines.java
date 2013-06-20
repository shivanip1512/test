package com.cannontech.tdc.utils;
/**
 * Insert the type's description here.
 * Creation date: (2/11/00 1:18:48 PM)
 * @author: 
 */

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import com.cannontech.common.login.ClientSession;
import com.cannontech.common.util.ClientRights;
import com.cannontech.core.roleproperties.YukonRoleProperty;

public final class TDCDefines 
{
	// main things of TDC
	public static final String HELP_FILE = "Tabular_Data_Console_Help.chm";

	public static String DBALIAS = "yukon";

	public static final String DISPLAY_OUT_FILE_NAME = com.cannontech.common.util.CtiUtilities.getConfigDirPath() + "TDCDisplaySettings.DAT";

   public static final javax.swing.Icon ICON_ALARM = new javax.swing.ImageIcon("/AlarmSqr.gif");
      //new javax.swing.ImageIcon("C:/Documents and Settings/rneuharth/My Documents/My Pictures/AlarmSqr.gif");
	
	// JTable model constants
	public static final int ROW_BREAK_COUNT = 8;
	public static final int ROW_BREAK_ID = 
            com.cannontech.database.data.point.PointTypes.SYS_PID_SYSTEM;

	public static int MAX_ROWS = 500;
	public static final String BOOKMARK_FILE_NAME = com.cannontech.common.util.CtiUtilities.getConfigDirPath() + "/TDCBookMarks.txt";

   //hex value representing the privileges of the user on this machine
   public static final long USER_RIGHTS = Long.parseLong( 
			ClientSession.getInstance().getRolePropertyValue(
         YukonRoleProperty.TDC_RIGHTS), 16 );


/**
 * This method was created in VisualAge.
 * @return int
 */
public static int createValidDisplayNumber () 
{
	boolean found = false;

	// Get the display numbers
	String query = new String
			("select displaynum from display order by displaynum" );
	Object[][] textCount = DataBaseInteraction.queryResults( query, null );
		
	int cnt = textCount.length;
	
	int j = com.cannontech.tdc.data.Display.BEGINNING_USER_DISPLAY_NUMBER;
	while ( j < com.cannontech.tdc.data.Display.MAX_DISPLAY_NUMBER )  // gotta have some bound
	{
		found = false;
		
		for ( int i = 0; i < cnt; i++ )
		{
			// linear lookup
			if ( textCount[i][0].toString().equalsIgnoreCase(Integer.toString( j ) ) )
			{
				found = true;
				break;
			}
		}
		
		if ( found )
			j++;
		else
			return ( j );
	}

	return com.cannontech.tdc.data.Display.BEGINNING_USER_DISPLAY_NUMBER; // j is too large, return the starting number!!
}


public static int getValidDisplayNumber () {
    String query1 = "select templatenum from template order by templatenum";
    return createValidDisplayNumber (query1);
}
/**
 * This method was created in VisualAge.
 * @return int
 */
public static int createValidDisplayNumber ( String query ) 
{
	boolean found = false;

	// Get the numbers
	String newQuery = new String( query );
	Object[][] textCount = DataBaseInteraction.queryResults( newQuery, null );
			
	
	int cnt = textCount.length;
	
	int j = com.cannontech.tdc.data.Display.BEGINNING_USER_DISPLAY_NUMBER;
	while ( j < com.cannontech.tdc.data.Display.MAX_DISPLAY_NUMBER )  // gotta have some bound
	{
		found = false;
		
		for ( int i = 0; i < cnt; i++ )
		{
			// linear lookup
			if ( textCount[i][0].toString().equalsIgnoreCase(Integer.toString( j ) ) )
			{
				found = true;
				break;
			}
		}
		
		if ( found )
			j++;
		else
			return ( j );
	}

	return com.cannontech.tdc.data.Display.BEGINNING_USER_DISPLAY_NUMBER; // j is too large, return the starting number!!
}



/**
 * Insert the method's description here.
 * Creation date: (3/5/2001 11:32:59 AM)
 * @return boolean
 */
public static boolean isHiddenCapControl( final long readOnlyInteger )
{
	return (readOnlyInteger & ClientRights.HIDE_CAPCONTROL) != 0;
}
/**
 * Insert the method's description here.
 * Creation date: (3/5/2001 11:32:59 AM)
 * @return boolean
 */
public static boolean isHiddenLoadControl( final long readOnlyInteger )
{
	return (readOnlyInteger & ClientRights.HIDE_LOADCONTROL) != 0;
}
/**
 * Insert the method's description here.
 * Creation date: (3/5/2001 11:32:59 AM)
 * @return boolean
 */
public static boolean isHiddenMACS( final long readOnlyInteger )
{
	return (readOnlyInteger & ClientRights.HIDE_MACS) != 0;
}

/**
 * Insert the method's description here.
 * Creation date: (3/5/2001 11:32:59 AM)
 * @return boolean
 */
public static boolean isAlarmColorHidden( final long readOnlyInteger )
{
	return (readOnlyInteger & ClientRights.HIDE_ALARM_COLORS) != 0;
}

public static Calendar getCalendarDate(Date stopDate) {
    Calendar c = new GregorianCalendar();
    c.setTime (stopDate);
    return c;
}

public static Date getNextDay(Calendar c) {
    c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), (c.get(Calendar.DAY_OF_MONTH) + 1), 0, 0);
    Date newStartDate = c.getTime();
    return newStartDate;
}



}