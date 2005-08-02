package com.cannontech.roles.application;

import com.cannontech.roles.*;

/**
 * @author aaron
 */
public interface TrendingRole {
	public static final int ROLEID = ApplicationRoleDefs.TRENDING_ROLEID;
	
	public static final int GRAPH_EDIT_GRAPHDEFINITION = ApplicationRoleDefs.TRENDING_PROPERTYID_BASE;
//	public static final int LOG_LEVEL = ApplicationRoleDefs.TRENDING_PROPERTYID_BASE - 1;

	public static final int TRENDING_DISCLAIMER = ApplicationRoleDefs.TRENDING_PROPERTYID_BASE - 2;

	public static final int SCAN_NOW_ENABLED = ApplicationRoleDefs.TRENDING_PROPERTYID_BASE - 3;
	public static final int SCAN_NOW_LABEL = ApplicationRoleDefs.TRENDING_PROPERTYID_BASE - 4;
	public static final int MINIMUM_SCAN_FREQUENCY = ApplicationRoleDefs.TRENDING_PROPERTYID_BASE - 5;
	public static final int MAXIMUM_DAILY_SCANS = ApplicationRoleDefs.TRENDING_PROPERTYID_BASE - 6;

	//Display trending usage (static 5/21/04) pages.	
	public static final int RESET_PEAKS = ApplicationRoleDefs.TRENDING_PROPERTYID_BASE - 7;	

	//Label for the header
	public static final int HEADER_LABEL = ApplicationRoleDefs.TRENDING_PROPERTYID_BASE - 8;
	//Secondary header label, some pages may have a second label to group all trends by
	public static final int HEADER_SECONDARY_LABEL = ApplicationRoleDefs.TRENDING_PROPERTYID_BASE - 9;

	// Allow assingment of trends to users.
	public static final int TREND_ASSIGN = ApplicationRoleDefs.TRENDING_PROPERTYID_BASE - 10;
	public static final int TREND_CREATE = ApplicationRoleDefs.TRENDING_PROPERTYID_BASE - 11;
	public static final int TREND_DELETE = ApplicationRoleDefs.TRENDING_PROPERTYID_BASE - 12;
	public static final int TREND_EDIT = ApplicationRoleDefs.TRENDING_PROPERTYID_BASE - 13;

	//Enable the options link for settings additional display configurations.
	public static final int OPTIONS_BUTTON_ENABLED = ApplicationRoleDefs.TRENDING_PROPERTYID_BASE - 14;
	public static final int EXPORT_PRINT_BUTTON_ENABLED = ApplicationRoleDefs.TRENDING_PROPERTYID_BASE - 15;
	public static final int VIEW_BUTTON_ENABLED = ApplicationRoleDefs.TRENDING_PROPERTYID_BASE - 16;

	public static final int EXPORT_PRINT_BUTTON_LABEL = ApplicationRoleDefs.TRENDING_PROPERTYID_BASE - 17;
	public static final int VIEW_TYPE_BUTTON_LABEL = ApplicationRoleDefs.TRENDING_PROPERTYID_BASE - 18;

	//Display trending usage (static 5/21/04) pages.	
	public static final int TRENDING_USAGE = ApplicationRoleDefs.TRENDING_PROPERTYID_BASE - 19;

	//Number of days from today (0) to default the start date to.  Used mainly for when data is imported today for yesterday.
	public static final int DEFAULT_START_DATE_DAYS = ApplicationRoleDefs.TRENDING_PROPERTYID_BASE - 20;

	//Default time period.
	public static final int DEFAULT_TIME_PERIOD = ApplicationRoleDefs.TRENDING_PROPERTYID_BASE - 21;
}
