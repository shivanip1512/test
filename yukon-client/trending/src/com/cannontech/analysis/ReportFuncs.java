/*
 * Created on May 20, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.analysis;

import com.cannontech.analysis.report.DatabaseReport;
import com.cannontech.analysis.report.DisconnectReport;
import com.cannontech.analysis.report.LGAccountingReport;
import com.cannontech.analysis.report.LMControlLogReport;
import com.cannontech.analysis.report.MissedMeterReport;
import com.cannontech.analysis.report.PowerFailReport;
import com.cannontech.analysis.report.StatisticReport;
import com.cannontech.analysis.report.YukonReportBase;
import com.cannontech.analysis.tablemodel.StatisticModel;
import com.cannontech.analysis.tablemodel.SystemLogModel;

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
		switch (reportType)
		{
			case ReportTypes.CARRIER_COMM_DATA:
				return new StatisticReport(new StatisticModel(reportType));
			case ReportTypes.COMM_CHANNEL_DATA:
				return new StatisticReport(new StatisticModel(reportType));
			case ReportTypes.DEVICE_COMM_DATA:
				return new StatisticReport(new StatisticModel(reportType));
			case ReportTypes.TRANS_COMM_DATA:
				return new StatisticReport(new StatisticModel(reportType));
			case ReportTypes.SYSTEM_LOG_DATA:
				return new LMControlLogReport(new SystemLogModel(reportType));
			case ReportTypes.LM_CONTROL_LOG_DATA:
				return new LMControlLogReport(new SystemLogModel(reportType));
			case ReportTypes.LG_ACCOUNTING_DATA:
				return new LGAccountingReport();
			case ReportTypes.MISSED_METER_DATA:
				return new MissedMeterReport();
			case ReportTypes.CARRIER_DATA:
				return new DatabaseReport();
			case ReportTypes.POWER_FAIL_DATA:
				return new PowerFailReport();
			case ReportTypes.DISCONNECT_DATA:
				return new DisconnectReport();
			default:
				return null;
		}		
	}
}
