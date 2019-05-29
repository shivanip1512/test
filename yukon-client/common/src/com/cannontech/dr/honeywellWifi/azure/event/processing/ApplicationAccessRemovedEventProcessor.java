package com.cannontech.dr.honeywellWifi.azure.event.processing;

import org.apache.logging.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.dr.honeywellWifi.HoneywellWifiDataType;
import com.cannontech.dr.honeywellWifi.azure.event.ApplicationAccessRemovedEvent;
import com.cannontech.dr.honeywellWifi.azure.event.HoneywellWifiData;

public class ApplicationAccessRemovedEventProcessor extends AbstractHoneywellWifiDataProcessor {
    private static final Logger log = YukonLogManager.getLogger(ApplicationAccessRemovedEventProcessor.class);
    
    @Override
    public HoneywellWifiDataType getSupportedType() {
        return HoneywellWifiDataType.APPLICATION_ACCESS_REMOVED_EVENT;
    }

    @Override
    public void processData(HoneywellWifiData data) {
        ApplicationAccessRemovedEvent event = (ApplicationAccessRemovedEvent) data;
        log.debug("Received ApplicationAccessRemovedEvent. This event is currently unused. Ignoring.");
        log.trace(event);
    }
}
