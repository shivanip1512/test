package com.cannontech.dr.ecobee.model;

import java.util.HashMap;
import java.util.Map;

import com.cannontech.dr.ecobee.dao.EcobeeQueryCount;
import com.cannontech.dr.ecobee.dao.EcobeeQueryType;

/**
 * Object containing query statistics for a single month of Ecobee communications.
 */
public final class EcobeeQueryStatistics implements Comparable<EcobeeQueryStatistics> {
    
    private final int month;
    private final int year;
    private final Map<EcobeeQueryType, EcobeeQueryCount> queryCounts = new HashMap<>();
    
    public EcobeeQueryStatistics(int year, int month) {
        this.year = year;
        this.month = month;
    }
    
    public void addQueryCount(EcobeeQueryCount queryCount) {
        queryCounts.put(queryCount.getType(), queryCount);
    }
    
    public int getMonth() {
        return month;
    }
    
    public int getYear() {
        return year;
    }
    
    public int getQueryCountByType(EcobeeQueryType queryType) {
        EcobeeQueryCount count = queryCounts.get(queryType);
        return count == null ? 0 : count.getCount();
    }
    
    /**
     * Orders by year and month, from least recent to most recent.
     * Query counts are ignored.
     */
    @Override
    public int compareTo(EcobeeQueryStatistics other) {
        int yearDifference = year - other.year;
        if(yearDifference != 0) {
            return yearDifference;
        }
        return month - other.month;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + month;
        result = prime * result + year;
        return result;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        EcobeeQueryStatistics other = (EcobeeQueryStatistics) obj;
        if (month != other.month)
            return false;
        if (year != other.year)
            return false;
        return true;
    }
}
