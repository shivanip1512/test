package com.cannontech.web.common.service;

import java.util.List;

import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.service.impl.CachedPointDataCorrelationServiceImpl.CorrelationSummary;

public interface CachedPointDataCorrelationService {
    
    /**
     * Correlates data across RPH/Caches/DynamicDispatch and logs results.
     * Logs detail of mismatches.
     */
    void correlateAndLog(int pointId, YukonUserContext userContext);

    /**
     * Correlates data across RPH/Caches/DynamicDispatchs and logs results.
     * Returns and logs details of mismatches.
     */
    List<CorrelationSummary> correlateAndLog(List<Integer> deviceIds, YukonUserContext userContext);
}
