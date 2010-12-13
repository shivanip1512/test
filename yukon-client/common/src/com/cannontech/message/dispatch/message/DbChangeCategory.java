package com.cannontech.message.dispatch.message;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public enum DbChangeCategory {
    POINT("Point"),
    STATEGROUP("StateGroup"),
    NOTIFCATIONGROUP("NotificationGroup"),
    ALARMCATEGORY("AlarmCategory"),
    CUSTOMERCONTACT("CustomerContact"),
    GRAPH("Graph"),
    HOLIDAY_SCHEDULE("HolidaySchedule"),
    ENERGY_COMPANY("EnergyCompany"),
    ENERGY_COMPANY_DEFAULT_ROUTE("EnergyCompanyDefaultRoute"),
    APPLIANCE_CATEGORY("EnergyCompanyApplianceCategory"),
    YUKON_USER("YukonUser"),
    CUSTOMER("Customer"),
    CI_CUSTOMER("CICustomer"),
    YUKON_USER_GROUP("YukonUserGroup"),
    BASELINE("BaseLine"),
    CONFIG("Config"),
    TAG("Tag"),
    LMCONSTRAINT("Load Constraint"),
    LMSCENARIO("Load Scenario"),
    SEASON_SCHEDULE("Season Schedule"),
    DEVICETYPE_COMMAND("DeviceType Command"),
    COMMAND("Command"),
    TOU_SCHEDULE("TOU Schedule"),
    CBC_STRATEGY("CBC Strategy"),
    PAO_SCHEDULE("PAO Schedule"),
    SETTLEMENT("Settlement"),
    DEVICE_CONFIG("Device Config"),
    CUSTOMER_ACCOUNT("CustomerAccount"),
    LM_PROGRAM("LMProgram"),
    LM_HARDWARE("LMHardware"),
    APPLIANCE("Appliance"),
    CUSTOMER_LOGIN("CustomerLogin"),
    SERVICE_COMPANY("ServiceCompany"),
    SERVICE_COMPANY_DESIGNATION_CODE("ServiceCompanyDesignationCode"),
    WORK_ORDER("WorkOrder"),
    CBC_ADDINFO("CB Additional Info"),
    WEB_CONFIG("Web Configuration"),
    STARS_PUBLISHED_PROGRAM("Stars Published Program"),
    YUKON_SELECTION_LIST("YukonSelectionList"), 
    YUKON_LIST_ENTRY("YukonListEntry"),
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

    private DbChangeCategory(String representation) {
        this.representation = representation;
    }

    public String getStringRepresentation() {
        return representation;
    }
    
    public static DbChangeCategory findForStringRepresentation(String stringRepresentation) {
        return categoryLookup.get(stringRepresentation);
    }

}
