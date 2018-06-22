package com.cannontech.watchdog.model;

import org.apache.commons.lang3.text.WordUtils;

import com.cannontech.common.i18n.DisplayableEnum;

public enum Watchdogs implements DisplayableEnum  {
    // TODO: Watchdogs will have to be added/removed from here
    SERVICE_STATUS, 
    DB_CONNECTION, 
    CRASH_DUMP,
    DISK_SPACE,
    CONFIG_FILE;
    
    private static final String keyBase = "yukon.web.widgets.watchdogWarnings.warningType.";

    @Override
    public String getFormatKey() {
        return keyBase + name();
    }

    @Override
    public String toString() {
        return WordUtils.capitalizeFully(name().toLowerCase().replaceAll("_", " "));
    }
}
