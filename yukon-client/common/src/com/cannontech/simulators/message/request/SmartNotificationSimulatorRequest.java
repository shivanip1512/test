package com.cannontech.simulators.message.request;

import com.cannontech.simulators.SimulatorType;

/**
 * Request to modify the state of the smart notification simulator. Tells the 
 * SmartNotificationSimulatorMessageHandler what the SmartNotificationSimulatorService should do
 * (e.g. clearAllSubscriptions, createRealEvents, etc)
 */
public class SmartNotificationSimulatorRequest implements SimulatorRequest {
    
    private RequestAction requestAction;
    
    public static enum RequestAction {
        GET_SETTINGS,
        CLEAR_ALL_SUBSCRIPTIONS,
        CLEAR_ALL_EVENTS,
        CREATE_EVENTS,
        SAVE_SUBSCRIPTION,
        START_DAILY_DIGEST;
    }
    
    public SmartNotificationSimulatorRequest(RequestAction requestAction) {
        this.requestAction = requestAction;
    }
    
    @Override
    public SimulatorType getRequestType() {
        return SimulatorType.SMART_NOTIFICATION;
    }
    
    public RequestAction getRequestAction() {
        return requestAction;
    }
}
