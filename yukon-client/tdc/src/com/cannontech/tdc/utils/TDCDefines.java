package com.cannontech.tdc.utils;
/**
 * Insert the type's description here.
 * Creation date: (2/11/00 1:18:48 PM)
 * @author: 
 */


public final class TDCDefines 
{
	// main things of TDC
	public static final String HELP_FILE = com.cannontech.common.util.CtiUtilities.getHelpDirPath()
			+ "Tabular Data Console Help.chm";

	public static final String ALARM_SOUND_FILE = 
			com.cannontech.common.util.CtiUtilities.getConfigDirPath() + "alarm.au";

	public static String DBALIAS = "yukon";
	public static final String VERSION = 
				com.cannontech.common.version.VersionTools.getYUKON_VERSION() + ".2.14";

	public static final String OUTPUT_FILE_NAME = com.cannontech.common.util.CtiUtilities.getConfigDirPath() + "TDCOut.DAT";
	public static final String DISPLAY_OUT_FILE_NAME = com.cannontech.common.util.CtiUtilities.getConfigDirPath() + "TDCDisplaySettings.DAT";

	public static final java.awt.Image ICON_TDC = java.awt.Toolkit.getDefaultToolkit().getImage("tdcIcon.gif");
	
	// JTable model constants
	public static final int ROW_BREAK_COUNT = 8;
	public static final long ROW_BREAK_ID = 
            com.cannontech.database.data.point.PointTypes.SYS_PID_SYSTEM;

	public static int MAX_ROWS = 500;
	public static final String BOOKMARK_FILE_NAME = com.cannontech.common.util.CtiUtilities.getConfigDirPath() + "/TDCBookMarks.txt";


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
}}