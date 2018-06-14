package com.cannontech.watchdog.base;

import java.util.List;

import com.cannontech.watchdog.model.WatchdogWarnings;
import com.cannontech.watchdog.model.Watchdogs;

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
     * Check's if watchdog/watcher has to be started or not.
     */
    default boolean shouldRun() {
        return true;
    }

    /**
     * Returns watchdog name.
     */
    Watchdogs getName();
}
