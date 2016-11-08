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
        log.debug("Processing equipment status message:" + data);
        if(!(data instanceof EquipmentStatusEvent)) {
            throw new IllegalArgumentException("Invalid data object passed to processor: " + data.getType());
        }
        
        EquipmentStatusEvent dataEvent = (EquipmentStatusEvent) data;
        
        // Ignore fanStatus messages (which have null equipmentStatus)
        if (dataEvent.getEquipmentStatus() == null) {
            return;
        } else {
            //TODO process the message
            //dataEvent.getEquipmentStatus();
            //dataEvent.getPreviousEquipmentStatus();
            //dataEvent.getMacId();
            //dataEvent.getMessageWrapper().getDate();
        }
        
    }

}
