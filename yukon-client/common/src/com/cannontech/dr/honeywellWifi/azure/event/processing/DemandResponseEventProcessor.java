package com.cannontech.dr.honeywellWifi.azure.event.processing;

import org.apache.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.dr.honeywellWifi.HoneywellWifiDataType;
import com.cannontech.dr.honeywellWifi.azure.event.DemandResponseEvent;
import com.cannontech.dr.honeywellWifi.azure.event.HoneywellWifiData;

public class DemandResponseEventProcessor implements HoneywellWifiDataProcessor {
    private static final Logger log = YukonLogManager.getLogger(DemandResponseEventProcessor.class);
    
    @Override
    public HoneywellWifiDataType getSupportedType() {
        return HoneywellWifiDataType.DEMAND_RESPONSE_EVENT;
    }

    @Override
    public void processData(HoneywellWifiData data) {
        log.debug("Processing demand response message: " + data);
        if (!(data instanceof DemandResponseEvent)) {
            throw new IllegalArgumentException("Invalid data object passed to processor: " + data.getType());
        }
        
        DemandResponseEvent event = (DemandResponseEvent) data;
        
        //TODO
    }

}
