package com.cannontech.common.util;

public enum ApplicationName {

    COMMANDER("Commander"),
    DATABASE_EDITOR("DBEditor"),
    DELETE_INVENTORY("DeleteInventory"),
    DEV_DATABASE_POPULATION("DevDatabasePopulationService"),
    MESSAGE_BROKER("MessageBroker"),
    SERVICE_MANAGER("ServiceManager"),
    TABULAR_DISPLAY_CONSOLE("TDC"),
    TRENDING("Trending"), 
    UNKNOWN("UnknownApplication"), 
    WEBSERVER("Webserver"),
    YUKON_SYSTRAY("Yukon Systray")
    ;

    private final String applicationName;

    private ApplicationName(String appName) {
        this.applicationName = appName;
    }

    public String getApplicationName() {
        return this.applicationName;
    }

    @Override
    public String toString() {
        return this.applicationName;
    }
}
