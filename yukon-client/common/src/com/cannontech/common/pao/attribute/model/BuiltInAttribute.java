package com.cannontech.common.pao.attribute.model;

import org.springframework.context.MessageSourceResolvable;

import com.cannontech.i18n.YukonMessageSourceResolvable;

public enum BuiltInAttribute implements Attribute {
    
    /* Keep this ordered alphabetically by description */
    AUTO_REMOTE_CONTROL("Auto/Remote Control"),
    BLINK_COUNT("Blink Count"), 
    CONNECTION_STATUS("Connection Status"),
    DEMAND("Demand"), 
    DISCONNECT_STATUS("Disconnect Status"),
    FAULT_STATUS("Fault Status"),
    GENERAL_ALARM_FLAG("General Alarm Flag"),
    KEEP_ALIVE("Keep Alive"),
    KVAR("kVAr"),
    KVARH("kVArh"),
    LM_GROUP_STATUS("LM Group Status"),
    LOAD_PROFILE("Load Profile", true),
    TAP_DOWN("Lower Tap Position"),
    MAXIMUM_VOLTAGE("Maximum Voltage"),
    MINIMUM_VOLTAGE("Minimum Voltage"),
    OUTAGE_LOG("Outage Log"),
    OUTAGE_STATUS("Outage Status"),
    PEAK_DEMAND("Peak Demand"),
    PHASE("Phase"),
    POWER_FAIL_FLAG("Power Fail Flag"),
    PROFILE_CHANNEL_2("Profile Channel 2", true),
    PROFILE_CHANNEL_3("Profile Channel 3", true),
    TAP_UP("Raise Tap Position"),
    REVERSE_POWER_FLAG("Reverse Power Flag"),
    TAMPER_FLAG("Tamper Flag"),
    TAP_POSITION("Tap Position"),
    TOU_RATE_A_PEAK_DEMAND("Tou Rate A Peak"),
    TOU_RATE_B_PEAK_DEMAND("Tou Rate B Peak"),
    TOU_RATE_C_PEAK_DEMAND("Tou Rate C Peak"),
    TOU_RATE_D_PEAK_DEMAND("Tou Rate D Peak"),
    TOU_RATE_A_USAGE("Tou Rate A Usage"), 
    TOU_RATE_B_USAGE("Tou Rate B Usage"), 
    TOU_RATE_C_USAGE("Tou Rate C Usage"), 
    TOU_RATE_D_USAGE("Tou Rate D Usage"), 
    USAGE("Usage Reading"), 
    VOLTAGE("Voltage"), 
    VOLTAGE_PROFILE("Voltage Profile", true),
    WATER_USAGE("Water Usage Reading"),
    ZERO_USAGE_FLAG("Zero Usage Flag"),
    ZIGBEE_LINK_STATUS("ZigBee Link Status"),
    ;
    
	private BuiltInAttribute(String description) {
        this(description, false);
    }
    
    private BuiltInAttribute(String description, boolean profile) {
    	this.description = description;
		this.profile = profile;
    }
    
    private String description;
    private final boolean profile;
    
    public String getDescription() {
        return description;
    }
    
    public boolean isProfile() {
		return profile;
	}
    
    public String getKey() {
        return this.name();
    }

}