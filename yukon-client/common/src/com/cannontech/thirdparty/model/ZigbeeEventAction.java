package com.cannontech.thirdparty.model;

import com.cannontech.common.util.DatabaseRepresentationSource;
import com.cannontech.core.dao.NotFoundException;

public enum ZigbeeEventAction implements DatabaseRepresentationSource{
    GATEWAY_ACK(0),
    THERMOSTAT_ACK(1),
    THERMOSTAT_EVENT_START(2),
    THERMOSTAT_EVENT_STOP(3),
    THERMOSTAT_EVENT_CANCELED(6),
    THERMOSTAT_EVENT_SUPERSEDED(7);
    
    private int eventStatus;
    
    private ZigbeeEventAction(int eventStatus) {
        this.eventStatus = eventStatus;
    }
    
    public static ZigbeeEventAction getForEventStatus(int eventStatus) {
        for (ZigbeeEventAction action : values()) {
            if (action.getEventStatus() == eventStatus){
                return action;
            }
        }
        throw new NotFoundException("ZigbeeEventAction with eventStatus, " + eventStatus + " not found.");
    }
    
    private int getEventStatus() {
        return eventStatus;
    }
    
    @Override
    public Object getDatabaseRepresentation() {
        return this.name();
    }
}
