package com.cannontech.analysis.tablemodel;

import com.cannontech.analysis.ReportFilter;

public class StatisticHistoryDeviceModel extends StatisticHistoryModel
{
	
	/**
	 * Constructor class
	 */
	public StatisticHistoryDeviceModel()
	{
		super(StatisticHistoryModel.DAILY_STAT_PERIOD_TYPE_STRING, StatisticHistoryModel.STAT_DEVICE_COMM_DATA);
		setFilterModelTypes(new ReportFilter[]{ ReportFilter.DEVICE });
	}
	
	/**
	 * Constructor class
	 * @param category_ YukonPaobject.category
	 * @param paoClass_ YukonPaobject.paoClass
	 * @param statPeriodType_ DynamicPaoStatistics.StatisticType
	 */
	public StatisticHistoryDeviceModel(String statPeriodType_, int statType_)
	{
		super(statPeriodType_, StatisticHistoryModel.STAT_DEVICE_COMM_DATA);
		setFilterModelTypes(new ReportFilter[]{ ReportFilter.DEVICE });
	}
}
