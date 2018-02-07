package com.cannontech.common.smartNotification.simulation.service;

import com.cannontech.common.smartNotification.model.SmartNotificationSubscription;
import com.cannontech.infrastructure.model.SmartNotificationSimulatorSettings;
import com.cannontech.simulators.message.response.SimulatorResponseBase;
import com.cannontech.user.YukonUserContext;

public interface SmartNotificationSimulatorService {
    
    /**
     * Clears all of the SmartNoficiationSubscriptions from the database.
     */
    SimulatorResponseBase clearAllSubscriptions();
    /**
     * Clear all SmartNotificationEvents from the database.
     */
    SimulatorResponseBase clearAllEvents();
    /**
     * Create events 
     * @param int waitTime to wait between messages
     * @param int eventsPerMessage how many events to send in each message
     * @param int numberOfMessages total number of messages to send for each SmartNotificationSubscription type
     * @return SimnulatorResponseBase indicating success or failure
     */
    SimulatorResponseBase createEvents(int waitTime, int eventsPerMessage, int numberOfMessages);
    /**
     * Saves the SmartNoficiationSubscription to the database for each of the users in the
     * user group for the userGroupId.
     * @param a SmartNotificationSubscription for saving
     * @param int userGroupId to get all of the users for saving the subscription
     * @param boolean generateTestEmailAddresses indicates whether or not to generate test email addresses
     * @param userContext used for retrieving i18n messages
     * @return a SimulatorResponseBase object indicating success or failure
     */
    SimulatorResponseBase saveSubscription(SmartNotificationSubscription subscription, int userGroupId, 
                                             boolean generateTestEmailAddresses, YukonUserContext userContext);
    /**
     * Initiates a TEST daily digest for the given hour.
     * @param int dailyDigestHour the hour to start the daily digest.
     * @return a SimulatorResponse base reporting success or failure.
     */
    SimulatorResponseBase startDailyDigest(int dailyDigestHour);
    /**
     * Gets the SmartNotificationSimulatorSettings from the YukonSimulatorSettingsDao.
     * @return a SmartNotificationSimulatorSettings object
     */
    SmartNotificationSimulatorSettings getCurrentSettings();

}
