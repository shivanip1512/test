package com.cannontech.web.updater;

import java.util.List;
import java.util.Map;

import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.user.YukonUserContext;

public interface BulkUpdateBackingService {
    /**
     * @param identifiers
     * @param afterDate the if-modified-since date, or null if the latest value is desired
     * @param canWait This will be true if the caller can wait for the value. If This is false, the method
     *        should return immediately and simply leave the value out.
     * @return null if unchanged
     */
    Map<UpdateIdentifier, String> getLatestValues(List<UpdateIdentifier> identifiers, long afterDate,
            YukonUserContext userContext, boolean canWait);

    PointValueQualityHolder getCachedValue(int pointId);
}
