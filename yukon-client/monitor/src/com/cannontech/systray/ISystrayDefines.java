package com.cannontech.systray;

import com.cannontech.common.util.CtiUtilities;

import snoozesoft.systray4j.SysTrayMenuIcon;

/**
 * @author rneuharth
 *
 * A place to define constants for the systray projects
 *
 */
public interface ISystrayDefines
{

	public static final String MSG_STARTING		= "Starting Yukon Monitor...";

	public static final String MSG_NOT_CONN		= "Not Connected to Server";
	public static final String MSG_ALRM_TOTALS	= "Alarm Totals : ";



	//command line execs for apps
	public static final String EXEC_TDC = "cmd /C java -jar " + CtiUtilities.USER_DIR + "tdc.jar";
	public static final String EXEC_DBEDITOR = "cmd /C java -jar " + CtiUtilities.USER_DIR + "dbeditor.jar";
	public static final String EXEC_COMMANDER = "cmd /C java -jar " + CtiUtilities.USER_DIR + "yc.jar";
	public static final String EXEC_TRENDING = "cmd /C java -jar " + CtiUtilities.USER_DIR + "graph.jar";

/*
	//used for testing
	public static final String EXEC_TDC = "cmd /C java -jar d:/yukon/client/bin/tdc.jar";
	public static final String EXEC_DBEDITOR = "cmd /C java -jar d:/yukon/client/bin/dbeditor.jar";
	public static final String EXEC_COMMANDER = "cmd /C java -jar d:/yukon/client/bin/yc.jar";
	public static final String EXEC_TRENDING = "cmd /C java -jar d:/yukon/client/bin/graph.jar";
*/

	//indexes to the icons array that have meaning
	public static final int ICO_NO_ALRM = 0;
	public static final int ICO_DISCON = 1;
	public static final int ICO_NO_LOG = 2;
	public static final int ICO_ANIME_START = 3;


	// create icons
	// the extension can be omitted for icons
	public static final SysTrayMenuIcon[] ALL_ICONS = 
	{
		
		//default state
		new SysTrayMenuIcon( 
		        ISystrayDefines.class.getResource("/YukonNoAlarm" + SysTrayMenuIcon.getExtension()) ),
	
		new SysTrayMenuIcon( 
                ISystrayDefines.class.getResource("/YukonDiscon" + SysTrayMenuIcon.getExtension()) ),
	
		new SysTrayMenuIcon( 
                ISystrayDefines.class.getResource("/YukonNoLog" + SysTrayMenuIcon.getExtension()) ),
	
	
		//all animated icons go below here
		new SysTrayMenuIcon( 
                ISystrayDefines.class.getResource("/YukonAlarm1" + SysTrayMenuIcon.getExtension()) ),
	
		new SysTrayMenuIcon(
		        ISystrayDefines.class.getResource("/YukonAlarm2" + SysTrayMenuIcon.getExtension()) ),
	};

}
