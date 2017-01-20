package com.cannontech.dr.honeywellWifi.azure.event.processing;

import org.apache.log4j.Logger;
import org.joda.time.Instant;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.dr.honeywellWifi.HoneywellWifiDataType;
import com.cannontech.dr.honeywellWifi.azure.event.DemandResponseEvent;
import com.cannontech.dr.honeywellWifi.azure.event.HoneywellWifiData;

public class DemandResponseEventProcessor extends AbstractHoneywellWifiDataProcessor {
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
        
        double stateValue = event.getPhase().getStateValue();
        Instant eventTime = event.getMessageWrapper().getDate();
        
        try {
            PaoIdentifier thermostat = getThermostatByMacId(event.getMacId());
            
            //Send point data to dispatch
            inputPointValue(thermostat, BuiltInAttribute.CONTROL_STATUS, eventTime, stateValue);
        } catch (NotFoundException e) {
            log.info("Honeywell demand response message received for unknown device with MAC ID " + event.getMacId());
        }
    }

}
