/*
 * Created on Feb 2, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.analysis;

import com.cannontech.analysis.tablemodel.ActivityDetailModel;
import com.cannontech.analysis.tablemodel.ActivityModel;
import com.cannontech.analysis.tablemodel.DatabaseModel;
import com.cannontech.analysis.tablemodel.DisconnectModel;
import com.cannontech.analysis.tablemodel.LMControlLogModel;
import com.cannontech.analysis.tablemodel.LoadGroupModel;
import com.cannontech.analysis.tablemodel.MeterReadModel;
import com.cannontech.analysis.tablemodel.PowerFailModel;
import com.cannontech.analysis.tablemodel.ProgramDetailModel;
import com.cannontech.analysis.tablemodel.ReportModelBase;
import com.cannontech.analysis.tablemodel.RouteMacroModel;
import com.cannontech.analysis.tablemodel.StatisticModel;
import com.cannontech.analysis.tablemodel.SystemLogModel;
import com.cannontech.analysis.tablemodel.WorkOrderModel;
import com.cannontech.report.cbc.CapBankListModel;

/**
 * @author snebben
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class ReportTypes
{
	/** enums for types of objects reported on */
	public static final int STATISTIC_DATA = 0;
	public static final int STAT_CARRIER_COMM_DATA = 1;
	public static final int STAT_COMM_CHANNEL_DATA = 2;
	public static final int STAT_DEVICE_COMM_DATA = 3;
	public static final int STAT_TRANS_COMM_DATA = 4;

	public static final int SYSTEM_LOG_DATA = 5;
	public static final int LM_CONTROL_LOG_DATA = 6;
	public static final int LG_ACCOUNTING_DATA = 7;
	
	public static final int MISSED_METER_DATA = 8;
	public static final int CARRIER_DATA = 9;
	public static final int POWER_FAIL_DATA = 10;
	public static final int DISCONNECT_DATA = 11;
	
	public static final int EC_ACTIVITY_LOG_DATA = 12;
	public static final int CARRIER_ROUTE_MACRO_DATA = 13;
	
	public static final int SUCCESS_METER_DATA = 14;
	public static final int LOAD_PROFILE_DATA = 15;
	public static final int EC_ACTIVITY_DETAIL_DATA = 16;
	public static final int PROGRAM_DETAIL_DATA = 17;
	
	public static final int EC_WORK_ORDER_DATA = 18;

	/** Cap Bank Reports */
	public static final int CBC_BANK_DATA = 19;

	
	/** String names for report types */
	public static final String STATISTIC_DATA_STRING = "Communication Statistics";
	public static final String STAT_CARRIER_COMM_DATA_STRING = "Carrier Communication Stats";
	public static final String STAT_COMM_CHANNEL_DATA_STRING = "Comm Channel Communication Stats";
	public static final String STAT_DEVICE_COMM_DATA_STRING = "Device Communication Stats";
	public static final String STAT_TRANS_COMM_DATA_STRING = "Transmitter Communication Stats";

	public static final String SYSTEM_LOG_DATA_STRING = "System Log";
	public static final String LM_CONTROL_LOG_DATA_STRING = "Load Management Log";
	public static final String LG_ACCOUNTING_DATA_STRING = "Load Group Accounting";
	
	public static final String MISSED_METER_DATA_STRING = "Missed Meter Reads";
	public static final String CARRIER_DATA_STRING = "Carrier Data";
	public static final String POWER_FAIL_DATA_STRING = "Power Fail Data";
	public static final String DISCONNECT_DATA_STRING = "Disconnect Data";
	
	public static final String EC_ACTIVITY_LOG_DATA_STRING = "Activity Log - Summary";
	public static final String CARRIER_ROUTE_MACRO_DATA_STRING = "Carrier Route Macro Data";
	
	public static final String SUCCESS_METER_DATA_STRING = "Successful Meter Reads";
	public static final String LOAD_PROFILE_DATA_STRING = "Load Profile Data";
	public static final String EC_ACTIVITY_DETAIL_DATA_STRING = "Activity Log - Detail";
	public static final String PROGRAM_DETAIL_DATA_STRING = "Program Status Detail";

	public static final String EC_WORK_ORDER_DATA_STRING = "Work Order";
	
	public static final String CAPBANK_DATA_STRING = "CapBank Details";

	/** Report String to enum mapping */
	public static final String[] reportName = {
		STATISTIC_DATA_STRING, 
		STAT_CARRIER_COMM_DATA_STRING,
		STAT_COMM_CHANNEL_DATA_STRING,
		STAT_DEVICE_COMM_DATA_STRING,
		STAT_TRANS_COMM_DATA_STRING,
		
		SYSTEM_LOG_DATA_STRING,
		LM_CONTROL_LOG_DATA_STRING,
		LG_ACCOUNTING_DATA_STRING,
	
		MISSED_METER_DATA_STRING,
		CARRIER_DATA_STRING,
		POWER_FAIL_DATA_STRING,
		DISCONNECT_DATA_STRING,
	
		EC_ACTIVITY_LOG_DATA_STRING,
		CARRIER_ROUTE_MACRO_DATA_STRING,
	
		SUCCESS_METER_DATA_STRING,
		LOAD_PROFILE_DATA_STRING,
		EC_ACTIVITY_DETAIL_DATA_STRING,
		PROGRAM_DETAIL_DATA_STRING,
		
		EC_WORK_ORDER_DATA_STRING,
		
		//Capcontrol
		CAPBANK_DATA_STRING
	};


	/* Report groupings */	
	public static final int ADMIN_LOG_REPORTS_GROUP = 0;
	public static final int AMR_REPORTS_GROUP = 1;
	public static final int STATISTICAL_REPORTS_GROUP = 2;
	public static final int LOAD_MANAGEMENT_REPORTS_GROUP = 3;
	public static final int CAP_CONTROL_REPORTS_GROUP = 4;
	public static final int DATABASE_REPORTS_GROUP = 5;
	public static final int STARS_REPORTS_GROUP = 6;
	public static final int OTHER_REPORTS_GROUP = 7;
	
	public static final String ADMIN_LOG_REPORTS_GROUP_STRING = "Administrative Reports";
	public static final String AMR_REPORTS_GROUP_STRING = "AMR Reports";
	public static final String STATISTICAL_REPORTS_GROUP_STRING = "Communication Statistics Reports";
	public static final String LOAD_MANAGEMENT_REPORTS_GROUP_STRING = "Load Management Reports";
	public static final String CAP_CONTROL_REPORTS_GROUP_STRING = "Cap Control Reports";
	public static final String DATABASE_REPORTS_GROUP_STRING = "Database Reports";
	public static final String STARS_REPORTS_GROUP_STRING = "STARS Reports";
	public static final String OTHER_REPORTS_GROUP_STRING = "Miscellaneous Reports";

	/** Report String to enum mapping */
	public static final String[] reportGroupName = {
		ADMIN_LOG_REPORTS_GROUP_STRING,
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
		{EC_ACTIVITY_LOG_DATA, SYSTEM_LOG_DATA},	//admin log reports
		{MISSED_METER_DATA, SUCCESS_METER_DATA, POWER_FAIL_DATA, DISCONNECT_DATA, LOAD_PROFILE_DATA},	//amr reports
		{STAT_CARRIER_COMM_DATA, STAT_COMM_CHANNEL_DATA, STAT_DEVICE_COMM_DATA, STAT_TRANS_COMM_DATA},	//stat reports
		{LM_CONTROL_LOG_DATA, LG_ACCOUNTING_DATA},		//lm reports
		{CBC_BANK_DATA}, //cap control reports
		{CARRIER_DATA}, //database reports
		{EC_ACTIVITY_LOG_DATA, EC_ACTIVITY_DETAIL_DATA, PROGRAM_DETAIL_DATA, EC_WORK_ORDER_DATA},	//stars reports
		{CARRIER_ROUTE_MACRO_DATA}	//other reports
	};

	public static ReportModelBase create(final int reportType)
	{
		switch (reportType)
		{
/* 0 */		case ReportTypes.STATISTIC_DATA:
				return new StatisticModel();
			case ReportTypes.STAT_CARRIER_COMM_DATA:
				return new StatisticModel(ReportTypes.STAT_CARRIER_COMM_DATA);
			case ReportTypes.STAT_COMM_CHANNEL_DATA:
				return new StatisticModel(ReportTypes.STAT_COMM_CHANNEL_DATA);
			case ReportTypes.STAT_DEVICE_COMM_DATA:
				return new StatisticModel(ReportTypes.STAT_DEVICE_COMM_DATA);
			case ReportTypes.STAT_TRANS_COMM_DATA:
				return new StatisticModel(ReportTypes.STAT_TRANS_COMM_DATA);
				
/* 5 */			case ReportTypes.SYSTEM_LOG_DATA:
				return new SystemLogModel();
			case ReportTypes.LM_CONTROL_LOG_DATA:
				return new LMControlLogModel();
			case ReportTypes.LG_ACCOUNTING_DATA:
				return new LoadGroupModel();
/* 8 */		case ReportTypes.MISSED_METER_DATA:
				return new MeterReadModel(reportType);
			case ReportTypes.CARRIER_DATA:
				return new DatabaseModel("CARRIER");
			case ReportTypes.POWER_FAIL_DATA:
				return new PowerFailModel();
			case ReportTypes.DISCONNECT_DATA:
				return new DisconnectModel();
/* 12*/		case ReportTypes.EC_ACTIVITY_LOG_DATA:
				return new ActivityModel();
			case ReportTypes.CARRIER_ROUTE_MACRO_DATA:
				return new RouteMacroModel("CARRIER");
			case ReportTypes.SUCCESS_METER_DATA:
				return new MeterReadModel(reportType);
/* 15*/		case ReportTypes.LOAD_PROFILE_DATA:	//TODO
				return new MeterReadModel();
			case ReportTypes.EC_ACTIVITY_DETAIL_DATA:
				return new ActivityDetailModel();
			case ReportTypes.PROGRAM_DETAIL_DATA:
				return new ProgramDetailModel();				
			case ReportTypes.EC_WORK_ORDER_DATA:
				return new WorkOrderModel();
			case ReportTypes.CBC_BANK_DATA:
				return new CapBankListModel();				

			default:
				return null;
		}		
	}	
/**
 * @return
 */
public static int[][] getGroupToTypeMap()
{
	return groupToTypeMap;
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
