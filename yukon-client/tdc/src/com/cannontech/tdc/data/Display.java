package com.cannontech.tdc.data;

import java.util.Date;
import java.util.GregorianCalendar;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.tdc.filter.DefaultTDCFilter;
import com.cannontech.tdc.filter.ITDCFilter;

/**
 * Insert the type's description here.
 * Creation date: (10/3/00 2:53:36 PM)
 * @author: 
 */
public class Display implements IDisplay
{
	// attributes
	private int displayNumber = UNKNOWN_DISPLAY_NUMBER;
	private String name = null;
	private String type = null;
	private String title = null;
	private String description = null;
	
	private DisplayData displayData = null;


	//what filter is currently being applied to this display
	private ITDCFilter tdcFilter = new DefaultTDCFilter();

	public static final Display UNKNOWN_DISPLAY = new Display();
	
	static
	{
		UNKNOWN_DISPLAY.setName("UNKNOWN");
		UNKNOWN_DISPLAY.setType("UNKNOWN");
		UNKNOWN_DISPLAY.setTitle("UNKNOWN");
		UNKNOWN_DISPLAY.setDescription("UNKNOWN");
	}


/**
 * Display constructor comment.
 */
public Display() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (7/31/2001 2:34:50 PM)
 * @return boolean
 * @param o java.lang.Object
 */
public boolean equals(Object o) 
{
	return ( (o != null) &&
			   (o instanceof Display) &&
			   ( ((Display)o).getDisplayNumber() == getDisplayNumber() ) );
}

public static synchronized boolean isLogDisplay( long displayNum, Date currDate ) 
{
	return
		displayNum == Display.HISTORY_EVENT_VIEWER_DISPLAY_NUMBER 
		|| displayNum == Display.RAW_POINT_HISTORY_VIEWER_DISPLAY_NUMBER
		|| displayNum == SOE_LOG_DISPLAY_NUMBER
		|| displayNum == TAG_LOG_DISPLAY_NUMBER
        || (displayNum == Display.EVENT_VIEWER_DISPLAY_NUMBER && !isTodaysDate(currDate));
}

public static synchronized boolean isHistoryDisplay( long displayNum ) 
{
	return 
		displayNum == Display.HISTORY_EVENT_VIEWER_DISPLAY_NUMBER 
		|| displayNum == Display.EVENT_VIEWER_DISPLAY_NUMBER
		|| displayNum == Display.RAW_POINT_HISTORY_VIEWER_DISPLAY_NUMBER
		|| displayNum == SOE_LOG_DISPLAY_NUMBER
		|| displayNum == TAG_LOG_DISPLAY_NUMBER;
}

public static synchronized boolean isReadOnlyDisplay( long displayNum ) 
{
	return 
		displayNum == Display.HISTORY_EVENT_VIEWER_DISPLAY_NUMBER 
		|| displayNum == Display.EVENT_VIEWER_DISPLAY_NUMBER
		|| displayNum == Display.RAW_POINT_HISTORY_VIEWER_DISPLAY_NUMBER
		|| displayNum == SOE_LOG_DISPLAY_NUMBER
		|| displayNum == TAG_LOG_DISPLAY_NUMBER;		
}

public static synchronized boolean isTodaysDate( Date date_ )
{
	GregorianCalendar newCal = new GregorianCalendar();
	GregorianCalendar todayCal = new GregorianCalendar();
	newCal.setTime( date_ );			
	todayCal.setTime( new Date() );

	return 
		 newCal.get(GregorianCalendar.DAY_OF_YEAR) == todayCal.get( GregorianCalendar.DAY_OF_YEAR )
		 && newCal.get(GregorianCalendar.YEAR) == todayCal.get( GregorianCalendar.YEAR );
}

public static synchronized boolean isAlarmDisplay( long displayNum ) 
{
	return( displayNum >= Display.GLOBAL_ALARM_DISPLAY &&
				displayNum <= Display.LAST_ALARM_DISPLAY );
}

/**
 * Insert the method's description here.
 * Creation date: (4/18/00 1:44:35 PM)
 * Version: <version>
 */
public static synchronized boolean isCoreType( String displayType )
{
	if( displayType == null )
		return false;
		
	return ( Display.getDisplayTypeIndexByType(displayType) == Display.ALARMS_AND_EVENTS_TYPE_INDEX);
}

/**
 * Insert the method's description here.
 * Creation date: (4/18/00 1:44:35 PM)
 * Version: <version>
 */
public static synchronized boolean isUserDefinedType( String displayType ) 
{
	if( displayType == null )
		return false;

	return ( Display.getDisplayTypeIndexByType(displayType) == Display.CUSTOM_DISPLAYS_TYPE_INDEX );
}

/**
 * Insert the method's description here.
 * Creation date: (1/24/2002 9:38:17 AM)
 * @return DisplayData[]
 */
public DisplayData getDisplayData() 
{
	if( displayData == null )
	{
		displayData = new DisplayData();
		displayData.setDisplayNumber( getDisplayNumber() );
		displayData.setFilterID( getTdcFilter().getFilterID() );
	}

	return displayData;
}
/**
 * Insert the method's description here.
 * Creation date: (10/3/00 2:55:08 PM)
 * @return java.lang.String
 */
public java.lang.String getDescription() {
	return description;
}
/**
 * Insert the method's description here.
 * Creation date: (10/3/00 2:55:08 PM)
 * @return long
 */
public int getDisplayNumber() {
	return displayNumber;
}
/**
 * Insert the method's description here.
 * Creation date: (1/18/2001 1:39:31 PM)
 * @return int
 * @param displayType java.lang.String
 */ 
public static String getDisplayTitle(int displayTitle) 
{
	if( displayTitle >= 0 && displayTitle < DISPLAY_TYPES.length )
		return DISPLAY_TYPES[displayTitle];
	else
		return null;
}
/**
 * Insert the method's description here.
 * Creation date: (1/18/2001 1:39:31 PM)
 * @return int
 * @param displayType java.lang.String
 */ 
public static int getDisplayTitle(String displayTitle) 
{
	for( int i = 0; i < DISPLAY_TITLES.length; i++ )
	{
		if( displayTitle.equalsIgnoreCase(DISPLAY_TITLES[i]) )
			return i;
	}
	
	return UNKNOWN_TYPE_INDEX;
}
/**
 * Insert the method's description here.
 * Creation date: (1/18/2001 1:39:31 PM)
 * @return int
 * @param displayType int
 */
public static String getDisplayType(int displayType) 
{
	if( displayType >= 0 && displayType < DISPLAY_TYPES.length )
		return DISPLAY_TYPES[displayType];
	else
		return null;
}
/**
 * Insert the method's description here.
 * Creation date: (1/18/2001 1:39:31 PM)
 * @return int
 * @param displayType java.lang.String
 */
public static int getDisplayTypeIndexByTitle(String displayTitle) 
{
	for( int i = 0; i < DISPLAY_TYPES.length; i++ )
	{
		if( displayTitle.equalsIgnoreCase(DISPLAY_TYPES[i]) )
			return i;
	}
	
	return UNKNOWN_TYPE_INDEX;
}
/**
 * Insert the method's description here.
 * Creation date: (3/20/2001 10:36:47 AM)
 * @param newDISPLAY_TYPES java.lang.String[]
 */
public static int getDisplayTypeIndexByType(String displayType) 
{
	for( int i = 0; i < DISPLAY_TYPES.length; i++ )
	{
		if( displayType.equalsIgnoreCase(DISPLAY_TYPES[i]) )
			return i;
	}
	
	return UNKNOWN_TYPE_INDEX;
}
/**
 * Insert the method's description here.
 * Creation date: (10/3/00 2:55:08 PM)
 * @return java.lang.String
 */
public java.lang.String getName() {
	return name;
}
/**
 * Insert the method's description here.
 * Creation date: (10/3/00 2:55:08 PM)
 * @return java.lang.String
 */
public java.lang.String getTitle() {
	return title;
}
/**
 * Insert the method's description here.
 * Creation date: (10/3/00 2:55:08 PM)
 * @return java.lang.String
 */
public java.lang.String getType() {
	return type;
}
/**
 * Insert the method's description here.
 * Creation date: (1/24/2002 9:38:17 AM)
 * @param newDisplData DisplayData
 */
public void setDisplayData(DisplayData newDisplData) 
{
	displayData = newDisplData;
}
/**
 * Insert the method's description here.
 * Creation date: (10/3/00 2:55:08 PM)
 * @param newDescription java.lang.String
 */
public void setDescription(java.lang.String newDescription) {
	description = newDescription;
}
/**
 * Allows changes to the titles for client displays.
 * @param displays_
 */ 
public static void setDISPLAY_TITLES( final Display[] displays_ ) 
{
	if( displays_ == null )
		return;

	for( int i = 0; i < displays_.length; i++ )
	{
		try
		{
			if( displays_[i].getDisplayNumber() <= BEGINNING_CLIENT_DISPLAY_NUMBER ) //-1 or less
			{				
				int type = Display.getDisplayTypeIndexByType( displays_[i].getType() );
				
				DISPLAY_TITLES[type] = displays_[i].getTitle();
			}

		}
		catch( Exception e )
		{
			CTILogger.info("*** Unable to set the display title text for display: " +
				displays_[i].getName() + ", using the defulat setting.");
		}  //we really dont care here, but we don not want the app to lock!!
	}
}
/**
 * Insert the method's description here.
 * Creation date: (10/3/00 2:55:08 PM)
 * @param newDisplayNumber long
 */
public void setDisplayNumber(int newDisplayNumber) {
	displayNumber = newDisplayNumber;
}
/**
 * Insert the method's description here.
 * Creation date: (10/3/00 2:55:08 PM)
 * @param newName java.lang.String
 */
public void setName(java.lang.String newName) {
	name = newName;
}
/**
 * Insert the method's description here.
 * Creation date: (10/3/00 2:55:08 PM)
 * @param newTitle java.lang.String
 */
public void setTitle(java.lang.String newTitle) {
	title = newTitle;
}
/**
 * Insert the method's description here.
 * Creation date: (10/3/00 2:55:08 PM)
 * @param newType java.lang.String
 */
public void setType(java.lang.String newType) {
	type = newType;
}
/**
 * Insert the method's description here.
 * Creation date: (10/3/00 3:35:06 PM)
 * @return java.lang.String
 */
public String toString() 
{
	//return getType() + ":" + getName();
	return getName();
}
	/**
	 * @return
	 */
	public ITDCFilter getTdcFilter()
	{
		return tdcFilter;
	}

	/**
	 * @param filter
	 */
	public void setTdcFilter(ITDCFilter filter)
	{
		if( filter == null )
			filter = new DefaultTDCFilter();
		else
			tdcFilter = filter;
	}

}
