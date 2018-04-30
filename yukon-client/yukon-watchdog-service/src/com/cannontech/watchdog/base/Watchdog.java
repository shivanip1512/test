package com.cannontech.watchdog.base;

import java.util.List;

import com.cannontech.common.smartNotification.model.SmartNotificationEvent;
import com.cannontech.common.smartNotification.model.SmartNotificationEventType;
import com.cannontech.watchdog.model.WatchdogWarnings;

public interface Watchdog {

    /**
     * Start a Watchdog, this will be called by watchdog service.
     * It will initialize scheduler, listener etc.
     */
    void start();

    /**
     * This will be called by scheduler/listener etc it will call watch method.
     * If there is anything notifiable it will create smart notification object it will call send notification
     * method.
     */
    void watchAndNotify();

    /**
     * This method will have actual watch logic, to send command, query, checking threshold values etc.
     */
    List<WatchdogWarnings> watch();

    /**
     * This method will send message to watch dog notification service.
     */
    void sendNotification(SmartNotificationEventType type, List<SmartNotificationEvent> events);

    /**
     * Can be used in future to check if the watchdog should run.
     * This can be used when watchdog become configurable.
     * It optional right now, can be removed.
     */
    default boolean shouldRun() {
        return true;
    }

    /**
     * This will convert WatchdogWarnings to SmartNotificationEvent
     */
    List<SmartNotificationEvent> assemble(List<WatchdogWarnings> warnings);

    /**
     * Returns watchdog name.
     */
    Watchdogs getName();
}
