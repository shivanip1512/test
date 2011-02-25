package com.cannontech.analysis.tablemodel;

import com.cannontech.analysis.ReportFilter;

public class StatisticHistoryTransmitterModel extends StatisticHistoryModel
{
	
	/**
	 * Constructor class
	 * @param category_ YukonPaobject.category
	 * @param paoClass_ YukonPaobject.paoClass
	 * @param statPeriodType_ DynamicPaoStatistics.StatisticType
	 */
	public StatisticHistoryTransmitterModel()
	{
		super(StatisticHistoryModel.DAILY_STAT_PERIOD_TYPE_STRING, StatisticHistoryModel.STAT_TRANS_COMM_DATA);
		setFilterModelTypes(new ReportFilter[]{ ReportFilter.TRANSMITTER });
	}
	
	/**
	 * Constructor class
	 * @param category_ YukonPaobject.category
	 * @param paoClass_ YukonPaobject.paoClass
	 * @param statPeriodType_ DynamicPaoStatistics.StatisticType
	 */
	public StatisticHistoryTransmitterModel(String statPeriodType_, int statType_)
	{
		super(statPeriodType_, StatisticHistoryModel.STAT_TRANS_COMM_DATA);
		setFilterModelTypes(new ReportFilter[]{ ReportFilter.TRANSMITTER });
	}
}
