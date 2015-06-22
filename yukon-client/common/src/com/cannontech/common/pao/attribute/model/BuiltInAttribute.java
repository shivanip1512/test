package com.cannontech.common.pao.attribute.model;

import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.context.MessageSourceResolvable;

import com.cannontech.common.i18n.Displayable;
import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Sets;

public enum BuiltInAttribute implements Attribute, DisplayableEnum {
    // NOTE: Remember to add any new attributes to point.xml for i18n'ing, too

    // This ordering is shared by our C++ code. 
    // Any changes/additions here need to be reflected there as well.
    
    BLINK_COUNT("Blink Count", AttributeGroup.BLINK_AND_OUTAGE),
    COMM_STATUS("Communication Status", AttributeGroup.STATUS, false),   // updated directly by port activity only
    CONTROL_POINT("Control Point", AttributeGroup.STATUS),
    CONTROL_STATUS("Control Status", AttributeGroup.STATUS),
    NEUTRAL_CURRENT("Current (Neutral)", AttributeGroup.CURRENT),
    CURRENT("Current", AttributeGroup.CURRENT, false),   //440 types only
    CURRENT_PHASE_A("Current (Phase A)", AttributeGroup.CURRENT),
    CURRENT_PHASE_B("Current (Phase B)", AttributeGroup.CURRENT),
    CURRENT_PHASE_C("Current (Phase C)", AttributeGroup.CURRENT),
    CURRENT_ANGLE_PHASE_A("Current Angle (Phase A)", AttributeGroup.CURRENT, false),   //440 types only
    CURRENT_ANGLE_PHASE_B("Current Angle (Phase B)", AttributeGroup.CURRENT, false),   //440 types only
    CURRENT_ANGLE_PHASE_C("Current Angle (Phase C)", AttributeGroup.CURRENT, false),   //440 types only
    CURRENT_WITHOUT_VOLTAGE_FLAG("Current Without Voltage", AttributeGroup.STATUS, false),   //440 types only
    DEMAND("Demand", AttributeGroup.DEMAND),
    DEMAND_PEAK_KVA_COIN("Demand at Peak kVa Coincidental", AttributeGroup.DEMAND),
    DISCONNECT_STATUS("Disconnect Status", AttributeGroup.STATUS),
    FAULT_STATUS("Fault Status", AttributeGroup.STATUS, false),
    FORWARD_INDUCTIVE_KVARH("Forward Inductive kVArh", AttributeGroup.REACTIVE, false),   //440 types only
    GENERAL_ALARM_FLAG("General Alarm Flag", AttributeGroup.STATUS),
    IED_DEMAND_RESET_COUNT("IED Demand Reset Count", AttributeGroup.DEMAND),
    // Treating "kVAh" as "Delivered kVAh". May need to created separate attributes in the future.
    KVAH("kVAh", AttributeGroup.USAGE),
    KVAH_RATE_A("kVAh Rate A", AttributeGroup.USAGE),
    KVAH_RATE_B("kVAh Rate B", AttributeGroup.USAGE),
    KVAH_RATE_C("kVAh Rate C", AttributeGroup.USAGE),
    KVAH_RATE_D("kVAh Rate D", AttributeGroup.USAGE),
    KVAR("kVAr", AttributeGroup.REACTIVE),
    // Treating "kVArh" as "Delivered kVArh". May need to created separate attributes in the future.
    KVARH("kVArh", AttributeGroup.REACTIVE),
    KVARH_RATE_A("kVArh Rate A", AttributeGroup.REACTIVE),
    KVARH_RATE_B("kVArh Rate B", AttributeGroup.REACTIVE),
    KVARH_RATE_C("kVArh Rate C", AttributeGroup.REACTIVE),
    KVARH_RATE_D("kVArh Rate D", AttributeGroup.REACTIVE),
    LM_GROUP_STATUS("LM Group Status", AttributeGroup.STATUS, false),
    LOAD_PROFILE("Load Profile", AttributeGroup.PROFILE, false),    //require extra input, not "attribute" based readable
    LOAD_SIDE_VOLTAGE_DETECTED_FLAG("Load Side Voltage Detected", AttributeGroup.STATUS, false),   //440 types only
    METER_BOX_COVER_REMOVAL_FLAG("Meter Box Cover Removal", AttributeGroup.STATUS, false),   //440 types only
    MAXIMUM_VOLTAGE("Maximum Voltage", AttributeGroup.VOLTAGE),
    MAXIMUM_VOLTAGE_FROZEN("Maximum Voltage Frozen", AttributeGroup.VOLTAGE),
    MINIMUM_VOLTAGE("Minimum Voltage", AttributeGroup.VOLTAGE),
    MINIMUM_VOLTAGE_FROZEN("Minimum Voltage Frozen", AttributeGroup.VOLTAGE),
    OUTAGE_LOG("Outage Log", AttributeGroup.BLINK_AND_OUTAGE),
    OUTAGE_STATUS("Outage Status", AttributeGroup.STATUS),
    OUT_OF_VOLTAGE_FLAG("Out of Voltage", AttributeGroup.STATUS, false),   //440 types only
    VOLTAGE_OUT_OF_LIMITS_FLAG("Voltage Out of Limits", AttributeGroup.STATUS, false),   //440 types only
    OVER_VOLTAGE("Over Voltage", AttributeGroup.RFN_HARDWARE_EVENT, false),
    OVER_VOLTAGE_MEASURED("Over Voltage Measured", AttributeGroup.RFN_HARDWARE_EVENT, false),
    OVER_VOLTAGE_THRESHOLD("Over Voltage Threshold", AttributeGroup.RFN_HARDWARE_EVENT, false),
    PEAK_DEMAND("Peak Demand", AttributeGroup.DEMAND),
    PEAK_DEMAND_FROZEN("Peak Demand Frozen", AttributeGroup.DEMAND),
    PEAK_DEMAND_RATE_A("Peak Demand Rate A", AttributeGroup.DEMAND),
    PEAK_DEMAND_FROZEN_RATE_A("Peak Demand Frozen Rate A", AttributeGroup.DEMAND),
    PEAK_DEMAND_RATE_B("Peak Demand Rate B", AttributeGroup.DEMAND),
    PEAK_DEMAND_FROZEN_RATE_B("Peak Demand Frozen Rate B", AttributeGroup.DEMAND),
    PEAK_DEMAND_RATE_C("Peak Demand Rate C", AttributeGroup.DEMAND),
    PEAK_DEMAND_FROZEN_RATE_C("Peak Demand Frozen Rate C", AttributeGroup.DEMAND),
    PEAK_DEMAND_RATE_D("Peak Demand Rate D", AttributeGroup.DEMAND),
    PEAK_DEMAND_FROZEN_RATE_D("Peak Demand Frozen Rate D", AttributeGroup.DEMAND),
    PEAK_DEMAND_RATE_E("Peak Demand Rate E", AttributeGroup.DEMAND),
    PEAK_KVA("Peak kVA", AttributeGroup.REACTIVE),
    PEAK_KVA_RATE_A("Peak kVA Rate A", AttributeGroup.REACTIVE),
    PEAK_KVA_RATE_B("Peak kVA Rate B", AttributeGroup.REACTIVE),
    PEAK_KVA_RATE_C("Peak kVA Rate C", AttributeGroup.REACTIVE),
    PEAK_KVA_RATE_D("Peak kVA Rate D", AttributeGroup.REACTIVE),
    PEAK_KVA_COIN("Peak kVA Coincidental", AttributeGroup.REACTIVE),
    PEAK_KVAR("Peak kVAr", AttributeGroup.REACTIVE),
    PEAK_KVAR_COIN("Peak kVAr Coincidental", AttributeGroup.REACTIVE),
    PEAK_KVAR_RATE_A("Peak kVAr Rate A", AttributeGroup.REACTIVE),
    PEAK_KVAR_RATE_B("Peak kVAr Rate B", AttributeGroup.REACTIVE),
    PEAK_KVAR_RATE_C("Peak kVAr Rate C", AttributeGroup.REACTIVE),
    PEAK_KVAR_RATE_D("Peak kVAr Rate D", AttributeGroup.REACTIVE),
    PEAK_KVARH("Peak kVArh", AttributeGroup.REACTIVE),
    PEAK_KVARH_COIN("Peak kVArh Coincidental", AttributeGroup.REACTIVE),
    PHASE("Phase", AttributeGroup.OTHER),
    POWER_FACTOR("Power Factor", AttributeGroup.REACTIVE),
    POWER_FACTOR_COIN("Power Factor Coincidental", AttributeGroup.REACTIVE),
    POWER_FACTOR_PHASE_A("Power Factor (Phase A)", AttributeGroup.REACTIVE),
    POWER_FACTOR_PHASE_B("Power Factor (Phase B)", AttributeGroup.REACTIVE),
    POWER_FACTOR_PHASE_C("Power Factor (Phase C)", AttributeGroup.REACTIVE),
    POWER_FAIL_FLAG("Power Fail Flag", AttributeGroup.STATUS),
    PROFILE_CHANNEL_2("Profile Channel 2", AttributeGroup.PROFILE, false),    //require extra input, not "attribute" based readable
    PROFILE_CHANNEL_3("Profile Channel 3", AttributeGroup.PROFILE, false),    //require extra input, not "attribute" based readable
    RECORDING_INTERVAL("Recording Interval", AttributeGroup.OTHER),
    HUMIDITY("Relative Humidity", AttributeGroup.OTHER, false),  //timer pulled data
    RELAY_1_LOAD_SIZE("Relay 1 kW Load Size", AttributeGroup.RELAY),
    RELAY_1_REMAINING_CONTROL("Relay 1 Remaining Control Time", AttributeGroup.RELAY),
    RELAY_1_RUN_TIME_DATA_LOG("Relay 1 Run Time", AttributeGroup.RELAY),
    RELAY_1_SHED_TIME_DATA_LOG("Relay 1 Shed Time", AttributeGroup.RELAY),
    RELAY_2_LOAD_SIZE("Relay 2 kW Load Size", AttributeGroup.RELAY),
    RELAY_2_REMAINING_CONTROL("Relay 2 Remaining Control Time", AttributeGroup.RELAY),
    RELAY_2_RUN_TIME_DATA_LOG("Relay 2 Run Time", AttributeGroup.RELAY),
    RELAY_2_SHED_TIME_DATA_LOG("Relay 2 Shed Time", AttributeGroup.RELAY),
    RELAY_3_LOAD_SIZE("Relay 3 kW Load Size", AttributeGroup.RELAY),
    RELAY_3_REMAINING_CONTROL("Relay 3 Remaining Control Time", AttributeGroup.RELAY),
    RELAY_3_RUN_TIME_DATA_LOG("Relay 3 Run Time", AttributeGroup.RELAY),
    RELAY_3_SHED_TIME_DATA_LOG("Relay 3 Shed Time", AttributeGroup.RELAY),
    RELAY_4_REMAINING_CONTROL("Relay 4 Remaining Control Time", AttributeGroup.RELAY),
    RELAY_4_RUN_TIME_DATA_LOG("Relay 4 Run Time", AttributeGroup.RELAY),
    RELAY_4_SHED_TIME_DATA_LOG("Relay 4 Shed Time", AttributeGroup.RELAY),
    REPORTING_INTERVAL("Reporting Interval", AttributeGroup.OTHER),
    REVERSE_INDUCTIVE_KVARH("Reverse Inductive kVArh", AttributeGroup.REACTIVE, false),   //440 types only
    REVERSE_POWER_FLAG("Reverse Power Flag", AttributeGroup.STATUS),
    RF_DEMAND_RESET_STATUS("RF Demand Reset Status", AttributeGroup.STATUS, false),
    SERVICE_STATUS("Service Status", AttributeGroup.STATUS),
    TAMPER_FLAG("Tamper Flag", AttributeGroup.STATUS),
    TEMPERATURE("Temperature", AttributeGroup.OTHER, false), //timer pulled data
    TEMPERATURE_DEVICE("Temperature of Device", AttributeGroup.RFN_HARDWARE_EVENT, false),
    TOTAL_LUF_COUNT("Total LUF Event Count", AttributeGroup.OTHER),
    TOTAL_LUV_COUNT("Total LUV Event Count", AttributeGroup.OTHER),
    UNDER_VOLTAGE("Under Voltage", AttributeGroup.RFN_HARDWARE_EVENT, false),
    UNDER_VOLTAGE_MEASURED("Under Voltage Measured", AttributeGroup.RFN_HARDWARE_EVENT, false),
    UNDER_VOLTAGE_THRESHOLD("Under Voltage Threshold", AttributeGroup.RFN_HARDWARE_EVENT, false),
    USAGE("Usage Reading", AttributeGroup.USAGE),
    USAGE_FROZEN("Usage Frozen", AttributeGroup.USAGE),
    USAGE_RATE_A("Usage Rate A", AttributeGroup.USAGE),
    USAGE_RATE_B("Usage Rate B", AttributeGroup.USAGE),
    USAGE_RATE_C("Usage Rate C", AttributeGroup.USAGE),
    USAGE_RATE_D("Usage Rate D", AttributeGroup.USAGE),
    USAGE_RATE_E("Usage Rate E", AttributeGroup.USAGE),
    USAGE_WATER("Water Usage Reading", AttributeGroup.USAGE, false),    //water not readable
    VOLTAGE("Voltage", AttributeGroup.VOLTAGE),
    VOLTAGE_PHASE_A("Voltage (Phase A)", AttributeGroup.VOLTAGE),
    VOLTAGE_PHASE_B("Voltage (Phase B)", AttributeGroup.VOLTAGE),
    VOLTAGE_PHASE_C("Voltage (Phase C)", AttributeGroup.VOLTAGE),
    VOLTAGE_PROFILE("Voltage Profile", AttributeGroup.PROFILE, false),    //require extra input, not "attribute" based readable
    WATT_HOUR_PULSE_FAILURE("Watt-Hour Pulse Failure", AttributeGroup.RFN_HARDWARE_EVENT, false),
    ZERO_USAGE_FLAG("Zero Usage Flag", AttributeGroup.STATUS),
    ZIGBEE_LINK_STATUS("ZigBee Link Status", AttributeGroup.STATUS, false),
    TERMINAL_BLOCK_COVER_REMOVAL_FLAG("Terminal Block Cover Removal", AttributeGroup.STATUS, false),   //440 types only
    INDOOR_TEMPERATURE("Indoor Temperature", AttributeGroup.OTHER),
    OUTDOOR_TEMPERATURE("Outdoor Temperature", AttributeGroup.OTHER),
    COOL_SET_TEMPERATURE("Cool Set Temperature", AttributeGroup.OTHER),
    HEAT_SET_TEMPERATURE("Heat Set Temperature", AttributeGroup.OTHER),

    DELIVERED_KWH("Delivered kWh", AttributeGroup.USAGE),
    // Delivered Rate x kWh is currently using USAGE_RATE_X attributes
    RECEIVED_KWH("Received kWh", AttributeGroup.USAGE),
    RECEIVED_KWH_FROZEN("Received kWh Frozen", AttributeGroup.USAGE, false),   //440 types only
    RECEIVED_KWH_RATE_A("Received kWh Rate A", AttributeGroup.USAGE),
    RECEIVED_KWH_RATE_B("Received kWh Rate B", AttributeGroup.USAGE),
    RECEIVED_KWH_RATE_C("Received kWh Rate C", AttributeGroup.USAGE),
    RECEIVED_KWH_RATE_D("Received kWh Rate D", AttributeGroup.USAGE),
    RECEIVED_KWH_RATE_E("Received kWh Rate E", AttributeGroup.USAGE),
    RECEIVED_KVAH("Received kVAh", AttributeGroup.USAGE),

    NET_KWH("Net kWh", AttributeGroup.USAGE),
    NET_KWH_RATE_A("Net kWh Rate A", AttributeGroup.USAGE),
    NET_KWH_RATE_B("Net kWh Rate B", AttributeGroup.USAGE),
    NET_KWH_RATE_C("Net kWh Rate C", AttributeGroup.USAGE),
    NET_KWH_RATE_D("Net kWh Rate D", AttributeGroup.USAGE),
    NET_KWH_RATE_E("Net kWh Rate E", AttributeGroup.USAGE),

    SUM_KWH("Sum kWh", AttributeGroup.USAGE),
    SUM_KWH_RATE_A("Sum kWh Rate A", AttributeGroup.USAGE),
    SUM_KWH_RATE_B("Sum kWh Rate B", AttributeGroup.USAGE),
    SUM_KWH_RATE_C("Sum kWh Rate C", AttributeGroup.USAGE),
    SUM_KWH_RATE_D("Sum kWh Rate D", AttributeGroup.USAGE),
    SUM_KVAH("Sum kVAh", AttributeGroup.USAGE),
    SUM_KVARH("Sum kVArh", AttributeGroup.USAGE),

    USAGE_PER_INTERVAL("Usage per Interval", AttributeGroup.USAGE, false),   //calculated, not readable
    DELIVERED_KWH_PER_INTERVAL("Delivered kWh per Interval", AttributeGroup.USAGE, false),   //calculated, not readable
    RECEIVED_KWH_PER_INTERVAL("Received kWh per Interval", AttributeGroup.USAGE, false),   //calculated, not readable
    SUM_KWH_PER_INTERVAL("Sum kWh per Interval", AttributeGroup.USAGE, false),   //calculated, not readable
    NET_KWH_PER_INTERVAL("Net kWh per Interval", AttributeGroup.USAGE, false),   //calculated, not readable
    SUM_KVAH_PER_INTERVAL("Sum kVAh per Interval", AttributeGroup.USAGE, false),   //calculated, not readable
    SUM_KVARH_PER_INTERVAL("Sum kVArh per Interval", AttributeGroup.REACTIVE, false),   //calculated, not readable
    WATER_USAGE_PER_INTERVAL("Water Usage per Interval", AttributeGroup.USAGE, false),   //calculated, water not readable
    FORWARD_INDUCTIVE_KVARH_PER_INTERVAL("Forward Inductive kVArh per Interval", AttributeGroup.REACTIVE, false),   //440 types only
    FORWARD_CAPACITIVE_KVARH_PER_INTERVAL("Forward Capacitive kVArh per Interval", AttributeGroup.REACTIVE, false),   //440 types only
    REVERSE_INDUCTIVE_KVARH_PER_INTERVAL("Reverse Inductive kVArh per Interval", AttributeGroup.REACTIVE, false),   //440 types only
    REVERSE_CAPACITIVE_KVARH_PER_INTERVAL("Reverse Capacitive kVArh per Interval", AttributeGroup.REACTIVE, false),   //440 types only

    DELIVERED_KW_LOAD_PROFILE("Delivered kW Load Profile", AttributeGroup.PROFILE, false),   //calculated, not readable
    DELIVERED_KVAR_LOAD_PROFILE("Delivered kVAr Load Profile", AttributeGroup.PROFILE, false),//calculated, not readable
    RECEIVED_KW_LOAD_PROFILE("Received kW Load Profile", AttributeGroup.PROFILE, false),   //calculated, not readable
    SUM_KW_LOAD_PROFILE("Sum kW Load Profile", AttributeGroup.PROFILE, false),   //calculated, not readable
    NET_KW_LOAD_PROFILE("Net kW Load Profile", AttributeGroup.PROFILE, false),   //calculated, not readable
    SUM_KVA_LOAD_PROFILE("Sum kVA Load Profile", AttributeGroup.PROFILE, false),   //calculated, not readable
    SUM_KVAR_LOAD_PROFILE("Sum kVAr Load Profile", AttributeGroup.PROFILE, false),   //calculated, not readable

    NET_DELIVERED_KVARH("Net Delivered kVArh", AttributeGroup.REACTIVE),
    NET_DELIVERED_KVARH_RATE_A("Net Delivered kVArh Rate A", AttributeGroup.REACTIVE),
    NET_DELIVERED_KVARH_RATE_B("Net Delivered kVArh Rate B", AttributeGroup.REACTIVE),
    NET_DELIVERED_KVARH_RATE_C("Net Delivered kVArh Rate C", AttributeGroup.REACTIVE),
    NET_DELIVERED_KVARH_RATE_D("Net Delivered kVArh Rate D", AttributeGroup.REACTIVE),

    RECEIVED_KVARH("Received kVArh", AttributeGroup.REACTIVE),
    RECEIVED_KVARH_RATE_A("Received kVArh Rate A", AttributeGroup.REACTIVE),
    RECEIVED_KVARH_RATE_B("Received kVArh Rate B", AttributeGroup.REACTIVE),
    RECEIVED_KVARH_RATE_C("Received kVArh Rate C", AttributeGroup.REACTIVE),
    RECEIVED_KVARH_RATE_D("Received kVArh Rate D", AttributeGroup.REACTIVE),
    NET_RECEIVED_KVARH("Net Received kVArh", AttributeGroup.REACTIVE),
    NET_RECEIVED_KVARH_RATE_A("Net Received kVArh Rate A", AttributeGroup.REACTIVE),
    NET_RECEIVED_KVARH_RATE_B("Net Received kVArh Rate B", AttributeGroup.REACTIVE),
    NET_RECEIVED_KVARH_RATE_C("Net Received kVArh Rate C", AttributeGroup.REACTIVE),
    NET_RECEIVED_KVARH_RATE_D("Net Received kVArh Rate D", AttributeGroup.REACTIVE),

    // RFN Events that map to Event Status points (this list must be kept in sync with both
    // our RFN set below AND its version in RfnConditionType.java. Outages and restores are
    // the only exception to this rule (as in the names don't exactly match))
    ALTERNATE_MODE_ENTRY("Alternate Mode Entry", AttributeGroup.RFN_SOFTWARE_EVENT, false),
    ANSI_SECURITY_FAILED("ANSI Security Failed", AttributeGroup.RFN_SOFTWARE_EVENT, false),
    BAD_UPGRADE_SECURITY_PARAM("Bad Upgrade Security Parameter", AttributeGroup.RFN_SOFTWARE_EVENT, false),
    CONFIGURATION_ERROR("Configuration Error", AttributeGroup.RFN_SOFTWARE_EVENT, false),
    CLOCK_ERROR("Clock Error", AttributeGroup.RFN_OTHER_EVENT, false),
    CRYSTAL_OSCILLATOR_ERROR("Crystal Oscillator Error", AttributeGroup.RFN_HARDWARE_EVENT, false),
    CURRENT_LOSS("Current Loss", AttributeGroup.RFN_CURRENT_EVENT, false),
    CURRENT_WAVEFORM_DISTORTION("Current Waveform Distortion", AttributeGroup.RFN_OTHER_EVENT, false),
    DEMAND_OVERLOAD("Demand Overload", AttributeGroup.RFN_DEMAND_EVENT, false),
    DEMAND_READS_AND_RESET("Demand Reads And Reset", AttributeGroup.RFN_DEMAND_EVENT, false),
    DEMAND_THRESHOLD_EXCEEDED_WARNING("Demand Threshold Exceeded Warning", AttributeGroup.RFN_DEMAND_EVENT, false),
    DNP3_ADDRESS_CHANGED("DNP3 Address Changed", AttributeGroup.RFN_OTHER_EVENT, false),
    DISPLAY_LOCKED_BY_WARNING("Display Locked By Warning", AttributeGroup.RFN_OTHER_EVENT, false),
    EEPROM_ACCESS_ERROR("Eeprom Access Error", AttributeGroup.RFN_HARDWARE_EVENT, false),
    ENCRYPTION_KEY_TABLE_CRC_ERROR("Encryption Key Table Crc Error", AttributeGroup.RFN_SOFTWARE_EVENT, false),
    END_OF_CALENDAR_WARNING("End Of Calendar Warning", AttributeGroup.RFN_SOFTWARE_EVENT, false),
    ENERGY_ACCUMULATED_WHILE_IN_STANDBY_MODE("Energy Accumulated While In Standby Mode", AttributeGroup.RFN_METERING_EVENT, false),
    FAILED_UPGRADE_SIGNATURE_VERIF("Failed Upgrade Signature Verification", AttributeGroup.RFN_SOFTWARE_EVENT, false),
    IMPROPER_METER_ENGINE_OPERATION_WARNING("Improper Meter Engine Operation Warning", AttributeGroup.RFN_HARDWARE_EVENT, false),
    INACTIVE_PHASE_CURRENT_DIAGNOSTIC_ERROR("Inactive Phase Current Diagnostic Error", AttributeGroup.RFN_OTHER_EVENT, false),
    INTERNAL_COMMUNICATION_ERROR("Internal Communication Error", AttributeGroup.RFN_HARDWARE_EVENT, false),
    INTERNAL_ERROR_FLAG("Internal Error Flag", AttributeGroup.RFN_HARDWARE_EVENT, false),   //440 types only
    INVALID_SERVICE("Invalid Service", AttributeGroup.RFN_SOFTWARE_EVENT, false),
    LINE_FREQUENCY_WARNING("Line Frequency Warning", AttributeGroup.RFN_METERING_EVENT, false),
    LOAD_SIDE_VOLTAGE_IS_MISSING("Load Side Voltage Is Missing", AttributeGroup.RFN_METERING_EVENT, false),
    LOSS_OF_ALL_CURRENT("Loss Of All Current", AttributeGroup.RFN_CURRENT_EVENT, false),
    LOSS_OF_PHASE_A_CURRENT("Loss Of Phase A Current", AttributeGroup.RFN_CURRENT_EVENT, false),
    LOSS_OF_PHASE_C_CURRENT("Loss Of Phase C Current", AttributeGroup.RFN_CURRENT_EVENT, false),
    LOW_BATTERY_WARNING("Low Battery Warning", AttributeGroup.RFN_HARDWARE_EVENT, false),
    LOW_LOSS_POTENTIAL("Low Loss Potential", AttributeGroup.RFN_METERING_EVENT, false),
    MASS_MEMORY_ERROR("Mass Memory Error", AttributeGroup.RFN_SOFTWARE_EVENT, false),
    MEASUREMENT_ERROR("Measurement Error", AttributeGroup.RFN_METERING_EVENT, false),
    METER_RECONFIGURE("Meter Reconfigure", AttributeGroup.RFN_HARDWARE_EVENT, false),
    METROLOGY_COMM_FAILURE("Metrology Communication Failure", AttributeGroup.RFN_OTHER_EVENT, false),
    NON_VOLATILE_MEM_FAILURE("Non Volatile Mem Failure", AttributeGroup.RFN_HARDWARE_EVENT, false),
    OUTSTATION_DNP3_SERCOMM_LOCKED("Outstation DNP3 SerComm Locked", AttributeGroup.RFN_OTHER_EVENT, false),
    PASSWORD_TABLE_CRC_ERROR("Password Table Crc Error", AttributeGroup.RFN_SOFTWARE_EVENT, false),
    PHASE_ANGLE_DISPLACEMENT("Phase Angle Displacement", AttributeGroup.RFN_OTHER_EVENT, false),
    PHASE_LOSS("Phase Loss", AttributeGroup.RFN_OTHER_EVENT, false),
    POLARITY_CROSS_PHASE_ENERGY_FLOW_DIAGNOSTIC("Polarity, Cross-phase and Energy Flow Diagnostic", AttributeGroup.RFN_OTHER_EVENT, false),
    POTENTIAL_INDICATOR_WARNING("Potential Indicator Warning", AttributeGroup.RFN_METERING_EVENT, false),
    POWER_FAIL_DATA_SAVE_ERROR("Power Fail Data Save Error", AttributeGroup.RFN_HARDWARE_EVENT, false),
    PQM_TEST_FAILURE_WARNING("Pqm Test Failure Warning", AttributeGroup.RFN_METERING_EVENT, false),
    RAM_ERROR("Ram Error", AttributeGroup.RFN_HARDWARE_EVENT, false),
    REGISTER_FULL_SCALE_EXCEEDED("Register Full-scale Exceeded", AttributeGroup.RFN_OTHER_EVENT, false),
    REVERSED_AGGREGATE("Reversed Aggregate", AttributeGroup.RFN_METERING_EVENT, false),
    REVERSED_PHASE_A("Reversed Phase A", AttributeGroup.RFN_METERING_EVENT, false),
    REVERSED_PHASE_C("Reversed Phase C", AttributeGroup.RFN_METERING_EVENT, false),
    RFN_BLINK_COUNT("Rfn Blink Count", AttributeGroup.BLINK_AND_OUTAGE, false),
    RFN_BLINK_RESTORE_COUNT("Rfn Blink Restore Count", AttributeGroup.BLINK_AND_OUTAGE, false),
    RFN_TEMPERATURE_ALARM("RFN High Temperature Alarm", AttributeGroup.RFN_HARDWARE_EVENT, false),
    RFN_OUTAGE_COUNT("Rfn Outage Count", AttributeGroup.BLINK_AND_OUTAGE, false),
    RFN_OUTAGE_RESTORE_COUNT("Rfn Outage Restore Count", AttributeGroup.BLINK_AND_OUTAGE, false),
    ROM_ERROR("Rom Error", AttributeGroup.RFN_HARDWARE_EVENT, false),
    SEASON_CHANGE("Season Change", AttributeGroup.RFN_OTHER_EVENT, false),
    SECURITY_CONFIGURATION_ERROR("Security Configuration Error", AttributeGroup.RFN_SOFTWARE_EVENT, false),
    SELF_CHECK_ERROR("Self Check Error", AttributeGroup.RFN_SOFTWARE_EVENT, false),
    SERVICE_CURRENT_TEST_FAILURE_WARNING("Service Current Test Failure Warning", AttributeGroup.RFN_METERING_EVENT, false),
    SERVICE_DISCONNECT_SWITCH_ERROR("Service Disconnect Switch Error", AttributeGroup.RFN_HARDWARE_EVENT, false),
    SERVICE_DISCONNECT_SWITCH_OPEN("Service Disconnect Switch Open", AttributeGroup.RFN_HARDWARE_EVENT, false),
    SERVICE_DISCONNECT_SWITCH_SENSOR_ERROR("Service Disconnect Switch Sensor Error", AttributeGroup.RFN_HARDWARE_EVENT, false),
    SITESCAN_ERROR("SiteScan Error", AttributeGroup.RFN_OTHER_EVENT, false),
    STUCK_SWITCH("Stuck Switch", AttributeGroup.RFN_HARDWARE_EVENT, false),
    TABLE_CRC_ERROR("Table Crc Error", AttributeGroup.RFN_SOFTWARE_EVENT, false),
    THD_V_OR_TDD_I_ERROR("THD V or TDD I Error", AttributeGroup.RFN_OTHER_EVENT, false),
    TIME_ADJUSTMENT("Time Adjustment", AttributeGroup.RFN_OTHER_EVENT, false),
    TIME_SYNC_FAILED("Time Sync Failed", AttributeGroup.RFN_OTHER_EVENT, false),
    TOU_SCHEDULE_ERROR("TOU Schedule Error", AttributeGroup.RFN_SOFTWARE_EVENT, false),
    UNCONFIGURED("Unconfigured", AttributeGroup.RFN_SOFTWARE_EVENT, false),
    UNPROGRAMMED("Unprogrammed", AttributeGroup.RFN_SOFTWARE_EVENT, false),
    USER_PROGRAMMABLE_TEMPERATURE_THRESHOLD_EXCEEDED("User Programmable Temperature Threshold Exceeded", AttributeGroup.RFN_METERING_EVENT, false),
    VOLTAGE_ALERTS("Voltage Alerts", AttributeGroup.RFN_METERING_EVENT, false),
    VOLTAGE_LOSS("Voltage Loss", AttributeGroup.RFN_VOLTAGE_EVENT, false),
    VOLTAGE_PHASE_A_OUT("Voltage Phase A Out", AttributeGroup.RFN_VOLTAGE_EVENT, false),
    VOLTAGE_PHASE_B_OUT("Voltage Phase B Out", AttributeGroup.RFN_VOLTAGE_EVENT, false),
    VOLTAGE_PHASE_C_OUT("Voltage Phase C Out", AttributeGroup.RFN_VOLTAGE_EVENT, false),
    VOLTAGE_PHASE_ERROR("Voltage Phase Error", AttributeGroup.RFN_METERING_EVENT, false),

    FIRMWARE_VERSION("Firmware Version", AttributeGroup.OTHER),
    IGNORED_CONTROL_REASON("Ignored Control Reason", AttributeGroup.OTHER),
    IP_ADDRESS("IP Address", AttributeGroup.OTHER),
    LAST_CONTROL_REASON("Last Control Reason", AttributeGroup.OTHER),
    NEUTRAL_CURRENT_SENSOR("Neutral Current Sensor", AttributeGroup.OTHER),
    SERIAL_NUMBER("Serial Number", AttributeGroup.OTHER),
    UDP_PORT("UDP Port", AttributeGroup.OTHER),
    
    // Estimated load
    CONNECTED_LOAD("Connected Load", AttributeGroup.ESTIMATED_LOAD, false), //calculated dr
    DIVERSIFIED_LOAD("Diversified Load", AttributeGroup.ESTIMATED_LOAD, false), //calculated dr
    MAX_LOAD_REDUCTION("Max Load Reduction", AttributeGroup.ESTIMATED_LOAD, false), //calculated dr
    AVAILABLE_LOAD_REDUCTION("Available Load Reduction", AttributeGroup.ESTIMATED_LOAD, false), //calculated dr
    ;

    private final String keyPrefix = "yukon.common.attribute.builtInAttribute.";

    // These are informational sets not used for display group purposes.
    private static Set<BuiltInAttribute> rfnEventTypes;
    private static Set<BuiltInAttribute> rfnEventStatusTypes;
    private static Set<BuiltInAttribute> rfnEventAnalogTypes;

    // These are both informational and used for display group purposes.
    private static Set<BuiltInAttribute> readableProfileAttributes;

    private static Set<BuiltInAttribute> readableAttributes;
    
    // The following maps and sets are used for displaying grouped attribute lists.
    private static Map<AttributeGroup, Set<BuiltInAttribute>> groupedDataAttributes;

    private static Map<AttributeGroup, Set<BuiltInAttribute>> groupedRfnEventAttributes;
    private static Set<BuiltInAttribute> rfnNonReadableEvents;

    private static Map<AttributeGroup, Set<BuiltInAttribute>> allGroupedAttributes;

    private final static ImmutableSetMultimap<AttributeGroup, BuiltInAttribute> lookupByGroup;
    static {

        ImmutableSetMultimap.Builder<AttributeGroup, BuiltInAttribute> builder = ImmutableSetMultimap.builder();
        ImmutableSet.Builder<BuiltInAttribute> readableBuilder = ImmutableSet.builder();
        
        for (BuiltInAttribute attribute : values()) {
            builder.put(attribute.getAttributeGroup(), attribute);
            
            if (attribute.isOnDemandReadable) {
                readableBuilder.add(attribute);
            }
        }
        lookupByGroup = builder.build();
        readableAttributes = readableBuilder.build();
        
        buildDataAttributeSets();
        buildRfnEventAttributeSets();
        buildAllAttributeGroups();
    }

    /**
     * Builds sets of data attributes.  A data attribute is an attribute that holds a value
     * or measurement.  Data attributes are sometimes called readable attributes since
     * they have numeric values which can be read.
     */
    private static void buildDataAttributeSets() {

        readableProfileAttributes = ImmutableSet.of(
                LOAD_PROFILE,
                PROFILE_CHANNEL_2,
                PROFILE_CHANNEL_3,
                VOLTAGE_PROFILE);

        // This map defines how attributes are grouped in drop downs and list selectors.
        // Used in conjunction with the selectNameValue or attributeSelector tag and groupItems="true".
        ImmutableMap.Builder<AttributeGroup, Set<BuiltInAttribute>> groupedDataAttributesBuilder =
                ImmutableMap.builder();

        groupedDataAttributesBuilder.put(AttributeGroup.BLINK_AND_OUTAGE, lookupByGroup.get(AttributeGroup.BLINK_AND_OUTAGE));
        groupedDataAttributesBuilder.put(AttributeGroup.CURRENT, lookupByGroup.get(AttributeGroup.CURRENT));
        groupedDataAttributesBuilder.put(AttributeGroup.DEMAND, lookupByGroup.get(AttributeGroup.DEMAND));
        groupedDataAttributesBuilder.put(AttributeGroup.OTHER, lookupByGroup.get(AttributeGroup.OTHER));
        groupedDataAttributesBuilder.put(AttributeGroup.PROFILE, lookupByGroup.get(AttributeGroup.PROFILE));
        groupedDataAttributesBuilder.put(AttributeGroup.REACTIVE, lookupByGroup.get(AttributeGroup.REACTIVE));
        groupedDataAttributesBuilder.put(AttributeGroup.RELAY, lookupByGroup.get(AttributeGroup.RELAY));
        groupedDataAttributesBuilder.put(AttributeGroup.STATUS, lookupByGroup.get(AttributeGroup.STATUS));
        groupedDataAttributesBuilder.put(AttributeGroup.USAGE, lookupByGroup.get(AttributeGroup.USAGE));
        groupedDataAttributesBuilder.put(AttributeGroup.VOLTAGE, lookupByGroup.get(AttributeGroup.VOLTAGE));
        groupedDataAttributesBuilder.put(AttributeGroup.ESTIMATED_LOAD, lookupByGroup.get(AttributeGroup.ESTIMATED_LOAD));

        groupedDataAttributes = groupedDataAttributesBuilder.build();
    }

    /**
     * This method builds ImmutableSet's of RFN event attributes.  An event attribute
     * indicates that some significant event has occurred on a device.
     */
    private static void buildRfnEventAttributeSets() {
        rfnEventStatusTypes = ImmutableSet.of(
                ALTERNATE_MODE_ENTRY,
                ANSI_SECURITY_FAILED,
                BAD_UPGRADE_SECURITY_PARAM,
                CLOCK_ERROR,
                CONFIGURATION_ERROR,
                CRYSTAL_OSCILLATOR_ERROR,
                CURRENT_LOSS,
                CURRENT_WAVEFORM_DISTORTION,
                DEMAND_OVERLOAD,
                DEMAND_READS_AND_RESET,
                DEMAND_THRESHOLD_EXCEEDED_WARNING,
                DISPLAY_LOCKED_BY_WARNING,
                EEPROM_ACCESS_ERROR,
                ENCRYPTION_KEY_TABLE_CRC_ERROR,
                END_OF_CALENDAR_WARNING,
                ENERGY_ACCUMULATED_WHILE_IN_STANDBY_MODE,
                FAILED_UPGRADE_SIGNATURE_VERIF,
                IMPROPER_METER_ENGINE_OPERATION_WARNING,
                INACTIVE_PHASE_CURRENT_DIAGNOSTIC_ERROR,
                INTERNAL_COMMUNICATION_ERROR,
                INTERNAL_ERROR_FLAG,
                INVALID_SERVICE,
                LINE_FREQUENCY_WARNING,
                LOAD_SIDE_VOLTAGE_IS_MISSING,
                LOSS_OF_ALL_CURRENT,
                LOSS_OF_PHASE_A_CURRENT,
                LOSS_OF_PHASE_C_CURRENT,
                LOW_BATTERY_WARNING,
                LOW_LOSS_POTENTIAL,
                MASS_MEMORY_ERROR,
                MEASUREMENT_ERROR,
                METER_RECONFIGURE,
                METROLOGY_COMM_FAILURE,
                NON_VOLATILE_MEM_FAILURE,
                OUTAGE_STATUS,                 //[PLC & RFN] Shared
                OVER_VOLTAGE,
                PASSWORD_TABLE_CRC_ERROR,
                PHASE_ANGLE_DISPLACEMENT,
                PHASE_LOSS,
                POLARITY_CROSS_PHASE_ENERGY_FLOW_DIAGNOSTIC,
                POTENTIAL_INDICATOR_WARNING,
                POWER_FAIL_DATA_SAVE_ERROR,
                POWER_FAIL_FLAG,             //[PLC & RFN] Shared
                PQM_TEST_FAILURE_WARNING,
                RAM_ERROR,
                REGISTER_FULL_SCALE_EXCEEDED,
                REVERSED_AGGREGATE,
                REVERSED_PHASE_A,
                REVERSED_PHASE_C,
                REVERSE_POWER_FLAG,          //[PLC & RFN] Shared
                RFN_TEMPERATURE_ALARM,
                ROM_ERROR,
                SEASON_CHANGE,
                SECURITY_CONFIGURATION_ERROR,
                SELF_CHECK_ERROR,
                SERVICE_CURRENT_TEST_FAILURE_WARNING,
                SERVICE_DISCONNECT_SWITCH_ERROR,
                SERVICE_DISCONNECT_SWITCH_OPEN,
                SERVICE_DISCONNECT_SWITCH_SENSOR_ERROR,
                SITESCAN_ERROR,
                STUCK_SWITCH,
                TABLE_CRC_ERROR,
                TAMPER_FLAG,                 //[PLC & RFN] Shared
                TIME_ADJUSTMENT,
                TIME_SYNC_FAILED,
                THD_V_OR_TDD_I_ERROR,
                TOU_SCHEDULE_ERROR,
                UNCONFIGURED,
                UNDER_VOLTAGE,
                UNPROGRAMMED,
                USER_PROGRAMMABLE_TEMPERATURE_THRESHOLD_EXCEEDED,
                VOLTAGE_ALERTS,
                VOLTAGE_LOSS,
                VOLTAGE_PHASE_A_OUT,
                VOLTAGE_PHASE_B_OUT,
                VOLTAGE_PHASE_C_OUT,
                VOLTAGE_PHASE_ERROR,
                WATT_HOUR_PULSE_FAILURE);

        rfnEventAnalogTypes = ImmutableSet.of(
                DNP3_ADDRESS_CHANGED,
                OUTSTATION_DNP3_SERCOMM_LOCKED,
                OVER_VOLTAGE_MEASURED,
                OVER_VOLTAGE_THRESHOLD,
                RFN_BLINK_COUNT,
                RFN_BLINK_RESTORE_COUNT,
                RFN_OUTAGE_COUNT,
                RFN_OUTAGE_RESTORE_COUNT,
                TEMPERATURE_DEVICE,
                UNDER_VOLTAGE_MEASURED,
                UNDER_VOLTAGE_THRESHOLD);

        Builder<BuiltInAttribute> builder = ImmutableSet.builder();
        builder.addAll(rfnEventStatusTypes);
        builder.addAll(rfnEventAnalogTypes);
        rfnEventTypes = builder.build();


        // This map defines how attributes are grouped in drop downs and list selectors.
        // Used in conjunction with the selectNameValue or attributeSelector tag and groupItems="true".
        ImmutableMap.Builder<AttributeGroup, Set<BuiltInAttribute>> groupedRfnEventBuilder = ImmutableMap.builder();

        groupedRfnEventBuilder.put(AttributeGroup.RFN_HARDWARE_EVENT, lookupByGroup.get(AttributeGroup.RFN_HARDWARE_EVENT));
        groupedRfnEventBuilder.put(AttributeGroup.RFN_SOFTWARE_EVENT, lookupByGroup.get(AttributeGroup.RFN_SOFTWARE_EVENT));
        groupedRfnEventBuilder.put(AttributeGroup.RFN_VOLTAGE_EVENT, lookupByGroup.get(AttributeGroup.RFN_VOLTAGE_EVENT));
        groupedRfnEventBuilder.put(AttributeGroup.RFN_CURRENT_EVENT, lookupByGroup.get(AttributeGroup.RFN_CURRENT_EVENT));
        groupedRfnEventBuilder.put(AttributeGroup.RFN_DEMAND_EVENT, lookupByGroup.get(AttributeGroup.RFN_DEMAND_EVENT));
        groupedRfnEventBuilder.put(AttributeGroup.RFN_OTHER_EVENT, lookupByGroup.get(AttributeGroup.RFN_OTHER_EVENT));;
        groupedRfnEventBuilder.put(AttributeGroup.RFN_METERING_EVENT, lookupByGroup.get(AttributeGroup.RFN_METERING_EVENT));

        groupedRfnEventAttributes = groupedRfnEventBuilder.build();

        ImmutableSet.Builder<BuiltInAttribute> nonReadableRfnEventBuilder = ImmutableSet.builder();
        nonReadableRfnEventBuilder.addAll(Sets.difference(BuiltInAttribute.getRfnEventTypes(),
                                    EnumSet.of(BuiltInAttribute.POWER_FAIL_FLAG,
                                               BuiltInAttribute.REVERSE_POWER_FLAG,
                                               BuiltInAttribute.TAMPER_FLAG,
                                               BuiltInAttribute.OUTAGE_STATUS)));
        rfnNonReadableEvents = nonReadableRfnEventBuilder.build();
    }

    /**
     * Group attributes in categories so they can be more easily displayed and
     * found in the UI.  Some data attribute groups and event attribute groups
     * are made into a composite group where there is some logical overlap as
     * in the case of the voltage & current groups.
     */
    private static void buildAllAttributeGroups() {
        ImmutableMap.Builder<AttributeGroup, Set<BuiltInAttribute>> allGroupedBuilder = ImmutableMap.builder();

        ImmutableSet.Builder<BuiltInAttribute> allCurrentAttributes = ImmutableSet.builder();
        allCurrentAttributes.addAll(lookupByGroup.get(AttributeGroup.CURRENT));
        allCurrentAttributes.addAll(lookupByGroup.get(AttributeGroup.RFN_CURRENT_EVENT));
        allGroupedBuilder.put(AttributeGroup.CURRENT, allCurrentAttributes.build());

        ImmutableSet.Builder<BuiltInAttribute> allVoltageAttributes = ImmutableSet.builder();
        allVoltageAttributes.addAll(lookupByGroup.get(AttributeGroup.VOLTAGE));
        allVoltageAttributes.addAll(lookupByGroup.get(AttributeGroup.RFN_VOLTAGE_EVENT));
        allGroupedBuilder.put(AttributeGroup.VOLTAGE, allVoltageAttributes.build());

        ImmutableSet.Builder<BuiltInAttribute> allDemandAttributes = ImmutableSet.builder();
        allDemandAttributes.addAll(lookupByGroup.get(AttributeGroup.DEMAND));
        allDemandAttributes.addAll(lookupByGroup.get(AttributeGroup.RFN_DEMAND_EVENT));
        allGroupedBuilder.put(AttributeGroup.DEMAND, allDemandAttributes.build());

        ImmutableSet.Builder<BuiltInAttribute> allOtherAttributes = ImmutableSet.builder();
        allOtherAttributes.addAll(lookupByGroup.get(AttributeGroup.OTHER));
        allOtherAttributes.addAll(lookupByGroup.get(AttributeGroup.RFN_OTHER_EVENT));
        allOtherAttributes.addAll(lookupByGroup.get(AttributeGroup.RFN_METERING_EVENT));
        allGroupedBuilder.put(AttributeGroup.OTHER, allOtherAttributes.build());

        allGroupedBuilder.put(AttributeGroup.BLINK_AND_OUTAGE, lookupByGroup.get(AttributeGroup.BLINK_AND_OUTAGE));
        allGroupedBuilder.put(AttributeGroup.PROFILE, lookupByGroup.get(AttributeGroup.PROFILE));
        allGroupedBuilder.put(AttributeGroup.REACTIVE, lookupByGroup.get(AttributeGroup.REACTIVE));
        allGroupedBuilder.put(AttributeGroup.RELAY, lookupByGroup.get(AttributeGroup.RELAY));
        allGroupedBuilder.put(AttributeGroup.STATUS, lookupByGroup.get(AttributeGroup.STATUS));
        allGroupedBuilder.put(AttributeGroup.USAGE, lookupByGroup.get(AttributeGroup.USAGE));
        allGroupedBuilder.put(AttributeGroup.ESTIMATED_LOAD, lookupByGroup.get(AttributeGroup.ESTIMATED_LOAD));

        allGroupedBuilder.put(AttributeGroup.RFN_HARDWARE_EVENT, lookupByGroup.get(AttributeGroup.RFN_HARDWARE_EVENT));
        allGroupedBuilder.put(AttributeGroup.RFN_SOFTWARE_EVENT, lookupByGroup.get(AttributeGroup.RFN_SOFTWARE_EVENT));

        // The attribute group map that is created can be used in conjunction with
        // the selectNameValue tag and groupItems="true".
        allGroupedAttributes = allGroupedBuilder.build();
    }

    private String defaultDescription;
    private AttributeGroup attributeGroup;
    private boolean isOnDemandReadable;

    /**
     * Defaults isOnDemandReadable to true
     */
    private BuiltInAttribute(String defaultDescription, AttributeGroup attributeGroup) {
        this(defaultDescription, attributeGroup, true);
    }

    private BuiltInAttribute(String defaultDescription, AttributeGroup attributeGroup, boolean isOnDemandReadable) {
        this.defaultDescription = defaultDescription;
        this.attributeGroup = attributeGroup;
        this.isOnDemandReadable = isOnDemandReadable;
    }

    public boolean isReadableProfile() {
        return readableProfileAttributes.contains(this);
    }

    public static Map<AttributeGroup, Set<BuiltInAttribute>> getStandardGroupedAttributes() {
        return groupedDataAttributes;
    }

    public static Map<AttributeGroup, Set<BuiltInAttribute>> getRfnEventGroupedAttributes() {
        return groupedRfnEventAttributes;
    }

    public static Map<AttributeGroup, Set<BuiltInAttribute>> getAllGroupedAttributes() {
        return allGroupedAttributes;
    }

    public static Set<BuiltInAttribute> getAccumulatorAttributes() {
        return lookupByGroup.get(AttributeGroup.USAGE);
    }

    public static Set<BuiltInAttribute> getRfnEventStatusTypes() {
        return rfnEventStatusTypes;
    }

    public static Set<BuiltInAttribute> getRfnEventAnalogTypes() {
        return rfnEventAnalogTypes;
    }

    public static Set<BuiltInAttribute> getRfnEventTypes() {
        return rfnEventTypes;
    }

    public static Set<BuiltInAttribute> getAttributesForGroup(AttributeGroup attributeGroup) {
        return lookupByGroup.get(attributeGroup);
    }

    /** Readable attributes (excludes profile attributes)*/
    public static Set<BuiltInAttribute> getReadableAttributes() {
        return readableAttributes;
    }

    /** Readable attributes + readable profile attributes */
    public static Set<BuiltInAttribute> getAdvancedReadableAttributes() {
        return Sets.union(readableAttributes, readableProfileAttributes);
    }

    public boolean isRfnEventType() {
        return rfnEventTypes.contains(this);
    }

    public boolean isRfnEventStatusType() {
        return rfnEventStatusTypes.contains(this);
    }

    public boolean isRfnEventAnalogType() {
        return rfnEventAnalogTypes.contains(this);
    }

    public boolean isRfnNonReadableEvent() {
        return rfnNonReadableEvents.contains(this);
    }

    @Override
    public String getKey() {
        return name();
    }

    @Override
    public String getFormatKey() {
        return keyPrefix + name();
    }

    @Override
    public MessageSourceResolvable getMessage() {
        return YukonMessageSourceResolvable.createDefault(keyPrefix + name(), defaultDescription);
    }

    public static void sort(List<BuiltInAttribute> attributes, final MessageSourceAccessor accessor) {
        Comparator<Displayable> comparator = new Comparator<Displayable>() {
            @Override
            public int compare(Displayable o1, Displayable o2) {
                return accessor.getMessage(o1.getMessage()).compareTo(accessor.getMessage(o2.getMessage()));
            }
        };
        Collections.sort(attributes, comparator);
    }

    /**
     * This method should not be used to display the name. Use getMessage() for i18n. This method
     * is used internally for logic that relies on these values remaining the same.
     */
    public String getDescription() {
        return defaultDescription;
    }
    
    public AttributeGroup getAttributeGroup() {
        return attributeGroup;
    }
}