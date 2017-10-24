package com.cannontech.common.util;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public enum ApplicationName {
    
    //  Main applications
    COMMANDER("Commander"),
    DATABASE_EDITOR("DBEditor"),
    MESSAGE_BROKER("MessageBroker"),
    NOTIFICATION("NotificationServer"),
    SERVICE_MANAGER("ServiceManager"),
    TABULAR_DISPLAY_CONSOLE("TDC"),
    TRENDING("Trending"), 
    WEBSERVER("Webserver"),
    YUKON_SYSTRAY("Yukon Systray"), 

    //  Database/upgrade tools
    DB_TOOLS_FRAME("DBToolsFrame"),
    DB_COMPARE("DBCompare"),
    DB_UPDATER("DBUpdater"),
    
    //  Placeholder
    UNKNOWN("UnknownApplication"), 
    
    //  Dev tools
    DEV_DATABASE_POPULATION("DevDatabasePopulationService"),
    SIMULATORS_SERVICE("SimulatorsService"),

    //  Misc utilities
    BILLING("Billing"),
    BILLING_FILE("BillingFile"),
    DELETE_INVENTORY("DeleteInventory"),
    BULK_METER_DELETER("BulkMeterDeleter"),
    CSV_TO_POINT_DATA("CSV2PointDataTool"),
    CUSTOM("Custom"),
    EXPORT("Export"),
    LOG_RAT("Rat"),
    MISSED_LIST_CONVERTER("MissListConverter"),
    POINT_HISTORY_CONVERTER("PHConverter"),
    POINT_IMPORT_UTILITY("PointImportUtility"),
    POINT_IMPORT_UTILITY_TOOL("PointImportUtilityTool"),
    SENSUS_FAULT("SensusFault"),
    SENSUS_GPUFF_DECODE("SensusGpuffDecode"),
    WEB_GRAPH("WebGraph")
    ;

    private final String applicationName;
    private final static ImmutableMap<String, ApplicationName> lookupByName;
    static {
        Builder<String, ApplicationName> byNameBuilder =
            ImmutableMap.builder();

        for (ApplicationName application : values()) {
            byNameBuilder.put(application.getApplicationName(), application);
        }
        lookupByName = byNameBuilder.build();
    }
    
    public static ApplicationName getByName(String name) {
        return lookupByName.get(name);
    }

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
