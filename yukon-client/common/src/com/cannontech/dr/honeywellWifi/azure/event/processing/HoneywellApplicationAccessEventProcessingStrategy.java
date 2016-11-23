package com.cannontech.dr.honeywellWifi.azure.event.processing;

import org.apache.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.dr.honeywellWifi.HoneywellWifiDataType;
import com.cannontech.dr.honeywellWifi.azure.event.ApplicationAccessAddedEvent;
import com.cannontech.dr.honeywellWifi.azure.event.HoneywellWifiData;

public class HoneywellApplicationAccessEventProcessingStrategy extends AbstractHoneywellWifiDataProcessingStrategy {
    private static final Logger log = YukonLogManager.getLogger(HoneywellApplicationAccessEventProcessingStrategy.class);
    
    @Override
    public HoneywellWifiDataType getSupportedType() {
        return HoneywellWifiDataType.APPLICATION_ACCESS_ADDED_EVENT;
    }

    @Override
    public void processData(HoneywellWifiData data) {
        ApplicationAccessAddedEvent event = (ApplicationAccessAddedEvent) data;
        log.debug("Received ApplicationAccessAddedEvent. This event is currently unused. Ignoring.");
        log.trace(event);
    }

}
