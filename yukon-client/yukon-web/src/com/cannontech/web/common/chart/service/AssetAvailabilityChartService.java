package com.cannontech.web.common.chart.service;

import net.sf.json.JSONObject;

import com.cannontech.dr.assetavailability.SimpleAssetAvailabilitySummary;
import com.cannontech.user.YukonUserContext;

public interface AssetAvailabilityChartService {
    
    /**
     * This method is used to get the color pie json data for a SimpleAssetAvailabilitySummary.
     * 
     * @return JSONObject
     */
    JSONObject getJSONPieData(SimpleAssetAvailabilitySummary aaSummary, YukonUserContext userContext);
    
}
