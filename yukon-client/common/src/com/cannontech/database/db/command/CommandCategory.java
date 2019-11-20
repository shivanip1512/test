package com.cannontech.database.db.command;

import java.util.Map;

import com.cannontech.common.i18n.DisplayableEnum;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public enum CommandCategory implements DisplayableEnum {
    
    ALPHA_BASE("All Alpha Meters"),
    CBC_BASE("All CBCs"),
    CBC_ONEWAY("Oneway CBCs"),
    CBC_TWOWAY("Twoway CBCs"),
    CCU_BASE("All CCUs"),
    DISCONNECT_BASE("All Disconnect Meters"),
    LOAD_GROUP_BASE("All Load Group"),
    ION_BASE("All ION Meters"),
    LCU_BASE("All LCUs"),
    MCT_BASE("All MCTs"),
    IED_BASE("All IED Meters"),
    LP_BASE("All LP Meters"),
    RFN_BASE("All RFNs"),
    RTU_BASE("All RTUs"),
    REPEATER_BASE("All Repeaters"),
    TCU_BASE("All TCUs"),
    STATUSINPUT_BASE("All Status Input"),
    PING_BASE("All Ping-able"),
    MCT_4XX_SERIES_BASE("All MCT-4xx Series"),
    TWO_WAY_LCR_BASE("All Two Way LCR"),

    //Strings that commander uses to decide what "category" it is displaying, these are for specific items
    // that don't necessarily have a deviceType, or where deviceType doesn't help find the commands.    
    VERSACOM_SERIAL("VersacomSerial"),
    EXPRESSCOM_SERIAL("ExpresscomSerial"),
    SERIALNUMBER("LCRSerial"),
    DEVICE_GROUP("Device Group"),

    // Specific only to GRE, must add a role to see if these display
    SA205_SERIAL("SA205Serial"),
    SA305_SERIAL("SA305Serial");
    
    private final static Map<String, CommandCategory> lookupByDbString;

    static {
        Builder<String, CommandCategory> dbBuilder = ImmutableMap.builder();
        for (CommandCategory value : CommandCategory.values()) {
            dbBuilder.put(value.getDbString(), value);
        }
        lookupByDbString = dbBuilder.build();
    }
    private String dbString;
    
    private CommandCategory(String dbString) {
        this.dbString = dbString;
    }
    
    public String getDbString() {
        return dbString;
    }
    
    public static CommandCategory getForDbString(String dbString) {
        return lookupByDbString.get(dbString);
    }

    @Override
    public String getFormatKey() {
        return "yukon.web.modules.tools.commander.category." + name();
    }
}