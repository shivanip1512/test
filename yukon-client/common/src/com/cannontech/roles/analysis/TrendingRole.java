package com.cannontech.roles.analysis;

import com.cannontech.roles.AnalysisRoleDefs;

/**
 * @author snebben
 */
public interface TrendingRole {
	public static final int ROLEID = AnalysisRoleDefs.TRENDING_ROLEID;
	
	public static final int TRENDING_DISCLAIMER = AnalysisRoleDefs.TRENDING_PROPERTYID_BASE;
	
	public static final int SCAN_NOW_ENABLED = AnalysisRoleDefs.TRENDING_PROPERTYID_BASE - 1;
	public static final int SCAN_NOW_LABEL = AnalysisRoleDefs.TRENDING_PROPERTYID_BASE - 2;
	public static final int MINIMUM_SCAN_FREQUENCY = AnalysisRoleDefs.TRENDING_PROPERTYID_BASE - 3;
	public static final int MAXIMUM_DAILY_SCANS = AnalysisRoleDefs.TRENDING_PROPERTYID_BASE - 4;

	//Display trending usage (static 5/21/04) pages.	
	public static final int RESET_PEAKS = AnalysisRoleDefs.TRENDING_PROPERTYID_BASE - 5;	

	//Label for the header
	public static final int HEADER_LABEL = AnalysisRoleDefs.TRENDING_PROPERTYID_BASE - 6;
	//Secondary header label, some pages may have a second label to group all trends by
	public static final int HEADER_SECONDARY_LABEL = AnalysisRoleDefs.TRENDING_PROPERTYID_BASE - 7;

	// Allow assingment of trends to users.
	public static final int TREND_ASSIGN = AnalysisRoleDefs.TRENDING_PROPERTYID_BASE - 8;
	public static final int TREND_CREATE = AnalysisRoleDefs.TRENDING_PROPERTYID_BASE - 9;
	public static final int TREND_DELETE = AnalysisRoleDefs.TRENDING_PROPERTYID_BASE - 10;
	public static final int TREND_EDIT = AnalysisRoleDefs.TRENDING_PROPERTYID_BASE - 11;
	
	//Enable the options link for settings additional display configurations.
	public static final int OPTIONS_BUTTON_ENABLED = AnalysisRoleDefs.TRENDING_PROPERTYID_BASE - 12;
	public static final int EXPORT_PRINT_BUTTON_ENABLED = AnalysisRoleDefs.TRENDING_PROPERTYID_BASE - 13;
	public static final int VIEW_BUTTON_ENABLED = AnalysisRoleDefs.TRENDING_PROPERTYID_BASE - 14;

	public static final int EXPORT_PRINT_BUTTON_LABEL = AnalysisRoleDefs.TRENDING_PROPERTYID_BASE - 15;
	public static final int VIEW_TYPE_BUTTON_LABEL = AnalysisRoleDefs.TRENDING_PROPERTYID_BASE - 16;
	
	//Display trending usage (static 5/21/04) pages.	
	public static final int TRENDING_USAGE = AnalysisRoleDefs.TRENDING_PROPERTYID_BASE - 17;
	
	//Number of days from today (0) to default the start date to.  Used mainly for when data is imported today for yesterday.
	public static final int DEFAULT_START_DATE_DAYS = AnalysisRoleDefs.TRENDING_PROPERTYID_BASE - 18;
	
	//Default time period.
	public static final int DEFAULT_TIME_PERIOD = AnalysisRoleDefs.TRENDING_PROPERTYID_BASE - 19;
}

