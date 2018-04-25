package com.cannontech.dr.honeywellWifi.azure.event.processing;

import org.apache.logging.log4j.Logger;
import org.joda.time.Instant;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.dr.honeywellWifi.HoneywellWifiDataType;
import com.cannontech.dr.honeywellWifi.azure.event.EquipmentStatusEvent;
import com.cannontech.dr.honeywellWifi.azure.event.HoneywellWifiData;

public class EquipmentStatusEventProcessor extends AbstractHoneywellWifiDataProcessor {
    private static final Logger log = YukonLogManager.getLogger(EquipmentStatusEventProcessor.class);
    
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
            double stateValue = dataEvent.getEquipmentStatus().getStateValue();
            Instant statusDateTime = dataEvent.getMessageWrapper().getDate();
            
            try {
                PaoIdentifier thermostat = getThermostatByMacId(dataEvent.getMacId());
                
                //Send point data to dispatch
                inputPointValue(thermostat, BuiltInAttribute.THERMOSTAT_RELAY_STATE, statusDateTime, stateValue);
            } catch (NotFoundException e) {
                log.info("Honeywell equipment status message received for unknown device with MAC ID " + dataEvent.getMacId());
            }
        }
        
    }

}
