package com.cannontech.web.support.dao;

/**
 * Dao to retrieve any database information needed by system metric criteria to determine the status of a system metric.
 */
public interface SystemMetricCriteriaDao {
    
    /**
     * Get the total number of RF meters in the system.
     * @see PaoType.getRfMeterTypes
     */
    public int getRfnMeterCount();
    
    /**
     * Get the total number of RF LCRs in the system.
     * @see PaoType.getRfLcrTypes
     */
    public int getRfnLcrCount();
    
}
