/*
 * Created on May 20, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.analysis;

import com.cannontech.analysis.report.DatabaseReport;
import com.cannontech.analysis.report.DisconnectReport;
import com.cannontech.analysis.report.ECActivityDetailReport;
import com.cannontech.analysis.report.ECActivityLogReport;
import com.cannontech.analysis.report.LGAccountingReport;
import com.cannontech.analysis.report.MissedMeterReport;
import com.cannontech.analysis.report.PowerFailReport;
import com.cannontech.analysis.report.ProgramDetailReport;
import com.cannontech.analysis.report.RouteMacroReport;
import com.cannontech.analysis.report.StatisticReport;
import com.cannontech.analysis.report.SystemLogReport;
import com.cannontech.analysis.report.WorkOrder;
import com.cannontech.analysis.report.YukonReportBase;
import com.cannontech.analysis.tablemodel.DatabaseModel;
import com.cannontech.analysis.tablemodel.LoadGroupModel;
import com.cannontech.analysis.tablemodel.ReportModelBase;
import com.cannontech.analysis.tablemodel.StatisticModel;
import com.cannontech.analysis.tablemodel.SystemLogModel;
import com.cannontech.clientutils.CTILogger;

/**
 * @author snebben
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class ReportFuncs
{
	public static YukonReportBase createYukonReport(final int reportType)
	{
		ReportModelBase model = ReportTypes.create(reportType);
		try
		{
			switch (reportType)
			{
/* 0 */			case ReportTypes.STATISTIC_DATA:
				case ReportTypes.STAT_CARRIER_COMM_DATA:
				case ReportTypes.STAT_COMM_CHANNEL_DATA:
				case ReportTypes.STAT_DEVICE_COMM_DATA:
				case ReportTypes.STAT_TRANS_COMM_DATA:
					return new StatisticReport((StatisticModel)model);
				
/* 5 */			case ReportTypes.SYSTEM_LOG_DATA:
				case ReportTypes.LM_CONTROL_LOG_DATA:
					return new SystemLogReport((SystemLogModel)model);
					
				case ReportTypes.LG_ACCOUNTING_DATA:
					return new LGAccountingReport((LoadGroupModel)model);
					
/* 8 */			case ReportTypes.MISSED_METER_DATA:
					return new MissedMeterReport();
					
				case ReportTypes.CARRIER_DATA:
					((DatabaseModel)model).setPaoClass("CARRIER");
					return new DatabaseReport((DatabaseModel)model);
					
				case ReportTypes.POWER_FAIL_DATA:
					return new PowerFailReport();
					
				case ReportTypes.DISCONNECT_DATA:
					return new DisconnectReport();
					
/* 12 */			case ReportTypes.EC_ACTIVITY_LOG_DATA:
					return new ECActivityLogReport();

				case ReportTypes.CARRIER_ROUTE_MACRO_DATA:
					return new RouteMacroReport("CARRIER");

				case ReportTypes.SUCCESS_METER_DATA:	//TODO verify this is the right report for successful reads
					return new MissedMeterReport();

/* 15*/			case ReportTypes.LOAD_PROFILE_DATA:	//TODO This is not the correct report to create!
					return new MissedMeterReport();

				case ReportTypes.EC_ACTIVITY_DETAIL_DATA:
					return new ECActivityDetailReport();
					
				case ReportTypes.PROGRAM_DETAIL_DATA:
					return new ProgramDetailReport();
				
				case ReportTypes.EC_WORK_ORDER_DATA:
					return new WorkOrder();
				
				default:
					return null;
			}
		}
		catch (ClassCastException e)
		{
			CTILogger.info("Invalid model ("+model.getClass().toString() + ") instantiated for Report, Please check the case statement evaluations.");
			return null;
		}
	}
 }
