package com.cannontech.dr.honeywellWifi.azure.event.processing;

import org.apache.log4j.Logger;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.dr.honeywellWifi.HoneywellWifiDataType;
import com.cannontech.dr.honeywellWifi.azure.event.DemandResponseEvent;
import com.cannontech.dr.honeywellWifi.azure.event.EventPhase;
import com.cannontech.dr.honeywellWifi.azure.event.HoneywellWifiData;
import com.cannontech.dr.recenteventparticipation.service.RecentEventParticipationService;

public class DemandResponseEventProcessor extends AbstractHoneywellWifiDataProcessor {
    private static final Logger log = YukonLogManager.getLogger(DemandResponseEventProcessor.class);
    @Autowired RecentEventParticipationService recentEventParticipationService;

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
        
        // If device is opted out, set the Control Status to 0 (false), regardless of the phase.
        double stateValue = event.getOptedOut() ? 0 : event.getPhase().getStateValue();
        Instant eventTime = event.getMessageWrapper().getDate();
        
        // Subtract 1 second from "NotStarted" events. This avoids problems that occur when a "NotStarted" and a 
        // "Phase1" event come in with identical timestamps. See YUK-16219.
        if (event.getPhase() == EventPhase.NOT_STARTED) {
            eventTime.minus(Duration.standardSeconds(1));
        }
        
        try {
            PaoIdentifier thermostat = getThermostatByMacId(event.getMacId());
            
            //Send point data to dispatch
            inputPointValue(thermostat, BuiltInAttribute.CONTROL_STATUS, eventTime, stateValue);
            recentEventParticipationService.updateDeviceControlEvent(event.getDemandResponseId(), thermostat.getPaoId(),
                event.getPhase(), eventTime);
        } catch (NotFoundException e) {
            log.info("Honeywell demand response message received for unknown device with MAC ID " + event.getMacId());
        }
    }

}
