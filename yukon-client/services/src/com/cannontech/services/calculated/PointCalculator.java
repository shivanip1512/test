package com.cannontech.services.calculated;

import java.util.List;

import com.cannontech.amr.rfn.model.CalculationData;
import com.cannontech.common.pao.definition.model.PaoTypePointIdentifier;
import com.cannontech.messaging.message.dispatch.PointDataMessage;
import com.google.common.cache.Cache;

public interface PointCalculator {
    
    public void calculate(Cache<CacheKey, CacheValue> recentReadings, CalculationData data, List<? super PointDataMessage> pointData);
    
    public boolean supports(PaoTypePointIdentifier ptpi);
    
}