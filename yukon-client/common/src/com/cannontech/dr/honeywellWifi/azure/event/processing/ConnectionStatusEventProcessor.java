package com.cannontech.dr.honeywellWifi.azure.event.processing;

import org.apache.log4j.Logger;
import org.joda.time.Instant;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.dr.honeywellWifi.HoneywellWifiDataType;
import com.cannontech.dr.honeywellWifi.azure.event.ConnectionStatusEvent;
import com.cannontech.dr.honeywellWifi.azure.event.HoneywellWifiData;

public class ConnectionStatusEventProcessor extends AbstractHoneywellWifiDataProcessor {
    private static final Logger log = YukonLogManager.getLogger(ConnectionStatusEventProcessor.class);
    
    @Override
    public HoneywellWifiDataType getSupportedType() {
        return HoneywellWifiDataType.CONNECTION_STATUS_EVENT;
    }
    
    @Override
    public void processData(HoneywellWifiData data) {
        log.debug("Processing connection status message" + data);
        if(!(data instanceof ConnectionStatusEvent)) {
            throw new IllegalArgumentException("Invalid data object passed to processor: " + data.getType());
        }
        
        ConnectionStatusEvent dataEvent = (ConnectionStatusEvent) data;
        
        double commStatus = dataEvent.getConnectionStatus().getCommStatus();
        Instant statusDateTime = dataEvent.getMessageWrapper().getDate();
        
        try {
            PaoIdentifier thermostat = getThermostatByMacId(dataEvent.getMacId());
            
            //Send the point data to Dispatch
            inputPointValue(thermostat, BuiltInAttribute.COMM_STATUS, statusDateTime, commStatus);
        } catch (NotFoundException e) {
            log.info("Honeywell connection status message received for unknown device with MAC ID " + dataEvent.getMacId());
        }
        
    }
}
