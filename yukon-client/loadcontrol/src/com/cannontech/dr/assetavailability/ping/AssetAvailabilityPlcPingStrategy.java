package com.cannontech.dr.assetavailability.ping;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.pao.YukonPao;
import com.cannontech.dr.assetavailability.service.AssetAvailabilityPlcPingService;
import com.google.common.base.Predicate;
import com.google.common.collect.Sets;

/**
 * Asset availability "ping" strategy for PLC devices.
 */
public class AssetAvailabilityPlcPingStrategy implements AssetAvailabilityPingStrategy {
    @Autowired AssetAvailabilityPlcPingService assetAvailabilityPlcPingService;
    
    @Override
    public AssetPingStrategyType getType() {
        return AssetPingStrategyType.PLC;
    }

    @Override
    public <T extends YukonPao> Set<T> filterDevices(Set<T> devices) {
        Predicate<YukonPao> plcTwoWayPredicate = new Predicate<YukonPao>() {
            public boolean apply(YukonPao input) {
                return input.getPaoIdentifier().getPaoType().isTwoWayPlcLcr();
            }
        };
        return Sets.filter(devices, plcTwoWayPredicate);
    }

    @Override
    public void readDevices(Set<? extends YukonPao> devices, String resultId) {
        assetAvailabilityPlcPingService.readDevices(devices, resultId);
    }
}
