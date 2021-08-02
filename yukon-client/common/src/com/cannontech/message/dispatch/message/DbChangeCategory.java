package com.cannontech.message.dispatch.message;

import java.util.EnumSet;
import java.util.Set;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
/**
 * All original DBChangeCategory values were ported into this enum (from DBChangeMsg).
 * However, the original DBChangeMsg category values do not mix with the new DBChangeMsg handling.
 * To prevent mixing of old style with new style, 
 *  the old style enums have been commented out so they are not used.
 * When we only have one way of handling DBChangeMsg categories, 
 *  then the commented out enums will be uncommented for use.
 */

public enum DbChangeCategory {
    ENERGY_COMPANY("EnergyCompany"),
    ENERGY_COMPANY_ROUTE,
    ENERGY_COMPANY_SUBSTATIONS,
    APPLIANCE("Appliance"),
    SERVICE_COMPANY("ServiceCompany"),
    SERVICE_COMPANY_DESIGNATION_CODE("ServiceCompanyDesignationCode"),
    WAREHOUSE,
    YUKON_SELECTION_LIST("YukonSelectionList"), 
    YUKON_LIST_ENTRY("YukonListEntry"),
    CC_MONITOR_BANK_LIST,
    GLOBAL_SETTING("GlobalSetting"),
    ENERGY_COMPANY_SETTING("EnergyCompanySetting"),
    REPEATING_JOB("RepeatingJob"),
    DATA_EXPORT_FORMAT("DataExportFormat"),
    CHANGE_OPTOUT,
       
    DEVICE_DATA_MONITOR,
    OUTAGE_MONITOR,
    TAMPER_FLAG_MONITOR,
    STATUS_POINT_MONITOR,
    PORTER_RESPONSE_MONITOR,
    VALIDATION_MONITOR,
    DASHBOARD_ASSIGNMENT,
    MULTISPEAK,
    ATTRIBUTE,
    ATTRIBUTE_ASSIGNMENT,
    LOGGER
    ;
    
    
    
//  WORK_ORDER("WorkOrder"),
//  CBC_ADDINFO("CB Additional Info"),
//  WEB_CONFIG("Web Configuration"),
//  STARS_PUBLISHED_PROGRAM("Stars Published Program"),
//  CUSTOMER_LOGIN("CustomerLogin"),
//  YUKON_USER("YukonUser"),
//  CUSTOMER("Customer"),
//  CI_CUSTOMER("CICustomer"),
//  BASELINE("BaseLine"),
//  CONFIG("Config"),
//  TAG("Tag"),
//  LMCONSTRAINT("Load Constraint"),
//  LMSCENARIO("Load Scenario"),
//  SEASON_SCHEDULE("Season Schedule"),
//  DEVICETYPE_COMMAND("DeviceType Command"),
//  COMMAND("Command"),
//  TOU_SCHEDULE("TOU Schedule"),
//  CBC_STRATEGY("CBC Strategy"),
//  PAO_SCHEDULE("PAO Schedule"),
//  SETTLEMENT("Settlement"),
//  DEVICE_CONFIG("Device Config"),
//  CUSTOMER_ACCOUNT("CustomerAccount"),
//  LM_PROGRAM("LMProgram"),
//  LM_HARDWARE("LMHardware"),
//  POINT("Point"),
//  STATEGROUP("StateGroup"),
//  NOTIFCATIONGROUP("NotificationGroup"),
//  ALARMCATEGORY("AlarmCategory"),
//  CUSTOMERCONTACT("CustomerContact"),
//  GRAPH("Graph"),
//  HOLIDAY_SCHEDULE("HolidaySchedule"),

    ;
    
    private final String representation;
    private static final ImmutableMap<String, DbChangeCategory> categoryLookup;
    
    static {
        Builder<String, DbChangeCategory> builder = ImmutableMap.builder();
        for (DbChangeCategory category : values()) {
            builder.put(category.getStringRepresentation(), category);
        }
        categoryLookup = builder.build();
    }

    private DbChangeCategory() {
        this.representation = name();
    }
    
    private DbChangeCategory(String representation) {
        this.representation = representation;
    }

    public String getStringRepresentation() {
        return representation;
    }
    
    public static DbChangeCategory findForStringRepresentation(String stringRepresentation) {
        return categoryLookup.get(stringRepresentation);
    }

    /**
     * Helper to return a databaseId value that can be used with the legacy DBChangeMsg database Id values.
     */
    public int getDbChangeMsgDatabaseId() {
        return DBChangeMsg.USES_NEW_CATEGORY_ENUM - this.ordinal();
    }
    
    public static Set<DbChangeCategory> getMonitorCategories(){
        return EnumSet.of(DEVICE_DATA_MONITOR, OUTAGE_MONITOR, TAMPER_FLAG_MONITOR, STATUS_POINT_MONITOR,
            PORTER_RESPONSE_MONITOR, VALIDATION_MONITOR);
    }

    /**
     * Returns true when the event category is LOGGER .
     */
    public static boolean isDbChangeForLogger(DatabaseChangeEvent event) {
        return event.getChangeCategory() == LOGGER;
    }
}