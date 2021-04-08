package com.cannontech.common.smartNotification.simulation.service;

import com.cannontech.infrastructure.model.SmartNotificationSimulatorSettings;
import com.cannontech.simulators.message.response.SimulatorResponseBase;

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
     * Create events using saved settings
     */
    SimulatorResponseBase createEvents();
    /**
     * Initiates a TEST daily digest for the given hour.
     */
    SimulatorResponseBase startDailyDigest();
    /**
     * Returns current settings.
     */
    SmartNotificationSimulatorSettings getCurrentSettings();

}
