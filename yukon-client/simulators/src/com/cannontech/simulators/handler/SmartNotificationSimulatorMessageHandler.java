package com.cannontech.simulators.handler;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.smartNotification.simulation.service.SmartNotificationSimulatorService;
import com.cannontech.simulators.SimulatorType;
import com.cannontech.simulators.message.request.SimulatorRequest;
import com.cannontech.simulators.message.request.SmartNotificationSimulatorRequest;
import com.cannontech.simulators.message.response.SimulatorResponse;
import com.cannontech.simulators.message.response.SimulatorResponseBase;
import com.cannontech.simulators.message.response.SmartNotificationSimulatorResponse;

public class SmartNotificationSimulatorMessageHandler extends SimulatorMessageHandler {
    private static final Logger log = YukonLogManager.getSmartNotificationsLogger(SmartNotificationSimulatorMessageHandler.class);
    @Autowired private SmartNotificationSimulatorService smartNotificationSimulatorService;
    
    public SmartNotificationSimulatorMessageHandler() {
        super(SimulatorType.SMART_NOTIFICATION);
    }
    
    @Override
    public SimulatorResponse handle(SimulatorRequest simulatorRequest) {
        try {
            if (simulatorRequest instanceof SmartNotificationSimulatorRequest) {
                SmartNotificationSimulatorRequest request = (SmartNotificationSimulatorRequest) simulatorRequest;
                switch (request.getRequestAction()) {
                    case GET_SETTINGS:
                        return new SmartNotificationSimulatorResponse(smartNotificationSimulatorService.getCurrentSettings());
                    case CLEAR_ALL_SUBSCRIPTIONS:
                        return smartNotificationSimulatorService.clearAllSubscriptions();
                    case CLEAR_ALL_EVENTS:
                        return smartNotificationSimulatorService.clearAllEvents();
                    case CREATE_EVENTS:
                        return smartNotificationSimulatorService.createEvents();
                    case START_DAILY_DIGEST:
                        return smartNotificationSimulatorService.startDailyDigest();
                    default:
                        SimulatorResponseBase response = new SimulatorResponseBase(false);
                        response.setException(new Exception("Unsupported request action in a SmartNotificationSimulatorRequest: " + request.getRequestAction()));
                        return response;
                }
            } else {
                throw new IllegalArgumentException(
                    "Unsupported request type received: " + simulatorRequest.getClass().getCanonicalName());
            }
        } catch (Exception e) {
            log.error("Exception handling request: " + simulatorRequest);
            throw e;
        }
    }
}
