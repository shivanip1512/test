package com.cannontech.web.support.dao;

import com.cannontech.common.pao.PaoType;

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
    
    /**
     * Get the total number of RF DA devices in the system.
     * @see PaoType.getRfDaTypes
     */
    int getRfDaCount();
    
    /**
     * Get the total number of RF Gateway devices in the system.
     * @see PaoType.getRfGatewayTypes
     */
    int getRfGatewayCount();
    
}
