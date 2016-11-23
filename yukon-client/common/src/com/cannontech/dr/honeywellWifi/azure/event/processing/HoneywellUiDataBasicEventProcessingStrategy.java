package com.cannontech.dr.honeywellWifi.azure.event.processing;

import org.apache.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.temperature.Temperature;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.dr.honeywellWifi.HoneywellWifiDataType;
import com.cannontech.dr.honeywellWifi.azure.event.HoneywellWifiData;
import com.cannontech.dr.honeywellWifi.azure.event.UiDataBasicEvent;

public class HoneywellUiDataBasicEventProcessingStrategy extends AbstractHoneywellWifiDataProcessingStrategy {
    private static final Logger log = YukonLogManager.getLogger(HoneywellUiDataBasicEventProcessingStrategy.class);
    
    @Override
    public HoneywellWifiDataType getSupportedType() {
        return HoneywellWifiDataType.UI_DATA_BASIC_EVENT;
    }

    @Override
    public void processData(HoneywellWifiData data) {
        log.debug("Processing data message: " + data);
        if (!(data instanceof UiDataBasicEvent)) {
            throw new IllegalArgumentException("Invalid data object passed to processor: " + data.getType());
        }
        
        // Get temp values, converting to Fahrenheit if necessary
        UiDataBasicEvent dataEvent = (UiDataBasicEvent) data;
        Double coolSetpoint = Temperature.from(dataEvent.getCoolSetpoint(), dataEvent.getDisplayedUnits()).toFahrenheit().getValue();
        Double heatSetpoint = Temperature.from(dataEvent.getHeatSetpoint(), dataEvent.getDisplayedUnits()).toFahrenheit().getValue();
        Double indoorTemp = Temperature.from(dataEvent.getDisplayedTemp(), dataEvent.getDisplayedUnits()).toFahrenheit().getValue();
        
        try {
            PaoIdentifier paoIdentifier = getThermostatByMacId(dataEvent.getMacId());
            
            // Send point data to Dispatch
            inputPointValue(paoIdentifier, BuiltInAttribute.COOL_SET_TEMPERATURE, dataEvent.getCreatedDate(), coolSetpoint);
            inputPointValue(paoIdentifier, BuiltInAttribute.HEAT_SET_TEMPERATURE, dataEvent.getCreatedDate(), heatSetpoint);
            inputPointValue(paoIdentifier, BuiltInAttribute.INDOOR_TEMPERATURE, dataEvent.getCreatedDate(), indoorTemp);
        } catch (NotFoundException e) {
            log.info("Honeywell data message received for unknown device with MAC ID " + dataEvent.getMacId());
        }
    }
    
}
