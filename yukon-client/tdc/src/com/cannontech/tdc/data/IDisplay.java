package com.cannontech.tdc.data;

/**
 * @author rneuharth
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public interface IDisplay
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
	public static final int LAST_ALARM_DISPLAY = 35;
	public static final int VIEWABLE_ALARM_COUNT = 11;  // ALL_ALARM display plus alarmStateIds 2 - 11 displays


	public static final int SOE_LOG_DISPLAY_NUMBER = 50;
	public static final int TAG_LOG_DISPLAY_NUMBER = 51;


	//these xxx_TYPE_INDEX variables are a mapping into the DISPLAY_TYPES[]
	public static final int ALARMS_AND_EVENTS_TYPE_INDEX = 0;
	public static final int CUSTOM_DISPLAYS_TYPE_INDEX = 1;	
	public static final int LOAD_CONTROL_CLIENT_TYPE_INDEX = 2;
	public static final int CAP_CONTROL_CLIENT_TYPE_INDEX = 3;
	public static final int SCHEDULER_CLIENT_TYPE_INDEX = 4;
	public static final int STATIC_CLIENT_TYPE_INDEX = 5;
	//public static final int EEXCHANGE_CLIENT_TYPE_INDEX = 5;
	
	public static final int UNKNOWN_TYPE_INDEX = -1;


	// All possible display types with their default Title values
	public static final String[] DISPLAY_TYPES =
	{  
		/* Mandatory displays */
		"Alarms and Events",		
		"Custom Displays",

		/* All optional displays go below*/
		"Load Management Client",
		"Cap Control Client",
		"Scheduler Client",
		"Static Displays"
		//"Energy Exchange Client"
	};

	public static String[] DISPLAY_TITLES =
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


}
