package com.cannontech.dr.honeywellWifi.azure.event.processing;

import org.apache.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.dr.honeywellWifi.HoneywellWifiDataType;
import com.cannontech.dr.honeywellWifi.azure.event.HoneywellWifiData;

public class HoneywellDemandResponseEventProcessingStrategy implements HoneywellWifiDataProcessingStrategy {
    private static final Logger log = YukonLogManager.getLogger(HoneywellDemandResponseEventProcessingStrategy.class);
    
    @Override
    public HoneywellWifiDataType getSupportedType() {
        return HoneywellWifiDataType.DEMAND_RESPONSE_EVENT;
    }

    @Override
    public void processData(HoneywellWifiData data) {
        log.info("Processing demand response message"); //TODO change to debug
        //TODO
    }

}
