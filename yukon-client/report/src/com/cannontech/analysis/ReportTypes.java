/*
 * Created on Feb 2, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.analysis;


/**
 * @author snebben
 *
 * ReportTypes represent a Report in com.cannontech.analysis.report package.
 * (Not to be confused with representing a "tableModel".  They represent a "Report"!)
 */
public class ReportTypes
{
	/** enums for types of objects reported on */
	public static final int NONE = -1;
	public static final int STATISTIC_DATA = 0;

	public static final int SYSTEM_LOG_DATA = 1;
	public static final int LM_CONTROL_LOG_DATA = 2;
	public static final int LG_ACCOUNTING_DATA = 3;
	public static final int LM_DAILY_PEAKS_DATA = 4;
	
	public static final int METER_READ_DATA = 5;
	public static final int CARRIER_DB_DATA = 6;
	public static final int POWER_FAIL_DATA = 7;
	public static final int DISCONNECT_METER_DATA = 8;
	public static final int CARRIER_ROUTE_MACRO_DATA = 9;
	public static final int LOAD_PROFILE_DATA = 10;
	
	public static final int EC_ACTIVITY_LOG_DATA = 11;
	public static final int EC_ACTIVITY_DETAIL_DATA = 12;
	public static final int PROGRAM_DETAIL_DATA = 13;
	
	public static final int EC_WORK_ORDER_DATA = 14;

	/** Cap Bank Reports */
	public static final int CBC_BANK_DATA = 15;

	public static final int POINT_DATA_INTERVAL_DATA = 16;	//Coincidental
	public static final int POINT_DATA_SUMMARY_DATA = 17;	//Peaks/Usage
	
	/** String names for report types */
	public static final String STATISTIC_DATA_STRING = "Communication Statistics";

	public static final String SYSTEM_LOG_DATA_STRING = "System Log";
	public static final String LM_CONTROL_LOG_DATA_STRING = "Load Management Log";
	public static final String LG_ACCOUNTING_DATA_STRING = "Load Group Accounting";
	public static final String LM_DAILY_PEAKS_STRING = "LM Daily Peaks";
	
	public static final String METER_DATA_STRING = "Meter Reads";
	public static final String CARRIER_DB_DATA_STRING = "Carrier Data";
	public static final String POWER_FAIL_DATA_STRING = "Power Fail Data";
	public static final String DISCONNECT_METER_DATA_STRING = "Disconnect Data";
	public static final String CARRIER_ROUTE_MACRO_DATA_STRING = "Carrier Route Macro Data";
	public static final String LOAD_PROFILE_DATA_STRING = "Load Profile Data";

	public static final String EC_ACTIVITY_LOG_DATA_STRING = "Activity Log - Summary";
	public static final String EC_ACTIVITY_DETAIL_DATA_STRING = "Activity Log - Detail";
	public static final String PROGRAM_DETAIL_DATA_STRING = "Program Status Detail";

	public static final String EC_WORK_ORDER_DATA_STRING = "Work Order";
	
	public static final String CAPBANK_DATA_STRING = "CapBank Details";
	
	public static final String POINT_DATA_INTERVAL_DATA_STRING = "PointData Interval";
	public static final String POINT_DATA_SUMMARY_DATA_STRING = "PointData Summary";

	/** Report String to enum mapping */
	public static final String[] reportName = {
		STATISTIC_DATA_STRING, 
		
		SYSTEM_LOG_DATA_STRING,
		LM_CONTROL_LOG_DATA_STRING,
		LG_ACCOUNTING_DATA_STRING,
		LM_DAILY_PEAKS_STRING,
	
		METER_DATA_STRING,
		CARRIER_DB_DATA_STRING,
		POWER_FAIL_DATA_STRING,
		DISCONNECT_METER_DATA_STRING,
		CARRIER_ROUTE_MACRO_DATA_STRING,
		LOAD_PROFILE_DATA_STRING,

		EC_ACTIVITY_LOG_DATA_STRING,
		EC_ACTIVITY_DETAIL_DATA_STRING,
		PROGRAM_DETAIL_DATA_STRING,
		
		EC_WORK_ORDER_DATA_STRING,
		
		//Capcontrol
		CAPBANK_DATA_STRING,
		
		POINT_DATA_INTERVAL_DATA_STRING,
		POINT_DATA_SUMMARY_DATA_STRING
	};


	/* Report groupings */	
	public static final int ADMIN_REPORTS_GROUP = 0;
	public static final int AMR_REPORTS_GROUP = 1;
	public static final int STATISTICAL_REPORTS_GROUP = 2;
	public static final int LOAD_MANAGEMENT_REPORTS_GROUP = 3;
	public static final int CAP_CONTROL_REPORTS_GROUP = 4;
	public static final int DATABASE_REPORTS_GROUP = 5;
	public static final int STARS_REPORTS_GROUP = 6;
	public static final int OTHER_REPORTS_GROUP = 7;
	
	public static final String ADMIN_REPORTS_GROUP_STRING = "Administrative";
	public static final String AMR_REPORTS_GROUP_STRING = "AMR";
	public static final String STATISTICAL_REPORTS_GROUP_STRING = "Communication";
	public static final String LOAD_MANAGEMENT_REPORTS_GROUP_STRING = "Load Management";
	public static final String CAP_CONTROL_REPORTS_GROUP_STRING = "Capacitor Control";
	public static final String DATABASE_REPORTS_GROUP_STRING = "Database";
	public static final String STARS_REPORTS_GROUP_STRING = "STARS";
	public static final String OTHER_REPORTS_GROUP_STRING = "Miscellaneous";

	/** Report String to enum mapping */
	public static final String[] reportGroupName = {
		ADMIN_REPORTS_GROUP_STRING,
		AMR_REPORTS_GROUP_STRING,
		STATISTICAL_REPORTS_GROUP_STRING,
		LOAD_MANAGEMENT_REPORTS_GROUP_STRING,
		CAP_CONTROL_REPORTS_GROUP_STRING,
		DATABASE_REPORTS_GROUP_STRING,
		STARS_REPORTS_GROUP_STRING,
		OTHER_REPORTS_GROUP_STRING
	};
	
//	[groupID][typeID array]
// xxx_reports_group may appear in multiple mappings
	private static int[][] groupToTypeMap = {
		{POINT_DATA_INTERVAL_DATA, POINT_DATA_SUMMARY_DATA, 
				EC_ACTIVITY_LOG_DATA, SYSTEM_LOG_DATA},	//archive data, admin log reports
		{METER_READ_DATA, POWER_FAIL_DATA, DISCONNECT_METER_DATA, LOAD_PROFILE_DATA},	//amr reports
		{STATISTIC_DATA},	//stat reports
		{LM_CONTROL_LOG_DATA, LG_ACCOUNTING_DATA, LM_DAILY_PEAKS_DATA},		//lm reports
		{CBC_BANK_DATA}, //cap control reports
		{CARRIER_DB_DATA, CARRIER_ROUTE_MACRO_DATA}, //database reports
		{EC_ACTIVITY_LOG_DATA, EC_ACTIVITY_DETAIL_DATA, PROGRAM_DETAIL_DATA, EC_WORK_ORDER_DATA},	//stars reports
		{NONE}	//other reports
	};

public static int getReportGroupType(int reportType)
{
	for (int i = 0; i < groupToTypeMap.length; i++)
	{
		for (int j = 0; j < groupToTypeMap[i].length; j++)
		{
			if( groupToTypeMap[i][j] == reportType)
			{
				return i;
			}
		}
	}
	return -1;	//unknown
}
/**
 * @return
 */
public static int[][] getGroupToTypeMap()
{
	return groupToTypeMap;
}
public static int[] getGroupTypes(int groupID)
{
	return groupToTypeMap[groupID];
}
public static String getReportName(int reportType)
{
	return reportName[reportType];
}

public static String getReportGroupName(int reportGroupType)
{
	return reportGroupName[reportGroupType];
}
}
