package com.cannontech.web.common.widgets.service.impl;

import java.text.NumberFormat;
import java.text.ParseException;

import org.apache.logging.log4j.Logger;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.pao.attribute.service.IllegalUseOfAttribute;
import com.cannontech.core.service.PointFormattingService.Format;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.dr.assetavailability.AssetAvailabilitySummary;
import com.cannontech.dr.assetavailability.service.impl.AssetAvailabilityServiceImpl;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.widgets.model.AssetAvailabilityWidgetSummary;
import com.cannontech.web.common.widgets.service.AssetAvailabilityWidgetService;
import com.cannontech.web.updater.UpdateValue;
import com.cannontech.web.updater.point.PointDataRegistrationService;
import com.cannontech.yukon.IDatabaseCache;

public class AssetAvailabilityWidgetServiceImpl implements AssetAvailabilityWidgetService {
    
    private static final Logger log = YukonLogManager.getLogger(AssetAvailabilityWidgetServiceImpl.class);
    private static final Duration MINUTES_TO_WAIT_BEFORE_NEXT_REFRESH = Duration.standardMinutes(15);

    @Autowired private AssetAvailabilityServiceImpl assetAvailabilityService;
    @Autowired private AttributeService attributeService;
    @Autowired private PointDataRegistrationService registrationService;
    @Autowired private IDatabaseCache cache;

    @Override
    public AssetAvailabilityWidgetSummary getAssetAvailabilitySummary(Integer areaOrLMProgramOrScenarioId, Instant lastUpdateTime, YukonUserContext userContext) {
        return buildAssetAvailabilityWidgetSummary(areaOrLMProgramOrScenarioId, lastUpdateTime, userContext);
    }

    /**
     * Creates asset availability widget summary .
     */
    private AssetAvailabilityWidgetSummary buildAssetAvailabilityWidgetSummary(Integer areaOrLMProgramOrScenarioId, Instant lastUpdateTime, YukonUserContext userContext) {
        AssetAvailabilityWidgetSummary summary = new AssetAvailabilityWidgetSummary(lastUpdateTime);
        LiteYukonPAObject drPao = cache.getAllPaosMap().get(areaOrLMProgramOrScenarioId);
        AssetAvailabilitySummary assetAvailabilitySummary = assetAvailabilityService.getAssetAvailabilityFromDrGroup(drPao.getPaoIdentifier());
        //set initial values - i believe these will be removed once the archiving of point values is complete
        summary.setActive(assetAvailabilitySummary.getActiveSize(), null);
        summary.setInactive(assetAvailabilitySummary.getInactiveSize(), null);
        summary.setUnavailable(assetAvailabilitySummary.getUnavailableSize(), null);
        summary.setOptedOut(assetAvailabilitySummary.getOptedOutSize(), null);
        NumberFormat formatter = NumberFormat.getInstance(userContext.getLocale());
        
        try {
            LitePoint activePoint = attributeService.getPointForAttribute(drPao, BuiltInAttribute.LM_ASSET_AVAILABILITY_ACTIVE_DEVICES);
            if (activePoint != null) {
                UpdateValue activeValue = registrationService.getLatestValue(activePoint.getPointID(), Format.VALUE.toString(), userContext);
                if (!activeValue.isUnavailable()) {
                    summary.setActive(formatter.parse(activeValue.getValue()).intValue(), activePoint.getPointID());
                }
            }
            LitePoint inactivePoint = attributeService.getPointForAttribute(drPao, BuiltInAttribute.LM_ASSET_AVAILABILITY_INACTIVE_DEVICES);
            if (inactivePoint != null) {
                UpdateValue inactiveValue = registrationService.getLatestValue(inactivePoint.getPointID(), Format.VALUE.toString(), userContext);
                if (!inactiveValue.isUnavailable()) {
                    summary.setInactive(formatter.parse(inactiveValue.getValue()).intValue(), inactivePoint.getPointID());
                }
            }
            LitePoint unavailablePoint = attributeService.getPointForAttribute(drPao, BuiltInAttribute.LM_ASSET_AVAILABILITY_UNAVAILABLE_DEVICES);
            if (unavailablePoint != null) {
                UpdateValue unavailableValue = registrationService.getLatestValue(unavailablePoint.getPointID(), Format.VALUE.toString(), userContext);
                if (!unavailableValue.isUnavailable()) {
                    summary.setUnavailable(formatter.parse(unavailableValue.getValue()).intValue(), unavailablePoint.getPointID());
                }
            }
            LitePoint optedOutPoint = attributeService.getPointForAttribute(drPao, BuiltInAttribute.LM_ASSET_AVAILABILITY_OPTED_OUT_DEVICES);
            if (optedOutPoint != null) {
                UpdateValue optedOutValue = registrationService.getLatestValue(optedOutPoint.getPointID(), Format.VALUE.toString(), userContext);
                if (!optedOutValue.isUnavailable()) {
                    summary.setOptedOut(formatter.parse(optedOutValue.getValue()).intValue(), optedOutPoint.getPointID());
                }
            }
        } catch (IllegalUseOfAttribute e) {
            log.info("Point not found for Asset Availability." , e);
        } catch (ParseException e) {
            log.info("Could not parse Asset Availability point value.", e);
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
