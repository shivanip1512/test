package com.cannontech.common.util;

import java.util.Arrays;

import com.cannontech.common.stream.StreamUtils;
import com.google.common.collect.ImmutableMap;

public enum ApplicationId {
    
    //  Main applications
    COMMANDER("Commander"),
    DATABASE_EDITOR("DBEditor"),
    MESSAGE_BROKER("MessageBroker"),
    WATCHDOG("Watchdog"),
    NOTIFICATION("NotificationServer"),
    SERVICE_MANAGER("ServiceManager"),
    TABULAR_DISPLAY_CONSOLE("TDC"),
    TRENDING("Trending"), 
    WEBSERVER("Webserver"), 

    //  Standalone Tomcat services
    OPENADR("OpenADR"),
    WEB_SERVICES("WebServices"),  // aka EIM

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

    private final static ImmutableMap<String, ApplicationId> lookupByName = 
            ImmutableMap.copyOf(
                Arrays.stream(values()).collect(
                    StreamUtils.mapToSelf(ApplicationId::getApplicationName)));

     public static ApplicationId getByName(String name) {
        return lookupByName.getOrDefault(name, UNKNOWN);
     }

    private ApplicationId(String appName) {
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
