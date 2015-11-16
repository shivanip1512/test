package com.cannontech.web.common.chart.service.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.dr.assetavailability.AssetAvailabilitySummary;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.chart.model.FlotPieDatas;
import com.cannontech.web.common.chart.service.AssetAvailabilityChartService;
import com.cannontech.web.common.chart.service.FlotChartService;
import com.google.common.collect.Maps;

public class AssetAvailabilityChartServiceImpl implements AssetAvailabilityChartService {

    @Autowired private FlotChartService flotChartService;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;

    @Override
    public Map<String, Object> getJsonPieData(AssetAvailabilitySummary aaSummary, YukonUserContext userContext) {

        MessageSourceAccessor msa = messageSourceResolver.getMessageSourceAccessor(userContext);
        String runningStr = msa.getMessage("yukon.web.modules.operator.hardware.assetAvailability.active");
        String notRunningStr = msa.getMessage("yukon.web.modules.operator.hardware.assetAvailability.inactive");
        String optedOutStr = msa.getMessage("yukon.web.modules.operator.hardware.assetAvailability.optedOut");
        String unavailableStr = msa.getMessage("yukon.web.modules.operator.hardware.assetAvailability.unavailable");
        
        Map<String, FlotPieDatas> labelDataColorMap = Maps.newHashMapWithExpectedSize(4);
        labelDataColorMap.put(runningStr, new FlotPieDatas(aaSummary.getActiveSize(), "#093")); // .success
        labelDataColorMap.put(notRunningStr, new FlotPieDatas(aaSummary.getInactiveSize(), "#ffac00")); // .warning
        labelDataColorMap.put(optedOutStr, new FlotPieDatas(aaSummary.getOptedOutSize(), "#888")); // .disabled
        labelDataColorMap.put(unavailableStr, new FlotPieDatas(aaSummary.getUnavailableSize(), "#d14836")); // .error

        Map<String, Object> pieJsonData = flotChartService.getPieGraphDataWithColor(labelDataColorMap, false, false, 0.9);
        return pieJsonData;
    }
}
