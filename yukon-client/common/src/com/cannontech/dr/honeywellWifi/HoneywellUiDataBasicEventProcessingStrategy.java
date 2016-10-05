package com.cannontech.dr.honeywellWifi;

import org.apache.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;

public class HoneywellUiDataBasicEventProcessingStrategy implements HoneywellWifiDataProcessingStrategy {
    private static final Logger log = YukonLogManager.getLogger(HoneywellUiDataBasicEventProcessingStrategy.class);
    
    @Override
    public HoneywellWifiDataType getSupportedType() {
        return HoneywellWifiDataType.UI_DATA_BASIC_EVENT;
    }

    @Override
    public void processData(HoneywellWifiData data) {
        log.info("Processing data message: " + data); //TODO change to debug
        //TODO
    }

}
