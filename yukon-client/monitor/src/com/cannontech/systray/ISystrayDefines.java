package com.cannontech.systray;

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
	public static final String EXEC_TDC = "cmd /C java -jar d:/yukon/client/bin/tdc.jar";
	public static final String EXEC_DBEDITOR = "cmd /C java -jar d:/yukon/client/bin/dbeditor.jar";
	public static final String EXEC_COMMANDER = "cmd /C java -jar d:/yukon/client/bin/yc.jar";
	public static final String EXEC_TRENDING = "cmd /C java -jar d:/yukon/client/bin/graph.jar";

/*
	public static final String EXEC_TDC = "cmd /C java -jar tdc.jar";
	public static final String EXEC_DBEDITOR = "cmd /C java -jar dbeditor.jar";
	public static final String EXEC_COMMANDER = "cmd /C java -jar yc.jar";
	public static final String EXEC_TRENDING = "cmd /C java -jar graph.jar";
*/
}
