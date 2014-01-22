package com.cannontech.web.common.chart.service;

import java.util.Map;

import com.cannontech.dr.assetavailability.SimpleAssetAvailabilitySummary;
import com.cannontech.user.YukonUserContext;

public interface AssetAvailabilityChartService {
    
    /**
     * This method is used to get the color pie json data for a SimpleAssetAvailabilitySummary.
     */
    Map<String, Object> getJsonPieData(SimpleAssetAvailabilitySummary aaSummary, YukonUserContext userContext);
    
}
