package com.cannontech.web.common.widgets.service.impl;

import java.util.Arrays;
import java.util.Map;

import org.apache.logging.log4j.Logger;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.core.dao.RawPointHistoryDao;
import com.cannontech.core.dynamic.PointValueQualityHolder;
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
    @Autowired private IDatabaseCache cache;
    @Autowired private RawPointHistoryDao rphDao;

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
        //set initial values - i believe these will be removed once the archiving of point values is complete
        summary.setActive(assetAvailabilitySummary.getActiveSize(), null);
        summary.setInactive(assetAvailabilitySummary.getInactiveSize(), null);
        summary.setUnavailable(assetAvailabilitySummary.getUnavailableSize(), null);
        summary.setOptedOut(assetAvailabilitySummary.getOptedOutSize(), null);
        
        PointValueQualityHolder activeValue = getMostRecentPointValue(drPao, BuiltInAttribute.LM_ASSET_AVAILABILITY_ACTIVE_DEVICES);
        if (activeValue != null) {
            summary.setActive((int)Math.round(activeValue.getValue()), activeValue.getId());
        }
        
        PointValueQualityHolder inActiveValue = getMostRecentPointValue(drPao, BuiltInAttribute.LM_ASSET_AVAILABILITY_INACTIVE_DEVICES);
        if (inActiveValue != null) {
            summary.setInactive((int)Math.round(inActiveValue.getValue()), inActiveValue.getId());
        }
        
        PointValueQualityHolder unavailableValue = getMostRecentPointValue(drPao, BuiltInAttribute.LM_ASSET_AVAILABILITY_UNAVAILABLE_DEVICES);
        if (unavailableValue != null) {
            summary.setUnavailable((int)Math.round(unavailableValue.getValue()), unavailableValue.getId());
        }
        
        PointValueQualityHolder optedOutValue = getMostRecentPointValue(drPao, BuiltInAttribute.LM_ASSET_AVAILABILITY_OPTED_OUT_DEVICES);
        if (optedOutValue != null) {
            summary.setOptedOut((int)Math.round(optedOutValue.getValue()), optedOutValue.getId());
        }

        summary.calculatePrecentages();
        log.debug(summary);
        return summary;
    }
    
    private PointValueQualityHolder getMostRecentPointValue(LiteYukonPAObject pao, BuiltInAttribute attribute) {
        Map<PaoIdentifier, PointValueQualityHolder> activePaoPointValue =
                rphDao.getMostRecentAttributeDataByValue(Arrays.asList(pao), attribute, false, null, null);
        return activePaoPointValue.get(pao.getPaoIdentifier());
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
