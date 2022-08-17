package com.cannontech.common.pao.definition.model;

import static com.cannontech.core.roleproperties.InputTypeFactory.*;

import com.cannontech.web.input.type.InputType;

public enum PaoTag {

    TOU("TOU"),
    HIGH_BILL("High Bill"),
    OUTAGE("Outage"),
    MOVE_SUPPORTED("Move Supported"),
    LOAD_PROFILE("Load Profile"),
    PEAK_REPORT("Peak Report"),
    VOLTAGE("Voltage"),
    VOLTAGE_PROFILE("Voltage Profile"),
    DEVICE_CONFIGURATION("Device Configuration"),
    DEVICE_ICON_TYPE("Device Icon Type", stringType()),
    DISCONNECT_COLLAR_COMPATIBLE("Disconnect Collar Compatible"),
    DISCONNECT_RFN("RFN Disconnect"),
    DISCONNECT_410("410 Disconnect"),
    DISCONNECT_310("310 Disconnect"),
    DISCONNECT_213("213 Disconnect"),
    METER_DETAIL_DISPLAYABLE("Meter Detail Displayable"),
    PHASE_DETECT("Phase Detect"),
    STARS_ACCOUNT_ATTACHABLE_METER("Stars Account Attachable Meter"),
    LOCATE_ROUTE("Locate Route"),
    PORTER_COMMAND_REQUESTS("Porter Command Requests"),
    NETWORK_MANAGER_ATTRIBUTE_READS("Network Manager Attribute Reads"),
    COMMANDER_REQUESTS("Commander Requests"),
    MCT_200_SERIES("MCT 200 Series"),
    MCT_300_SERIES("MCT 300 Series"),
    USES_METER_NUMBER_FOR_MSP("Uses meter number for Msp"),
    VOLTAGE_REGULATOR("Voltage Regulator"),
    LM_GROUP("LM Group"),
    LM_PROGRAM("LM Program"),
    LM_CONTROL_AREA("LM Control Area"),
    LM_SCENARIO("LM Scenario"),
    LM_SCHEDULE("LM Schedule"), //used to identify script/schedule that LM permissions can be given to
    DIRECT_PROGRAM_ENROLLMENT("Direct Program Enrollment"),
    SEP_PROGRAM_ENROLLMENT("SEP Program Enrollment"),
    DLC_ADDRESS_RANGE_ENFORCE("DLC Address Range Enforced", stringType()),  // range used for validation, may be mfg address or other(field length, for example).
    DLC_ADDRESS_RANGE("DLC Address Range", stringType()),   // range used for display info, should be limited to mfg addresses
    DUMMY_LONG_TAG("for internal testing", longType()),  // can be removed when we add a real tag that uses long
    WATER_METER_DETAIL_DISPLAYABLE("Water Detail Displayable"),
    GAS_METER_DETAIL_DISPLAYABLE("Gas Detail Displayable"),
    DB_EDITOR_INCOMPATIBLE("Not Displayable in Database Editor"),   // Excludes devices from DeviceTreeModel.java, hiding them in DB Editor and Commander
    ROUTE_ENCRYPTION("Supports Route Encryption"),
    THREE_PHASE_VOLTAGE("Supports Three Phase Voltage"),
    THREE_PHASE_CURRENT("Supports Three Phase Current"),
    RFN_EVENTS("Supports Rfn Events"),
    ONE_WAY_DEVICE("Supports One-Way Communication"),
    TWO_WAY_DEVICE("Supports Two-Way Communication"),
    IPC_METER("IPC Meter"),
    RFN_DEMAND_RESET("RFN Device Supporting Demand Reset"),
    PLC_DEMAND_RESET("PLC Device Supporting Demand Reset"),
    RFN_POINT_CALCULATION("RFN Point Calculation"), // This may need to be split into one tag per point and/or used for plc devices later
    ECOBEE_PROGRAM_ENROLLMENT("ecobee Program Enrollment"),
    HONEYWELL_PROGRAM_ENROLLMENT("Honeywell Program Enrollment"),
    NEST_PROGRAM_ENROLLMENT("Nest Program Enrollment"),
    METER_DISCONNECT_PROGRAM_ENROLLMENT("Meter Disconnect Program Enrollment"),
    ASSET_DETAIL_DISPLAYABLE("Asset Detail Displayable"),
    RELAY_DETAIL_DISPLAYABLE("Relay Detail Displayable"),
    RTU_DETAIL_DISPLAYABLE("RTU Detail Displayable"),
    GATEWAY_DETAIL_DISPLAYABLE("Gateway Detail Displayable"),
    ITRON_PROGRAM_ENROLLMENT("Itron Program Enrollment"),
    METER_PROGRAMMING("Meter Programming"),
    DATA_STREAMING("Data Streaming Capable")
    ;

    private final String description;
    private final InputType<?> valueType;
    
    PaoTag(String description) {
        this(description, null);
    }
    
    PaoTag(String description, InputType<?> valueType) {
        this.description = description;
        this.valueType = valueType;
    }
    
    public String getDescription() {
        return description;
    }
    
    public String getName() {
        return this.name();
    }
    
    public boolean isTagHasValue() {
        return valueType != null;
    }
    
    public InputType<?> getValueType() {
        return valueType;
    }
    
}