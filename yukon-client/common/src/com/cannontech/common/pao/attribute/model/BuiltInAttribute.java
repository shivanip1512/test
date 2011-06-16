package com.cannontech.common.pao.attribute.model;


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
    KEEP_ALIVE_TIMER("Keep Alive Timer"),
    KVAR("kVAr"),
    KVARH("kVArh"),
    LM_GROUP_STATUS("LM Group Status"),
    LOAD_PROFILE("Load Profile", true, false),
    TAP_DOWN("Lower Tap Position"),
    MAXIMUM_VOLTAGE("Maximum Voltage"),
    MINIMUM_VOLTAGE("Minimum Voltage"),
    OUTAGE_LOG("Outage Log"),
    OUTAGE_STATUS("Outage Status"),
    PEAK_DEMAND("Peak Demand"),
    PHASE("Phase"),
    POWER_FAIL_FLAG("Power Fail Flag"),
    PROFILE_CHANNEL_2("Profile Channel 2", true, false),
    PROFILE_CHANNEL_3("Profile Channel 3", true, false),
    TAP_UP("Raise Tap Position"),
    REVERSE_POWER_FLAG("Reverse Power Flag"),
    TAMPER_FLAG("Tamper Flag"),
    TAP_POSITION("Tap Position"),
    TOU_RATE_A_PEAK_DEMAND("Tou Rate A Peak"),
    TOU_RATE_B_PEAK_DEMAND("Tou Rate B Peak"),
    TOU_RATE_C_PEAK_DEMAND("Tou Rate C Peak"),
    TOU_RATE_D_PEAK_DEMAND("Tou Rate D Peak"),
    TOU_RATE_A_USAGE("Tou Rate A Usage", false, true), 
    TOU_RATE_B_USAGE("Tou Rate B Usage", false, true), 
    TOU_RATE_C_USAGE("Tou Rate C Usage", false, true), 
    TOU_RATE_D_USAGE("Tou Rate D Usage", false, true), 
    USAGE("Usage Reading", false, true), 
    VOLTAGE("Voltage"), 
    VOLTAGE_X("Voltage X"), // Source Side
    VOLTAGE_Y("Voltage Y"), // Load Side
    TERMINATE("Terminate"),
    AUTO_BLOCK_ENABLE("Auto Block Enable"),
    VOLTAGE_PROFILE("Voltage Profile", true, false),
    USAGE_WATER("Water Usage Reading", false, true),
    ZERO_USAGE_FLAG("Zero Usage Flag"),
    ZIGBEE_CONNECTION_STATUS("ZigBee Connection Status"),
    ZIGBEE_LINK_STATUS("ZigBee Link Status"),
    ;

    private BuiltInAttribute(String description) {
    	this.description = description;
		this.profile = false;
		this.accumulator = false;
    }
    private BuiltInAttribute(String description, boolean profile, boolean accumulator) {
    	this.description = description;
		this.profile = profile;
		this.accumulator = accumulator;
    }
    
    private String description;
    private final boolean profile;
    private final boolean accumulator;	//point is an accumulation; Example: Usage 
    
    public String getDescription() {
        return description;
    }
    
    public boolean isProfile() {
		return profile;
	}

    public boolean isAccumulator() {
		return accumulator;
	}
    
    public String getKey() {
        return this.name();
    }

}