package com.cannontech.clientutils;

/**
 * @author rneuharth
 *
 * Contains all structures for logging that is needed
 *
 */
interface ILogging
{
    //Tomcat specific for now
    public static final String FILE_BASE = System.getProperty("catalina.base");

    public static final String DEFAULT_LOG_LEVEL = "INFO";

    //special case indexes for levels that do not have classes
    public static final int GENERAL_LOG_LEVEL = 15;
    public static final int LOG_TO_FILE = 16;
    

    public static final String[] LOG_LEVEL_NAMES =
    {
        "log_dbeditor_level",
        "log_database_level",
        "log_tdc_level",
        "log_commander_level",
        "log_billing_level",
        "log_calchist_level",
        "log_cap_control_level",
        "log_esub_level",  //7
        "log_export_level",
        "log_load_control_level",
        "log_macs_level",
        "log_notification_level",
        "log_reporting_level",
        "log_trending_level",  //13
        "log_stars_level",
        
        //items that do not have a class definition in ALL_NAMES
        "log_general_level",
        "log_to_file"
    };

    //a mapping of package objects to their log_level
    // contents of this array are mutable and mutated
    public static final String[][] ALL_NAMES = 
    {
        { "com.cannontech.dbeditor", DEFAULT_LOG_LEVEL },
        { "com.cannontech.database", DEFAULT_LOG_LEVEL },
        { "com.cannontech.tdc", DEFAULT_LOG_LEVEL },
        { "com.cannontech.yc", DEFAULT_LOG_LEVEL },
        { "com.cannontech.billing", DEFAULT_LOG_LEVEL },
        { "com.cannontech.calchist", DEFAULT_LOG_LEVEL },
        { "com.cannontech.cbc", DEFAULT_LOG_LEVEL },
        { "com.cannontech.esub", DEFAULT_LOG_LEVEL },  //7
        { "com.cannontech.export", DEFAULT_LOG_LEVEL },
        { "com.cannontech.loadcontrol", DEFAULT_LOG_LEVEL },
        { "com.cannontech.macs", DEFAULT_LOG_LEVEL },
        { "com.cannontech.notif", DEFAULT_LOG_LEVEL },
        { "com.cannontech.report", DEFAULT_LOG_LEVEL },
        { "com.cannontech.graph", DEFAULT_LOG_LEVEL },  //13
        { "com.cannontech.stars", DEFAULT_LOG_LEVEL },

        //items that do not have a class definition
        { "log_general_level", DEFAULT_LOG_LEVEL },
        { "log_to_file", "false" },
    };

}