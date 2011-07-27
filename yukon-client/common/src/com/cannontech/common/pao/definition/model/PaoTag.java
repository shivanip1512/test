package com.cannontech.common.pao.definition.model;

import static com.cannontech.core.roleproperties.InputTypeFactory.longType;
import static com.cannontech.core.roleproperties.InputTypeFactory.stringType;

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
	DEVICE_CONFIGURATION_470("Device Configuration 470"),
	DEVICE_CONFIGURATION_430("Device Configuration 430"),
	DEVICE_CONFIGURATION_420("Device Configuration 420"),
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
	MCT_200_SERIES("MCT 200 Series"),
	MCT_300_SERIES("MCT 300 Series"),
	USES_METER_NUMBER_FOR_MSP("Uses meter number for Msp"),
	VOLTAGE_REGULATOR("Voltage Regulator"),
	LM_GROUP("LM Group"),
	LM_PROGRAM("LM Program"),
    LM_CONTROL_AREA("LM Control Area"),
    LM_SCENARIO("LM Scenario"),
    DIRECT_PROGRAM_ENROLLMENT("Direct Program Enrollment"),
    SEP_PROGRAM_ENROLLMENT("SEP Program Enrollment"),
    DLC_ADDRESS_RANGE_ENFORCE("DLC Address Range Enforced", stringType()),  // range used for validation, may be mfg address or other(field length, for example).
    DLC_ADDRESS_RANGE("DLC Address Range", stringType()),   // range used for display info, should be limited to mfg addresses
	DUMMY_LONG_TAG("for internal testing", longType()),  // can be removed when we add a real tag that uses long
	WATER_METER_DETAIL_DISPLAYABLE("Water Detail Displayable"),
	DB_EDITOR_INCOMPATIBLE("Not Displayable in DB Editor"),
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
