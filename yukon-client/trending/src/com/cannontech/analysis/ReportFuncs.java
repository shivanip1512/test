/*
 * Created on May 20, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.analysis;

import com.cannontech.analysis.report.DatabaseReport;
import com.cannontech.analysis.report.DisconnectReport;
import com.cannontech.analysis.report.EnergyCompanyActivityLogReport;
import com.cannontech.analysis.report.LGAccountingReport;
import com.cannontech.analysis.report.MissedMeterReport;
import com.cannontech.analysis.report.PowerFailReport;
import com.cannontech.analysis.report.RouteMacroReport;
import com.cannontech.analysis.report.StatisticReport;
import com.cannontech.analysis.report.SystemLogReport;
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
				case ReportTypes.STATISTIC_DATA:
					return new StatisticReport((StatisticModel)model);
				
				case ReportTypes.SYSTEM_LOG_DATA:
				case ReportTypes.LM_CONTROL_LOG_DATA:
					return new SystemLogReport((SystemLogModel)model);
					
				case ReportTypes.LG_ACCOUNTING_DATA:
					return new LGAccountingReport((LoadGroupModel)model);
					
				case ReportTypes.MISSED_METER_DATA:
					return new MissedMeterReport();
					
				case ReportTypes.CARRIER_DATA:
					((DatabaseModel)model).setPaoClass("CARRIER");
					return new DatabaseReport((DatabaseModel)model);
					
				case ReportTypes.POWER_FAIL_DATA:
					return new PowerFailReport();
					
				case ReportTypes.DISCONNECT_DATA:
					return new DisconnectReport();
					
				case ReportTypes.ENERGY_COMPANY_ACTIVITY_LOG_DATA:
					return new EnergyCompanyActivityLogReport();
					
				case ReportTypes.CARRIER_ROUTE_MACRO_DATA:
					return new RouteMacroReport("CARRIER");
					
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
