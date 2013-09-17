package com.cannontech.dr.assetavailability.ping;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.pao.YukonPao;
import com.cannontech.dr.assetavailability.service.AssetAvailabilityRfnPingService;
import com.google.common.base.Predicate;
import com.google.common.collect.Sets;

/**
 * Asset availability "ping" strategy for RFN devices.
 */
public class AssetAvailabilityRfnPingStrategy implements AssetAvailabilityPingStrategy {
    @Autowired AssetAvailabilityRfnPingService assetAvailabilityRfnPingService;
    
    @Override
    public AssetPingStrategyType getType() {
        return AssetPingStrategyType.RFN;
    }

    @Override
    public <T extends YukonPao> Set<T> filterDevices(Set<T> devices) {
        Predicate<YukonPao> rfnTwoWayPredicate = new Predicate<YukonPao>() {
            public boolean apply(YukonPao input) {
                return input.getPaoIdentifier().getPaoType().isTwoWayRfnLcr();
            }
        };
        return Sets.filter(devices, rfnTwoWayPredicate);
    }

    @Override
    public void readDevices(Set<? extends YukonPao> devices, String resultId) {
        assetAvailabilityRfnPingService.readDevices(devices, resultId);
    }
}
