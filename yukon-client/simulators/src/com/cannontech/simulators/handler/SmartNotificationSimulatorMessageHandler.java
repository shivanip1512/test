package com.cannontech.simulators.handler;

import org.apache.log4j.Logger;
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
    private static final Logger log = YukonLogManager.getLogger(SmartNotificationSimulatorMessageHandler.class);
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
                    case "getSettings":
                        return new SmartNotificationSimulatorResponse(smartNotificationSimulatorService.getCurrentSettings());
                    case "clearAllSubscriptions":
                        return smartNotificationSimulatorService.clearAllSubscriptions();
                    case "clearAllEvents":
                        return smartNotificationSimulatorService.clearAllEvents();
                    case "createEvents":
                        return smartNotificationSimulatorService.createEvents(request.getWaitTime(),
                                                                              request.getEventsPerMessage(), 
                                                                              request.getNumberOfMessages());
                    case "saveSubscription":
                        return smartNotificationSimulatorService.saveSubscription(request.getSubscription(), request.getUserGroupId(), 
                                                                                  request.isGenerateTestEmailAddresses(),
                                                                                  request.getUserContext());
                    case "startDailyDigest":
                        return smartNotificationSimulatorService.startDailyDigest(request.getDailyDigestHour());
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
