package com.cannontech.tdc.data;

/**
 * Insert the type's description here.
 * Creation date: (10/3/00 2:53:36 PM)
 * @author: 
 */
public class Display
{
	// TDC Display info
	public static final int BEGINNING_CLIENT_DISPLAY_NUMBER = -1;
	public static final int PRECANNED_USER_DISPLAY_NUMBER = 99;
	public static final int BEGINNING_USER_DISPLAY_NUMBER = 100;
	public static final int MAX_DISPLAY_NUMBER = Integer.MAX_VALUE;

	// Special displays that are predefined
	public static final int UNKNOWN_DISPLAY_NUMBER = 0;
	public static final int EVENT_VIEWER_DISPLAY_NUMBER = 1;
	public static final int HISTORY_EVENT_VIEWER_DISPLAY_NUMBER = 2;
	public static final int RAW_POINT_HISTORY_VIEWER_DISPLAY_NUMBER = 3;
	public static final int GLOBAL_ALARM_DISPLAY = 4;
	public static final int LAST_ALARM_DISPLAY = 14;
	public static final int VIEWABLE_ALARM_COUNT = 11;  // ALL_ALARM display plus alarmStateIds 2 - 11 displays

	// All possible display types with their default Title values
	public static final String[] DISPLAY_TYPES =
	{  
		"Alarms and Events",		
		"Custom Displays",
		"Load Management Client",
		"Cap Control Client",
		"Scheduler Client",
      "Static Displays"
		//"Energy Exchange Client"
	};

	//these xxx_TYPE_INDEX variables are a mapping into the DISPLAY_TYPES[]
	public static final int ALARMS_AND_EVENTS_TYPE_INDEX = 0;
	public static final int CUSTOM_DISPLAYS_TYPE_INDEX = 1;	
	public static final int LOAD_CONTROL_CLIENT_TYPE_INDEX = 2;
	public static final int CAP_CONTROL_CLIENT_TYPE_INDEX = 3;
	public static final int SCHEDULER_CLIENT_TYPE_INDEX = 4;
   public static final int STATIC_CLIENT_TYPE_INDEX = 5;
	//public static final int EEXCHANGE_CLIENT_TYPE_INDEX = 5;
	
	public static final int UNKNOWN_TYPE_INDEX = -1;
	public static final int BEGINNING_OPTIONAL_INDEX = 2;	// All possible display types with their default Title values
	private static String[] DISPLAY_TITLES =
	{  
		/* Mandatory displays */
		"Alarms and Events",		
		"Custom Displays",
		
		/* All optional displays go below*/
		"Load Management Client",
		"Cap Control Client",
		"Scheduler Client",
      "Yukon Servers"
		//"Energy Exchange"
	};

	
	// attributes
	private long displayNumber = UNKNOWN_DISPLAY_NUMBER;
	private String name = null;
	private String type = null;
	private String title = null;
	private String description = null;
	private ColumnData[] columnData = null;
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
/**
 * Insert the method's description here.
 * Creation date: (1/24/2002 9:38:17 AM)
 * @return ColumnData[]
 */
public ColumnData[] getColumnData() 
{
	if( columnData == null )
		columnData = new ColumnData[0];

	return columnData;
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
public long getDisplayNumber() {
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
 * Creation date: (8/1/2001 9:39:21 AM)
 * @return boolean
 */
//Returns true if the display type passed it does not
// require a reload for displays from the database
public static boolean needsNoIniting( String type )
{
	return ( type.equalsIgnoreCase(Display.DISPLAY_TYPES[Display.LOAD_CONTROL_CLIENT_TYPE_INDEX]) );
}
/**
 * Insert the method's description here.
 * Creation date: (1/24/2002 9:38:17 AM)
 * @param newColumnData ColumnData[]
 */
public void setColumnData(ColumnData[] newColumnData) 
{
	columnData = newColumnData;
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
 * Insert the method's description here.
 * Creation date: (3/20/2001 10:36:47 AM)
 * @param newDISPLAY_TITLES java.lang.Object[]
 */ 
public static void setDISPLAY_TITLES(Object[] newDISPLAY_TITLES) 
{
	int originalCount = DISPLAY_TITLES.length;

	for( int i = BEGINNING_OPTIONAL_INDEX; i < originalCount; i++ )
	{
		try
		{
			DISPLAY_TITLES[i] = newDISPLAY_TITLES[i-BEGINNING_OPTIONAL_INDEX].toString();
		}
		catch( ArrayIndexOutOfBoundsException e )
		{
			com.cannontech.clientutils.CTILogger.info("*** " + DISPLAY_TITLES[i] + " not found in the database, the client for this display title must not be available.");
		}  //we really dont care here, but we don not want the app to lock!!
	}
}
/**
 * Insert the method's description here.
 * Creation date: (10/3/00 2:55:08 PM)
 * @param newDisplayNumber long
 */
public void setDisplayNumber(long newDisplayNumber) {
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
	return getType() + ":" + getName();
}
}
