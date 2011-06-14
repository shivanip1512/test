package com.cannontech.thirdparty.model;

import com.cannontech.core.dao.NotFoundException;

public enum ZigbeeEventAction {
    THERMOSTAT_ACK(1),
    EVENT_START(2),
    EVENT_COMPLETE(3),
    USER_OPT_OUT(4),
    USER_OPT_IN(5),
    THERMOSTAT_EVENT_CANCELED(6),
    THERMOSTAT_EVENT_SUPERSEDED(7),
    PARTIAL_COMPLETE_WITH_OPT_OUT(8),
    PARTIAL_COMPLETE_WITH_OPT_IN(9),
    EVENT_COMPLETE_NO_PARTICIPATION(10),//Previous Opt out
    INVALID_CANCEL_COMMAND_DEFAULT(248),
    INVALID_CANCEL_COMMAND_BAD_TIME(249),
    REJECTED_PAST_EVENT(251),
    INVALID_CANCEL_COMMAND_BAD_EVENT(253),
    CONTROL_EVENT_REJECTED(254);
    
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

}
