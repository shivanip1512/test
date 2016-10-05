package com.cannontech.dr.honeywellWifi;

import org.apache.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;

public class HoneywellEquipmentStatusEventProcessingStrategy implements HoneywellWifiDataProcessingStrategy {
    private static final Logger log = YukonLogManager.getLogger(HoneywellEquipmentStatusEventProcessingStrategy.class);
    
    @Override
    public HoneywellWifiDataType getSupportedType() {
        return HoneywellWifiDataType.EQUIPMENT_STATUS_EVENT;
    }

    @Override
    public void processData(HoneywellWifiData data) {
        log.info("Processing equipment status message"); //TODO change to debug
        //TODO
    }

}
