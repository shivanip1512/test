package com.cannontech.web.common.service;

import java.util.List;

import com.cannontech.user.YukonUserContext;

public interface CachedPointDataCorrelationService {
    
    /**
     * Correlates data across RPH/Caches/DynamicDispatch and logs results.
     * Logs detail of mismatches.
     */
    void correlateAndLog(int pointId, YukonUserContext userContext);

    /**
     * Correlates data across RPH/Caches/DynamicDispatchs and logs results.
     * Returns true if mismatches found.
     * @throws Exception 
     */
    boolean correlateAndLog(List<Integer> deviceIds, YukonUserContext userContext) throws Exception;

    /**
     * Schedules or reschedules device correlation, email with instructions will be sent if mismatches are found.
     */
    void reschedule(Integer initialDelay);
}
