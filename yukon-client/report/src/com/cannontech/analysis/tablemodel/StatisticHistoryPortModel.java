package com.cannontech.analysis.tablemodel;

import com.cannontech.analysis.ReportFilter;


public class StatisticHistoryPortModel extends StatisticHistoryModel
{
	
	/**
	 * Constructor class
	 * @param category_ YukonPaobject.category
	 * @param paoClass_ YukonPaobject.paoClass
	 * @param statPeriodType_ DynamicPaoStatistics.StatisticType
	 */
	public StatisticHistoryPortModel()
	{
		super(StatisticHistoryModel.DAILY_STAT_PERIOD_TYPE_STRING, StatisticHistoryModel.STAT_COMM_CHANNEL_DATA);
		setFilterModelTypes(new ReportFilter[]{ ReportFilter.PORT });
	}
	
	/**
	 * Constructor class
	 * @param category_ YukonPaobject.category
	 * @param paoClass_ YukonPaobject.paoClass
	 * @param statPeriodType_ DynamicPaoStatistics.StatisticType
	 */
	public StatisticHistoryPortModel(String statPeriodType_, int statType_)
	{
		super(statPeriodType_, StatisticHistoryModel.STAT_COMM_CHANNEL_DATA);
		setFilterModelTypes(new ReportFilter[]{ ReportFilter.PORT });
	}
}
