package com.cannontech.watchdog.model;

import org.apache.commons.lang3.text.WordUtils;

public enum Watchdogs {
    // TODO: Watchdogs will have to be added/removed from here
    SERVICE_STATUS, 
    DB_CONNECTION, 
    CRASH_DUMP,
    DISK_SPACE,
    CONFIG_FILE;
    
    @Override
    public String toString() {
        return WordUtils.capitalizeFully(name().toLowerCase().replaceAll("_", " "));
    }
}
