/*
 * Created on Feb 2, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.analysis;

import com.cannontech.analysis.tablemodel.ActivityModel;
import com.cannontech.analysis.tablemodel.DatabaseModel;
import com.cannontech.analysis.tablemodel.DisconnectModel;
import com.cannontech.analysis.tablemodel.LMControlLogModel;
import com.cannontech.analysis.tablemodel.LoadGroupModel;
import com.cannontech.analysis.tablemodel.MeterReadModel;
import com.cannontech.analysis.tablemodel.PowerFailModel;
import com.cannontech.analysis.tablemodel.ReportModelBase;
import com.cannontech.analysis.tablemodel.RouteMacroModel;
import com.cannontech.analysis.tablemodel.StatisticModel;
import com.cannontech.analysis.tablemodel.SystemLogModel;

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

	public static final int SYSTEM_LOG_DATA = 1;
	public static final int LM_CONTROL_LOG_DATA = 2;
	public static final int LG_ACCOUNTING_DATA = 3;
	
	public static final int MISSED_METER_DATA = 4;
	public static final int CARRIER_DATA = 5;
	public static final int POWER_FAIL_DATA = 6;
	public static final int DISCONNECT_DATA = 7;
	
	public static final int ENERGY_COMPANY_ACTIVITY_LOG_DATA = 8;
	public static final int CARRIER_ROUTE_MACRO_DATA = 9;
	
	public static final int SUCCESS_METER_DATA = 10;
	public static final int LOAD_PROFILE_DATA = 11;
	
	public static final int ADMIN_LOG_REPORTS_GROUP = 0;
	public static final int AMR_REPORTS_GROUP = 1;
	public static final int STATISTICAL_REPORTS_GROUP = 2;
	public static final int LOAD_MANAGEMENT_REPORTS_GROUP = 3;
	public static final int CAP_CONTROL_REPORTS_GROUP = 4;
	public static final int DATABASE_REPORTS_GROUP = 5;
	public static final int STARS_REPORTS_GROUP = 6;
	public static final int OTHER_REPORTS_GROUP = 7;
	
//	[groupID][typeID array]
// xxx_reports_group may appear in multiple mappings
	private static int[][] groupToTypeMap = {
		{ENERGY_COMPANY_ACTIVITY_LOG_DATA, SYSTEM_LOG_DATA},	//admin log reports
		{MISSED_METER_DATA, SUCCESS_METER_DATA, POWER_FAIL_DATA, DISCONNECT_DATA, LOAD_PROFILE_DATA},	//amr reports
		{STATISTIC_DATA},	//stat reports
		{LM_CONTROL_LOG_DATA, LG_ACCOUNTING_DATA},		//lm reports
		{},		//cap control reports
		{CARRIER_DATA}, //database reports
		{ENERGY_COMPANY_ACTIVITY_LOG_DATA },	//stars reports
		{CARRIER_ROUTE_MACRO_DATA}	//other reports
	};

	public static ReportModelBase create(final int reportType)
	{
		switch (reportType)
		{
/* 0 */		case ReportTypes.STATISTIC_DATA:
				return new StatisticModel();
			case ReportTypes.SYSTEM_LOG_DATA:
				return new SystemLogModel();
			case ReportTypes.LM_CONTROL_LOG_DATA:
				return new LMControlLogModel();
			case ReportTypes.LG_ACCOUNTING_DATA:
				return new LoadGroupModel();
/* 4 */		case ReportTypes.MISSED_METER_DATA:
				return new MeterReadModel(reportType);
			case ReportTypes.CARRIER_DATA:
				return new DatabaseModel("CARRIER");
			case ReportTypes.POWER_FAIL_DATA:
				return new PowerFailModel();
			case ReportTypes.DISCONNECT_DATA:
				return new DisconnectModel();
/* 8*/		case ReportTypes.ENERGY_COMPANY_ACTIVITY_LOG_DATA:
				return new ActivityModel();
			case ReportTypes.CARRIER_ROUTE_MACRO_DATA:
				return new RouteMacroModel("CARRIER");
			case ReportTypes.SUCCESS_METER_DATA:
				return new MeterReadModel(reportType);
/* 11*/		case ReportTypes.LOAD_PROFILE_DATA:	//TODO
				return new MeterReadModel();
			default:
				return null;
		}		
	}	
}
