package com.cannontech.simulators.message.request;

import com.cannontech.common.smartNotification.model.SmartNotificationSubscription;
import com.cannontech.simulators.SimulatorType;
import com.cannontech.user.YukonUserContext;

/**
 * Request to modify the state of the smart notification simulator. Tells the 
 * SmartNotificationSimulatorMessageHandler what the SmartNotificationSimulatorService should do
 * (e.g. clearAllSubscriptions, createRealEvents, etc)
 */
public class SmartNotificationSimulatorRequest implements SimulatorRequest {
    
    private RequestAction requestAction;
    private int dailyDigestHour, waitTime, eventsPerMessage, numberOfMessages, userGroupId;
    private boolean generateTestEmailAddresses;
    SmartNotificationSubscription subscription;
    private YukonUserContext userContext;
    
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
    
    public SmartNotificationSimulatorRequest(int dailyDigestHour) {
        this.dailyDigestHour = dailyDigestHour;
        requestAction = RequestAction.START_DAILY_DIGEST;
    }
    
    public SmartNotificationSimulatorRequest(int waitTime, int eventsPerMessage, int numberOfMessages) {
        this.waitTime = waitTime;
        this.eventsPerMessage = eventsPerMessage;
        this.numberOfMessages = numberOfMessages;
        requestAction = RequestAction.CREATE_EVENTS;
    }
    
    public SmartNotificationSimulatorRequest(SmartNotificationSubscription subscription, int userGroupId, 
                                             boolean generateTestEmailAddresses, YukonUserContext userContext) {
        this.subscription = subscription;
        this.userGroupId = userGroupId;
        this.generateTestEmailAddresses = generateTestEmailAddresses;
        this.userContext = userContext;
        requestAction = RequestAction.SAVE_SUBSCRIPTION;
    }
    
    @Override
    public SimulatorType getRequestType() {
        return SimulatorType.SMART_NOTIFICATION;
    }
    
    public RequestAction getRequestAction() {
        return requestAction;
    }
    
    public int getDailyDigestHour() {
        return dailyDigestHour;
    }

    public int getWaitTime() {
        return waitTime;
    }

    public int getEventsPerMessage() {
        return eventsPerMessage;
    }

    public int getNumberOfMessages() {
        return numberOfMessages;
    }
    
    public int getUserGroupId() {
        return userGroupId;
    }

    public boolean isGenerateTestEmailAddresses() {
        return generateTestEmailAddresses;
    }

    public SmartNotificationSubscription getSubscription() {
        return subscription;
    }

    public YukonUserContext getUserContext() {
        return userContext;
    }

}
