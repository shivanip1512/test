package com.cannontech.web.common.widgets.service.impl;

import org.apache.logging.log4j.Logger;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.pao.attribute.service.IllegalUseOfAttribute;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.dr.assetavailability.AssetAvailabilitySummary;
import com.cannontech.dr.assetavailability.service.impl.AssetAvailabilityServiceImpl;
import com.cannontech.web.common.widgets.model.AssetAvailabilityWidgetSummary;
import com.cannontech.web.common.widgets.service.AssetAvailabilityWidgetService;
import com.cannontech.yukon.IDatabaseCache;

public class AssetAvailabilityWidgetServiceImpl implements AssetAvailabilityWidgetService {
    
    private static final Logger log = YukonLogManager.getLogger(AssetAvailabilityWidgetServiceImpl.class);
    private static final Duration MINUTES_TO_WAIT_BEFORE_NEXT_REFRESH = Duration.standardMinutes(15);

    @Autowired private AssetAvailabilityServiceImpl assetAvailabilityService;
    @Autowired private AttributeService attributeService;
    @Autowired private IDatabaseCache cache;

    @Override
    public AssetAvailabilityWidgetSummary getAssetAvailabilitySummary(Integer areaOrLMProgramOrScenarioId, Instant lastUpdateTime) {
        return buildAssetAvailabilityWidgetSummary(areaOrLMProgramOrScenarioId, lastUpdateTime);
    }

    /**
     * Creates asset availability widget summary .
     */
    private AssetAvailabilityWidgetSummary buildAssetAvailabilityWidgetSummary(Integer areaOrLMProgramOrScenarioId, Instant lastUpdateTime) {
        AssetAvailabilityWidgetSummary summary = new AssetAvailabilityWidgetSummary(lastUpdateTime);
        LiteYukonPAObject drPao = cache.getAllPaosMap().get(areaOrLMProgramOrScenarioId);
        AssetAvailabilitySummary assetAvailabilitySummary = assetAvailabilityService.getAssetAvailabilityFromDrGroup(drPao.getPaoIdentifier());

        try {
            LitePoint activePoint = attributeService.getPointForAttribute(drPao, BuiltInAttribute.LM_ASSET_AVAILABILITY_ACTIVE_DEVICES);
            LitePoint inactivePoint = attributeService.getPointForAttribute(drPao, BuiltInAttribute.LM_ASSET_AVAILABILITY_INACTIVE_DEVICES);
            LitePoint unavailablePoint = attributeService.getPointForAttribute(drPao, BuiltInAttribute.LM_ASSET_AVAILABILITY_UNAVAILABLE_DEVICES);
            LitePoint optedOutPoint = attributeService.getPointForAttribute(drPao, BuiltInAttribute.LM_ASSET_AVAILABILITY_OPTED_OUT_DEVICES);
            summary.setActive(assetAvailabilitySummary.getActiveSize(), activePoint.getPointID());
            summary.setInactive(assetAvailabilitySummary.getInactiveSize(), inactivePoint.getPointID());
            summary.setUnavailable(assetAvailabilitySummary.getUnavailableSize(), unavailablePoint.getPointID());
            summary.setOptedOut(assetAvailabilitySummary.getOptedOutSize(), optedOutPoint.getPointID());
        } catch (IllegalUseOfAttribute e) {
            summary.setActive(assetAvailabilitySummary.getActiveSize(), null);
            summary.setInactive(assetAvailabilitySummary.getInactiveSize(), null);
            summary.setUnavailable(assetAvailabilitySummary.getUnavailableSize(), null);
            summary.setOptedOut(assetAvailabilitySummary.getOptedOutSize(), null);
        }

        summary.calculatePrecentages();
        log.debug(summary);
        return summary;
    }

    @Override
    public Instant getNextRefreshTime(Instant lastUpdateTime) {
        return lastUpdateTime.plus(MINUTES_TO_WAIT_BEFORE_NEXT_REFRESH);
    }

    @Override
    public long getRefreshMilliseconds() {
        return MINUTES_TO_WAIT_BEFORE_NEXT_REFRESH.getMillis();
    }

}
