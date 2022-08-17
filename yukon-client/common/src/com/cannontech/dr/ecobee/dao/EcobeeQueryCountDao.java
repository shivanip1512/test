package com.cannontech.dr.ecobee.dao;

import java.util.List;

import com.cannontech.common.util.MonthYear;
import com.cannontech.common.util.Range;
import com.cannontech.dr.ecobee.model.EcobeeQueryStatistics;

/**
 * Dao for retrieving Ecobee query statistics (e.g. how many queries Yukon has made against the Ecobee API, and of what
 * types.) Query statistics are tracked by month, and separately for each energy company.
 */
public interface EcobeeQueryCountDao {

    /**
     * Increments the query count by one for the specified query type and energy company;
     */
    public void incrementQueryCount(EcobeeQueryType queryType);

    /**
     * Gets a statistics object containing counts for all query types for the specified month and energy company.
     */
    public EcobeeQueryStatistics getCountsForMonth(MonthYear monthYear);

    /**
     * Gets a statistics object containing counts for all query types for the specified range of months and energy
     * company.
     */
    public List<EcobeeQueryStatistics> getCountsForRange(Range<MonthYear> dateRange);

}
