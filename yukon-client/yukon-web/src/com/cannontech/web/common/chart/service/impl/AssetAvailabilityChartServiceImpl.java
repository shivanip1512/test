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
        String active = msa.getMessage("yukon.web.modules.operator.hardware.assetAvailability.active");
        String inactive = msa.getMessage("yukon.web.modules.operator.hardware.assetAvailability.inactive");
        String optedOut = msa.getMessage("yukon.web.modules.operator.hardware.assetAvailability.optedOut");
        String unavailable = msa.getMessage("yukon.web.modules.operator.hardware.assetAvailability.unavailable");
        
        Map<String, FlotPieDatas> labelDataColorMap = Maps.newHashMapWithExpectedSize(4);
        labelDataColorMap.put(active, new FlotPieDatas(aaSummary.getActiveSize(), "#009933")); // .pie-green
        labelDataColorMap.put(optedOut, new FlotPieDatas(aaSummary.getOptedOutSize(), "#4d90fe")); // .pie-blue
        labelDataColorMap.put(inactive, new FlotPieDatas(aaSummary.getInactiveSize(), "#ec971f")); // .orange
        labelDataColorMap.put(unavailable, new FlotPieDatas(aaSummary.getUnavailableSize(), "#888")); // .grey

        Map<String, Object> pieJsonData = flotChartService.getPieGraphDataWithColor(labelDataColorMap, false, false, 0.9);
        return pieJsonData;
    }
}
