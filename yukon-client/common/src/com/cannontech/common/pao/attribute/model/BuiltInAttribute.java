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
import com.google.common.collect.Sets;


public enum BuiltInAttribute implements Attribute, DisplayableEnum {
    // NOTE: Remember to add any new attributes to point.xml for i18n'ing, too

    // This ordering is shared by our C++ code. 
    // Any changes/additions here need to be reflected there as well.
    
    BLINK_COUNT("Blink Count"),
    COMM_STATUS("Communication Status"),
    CONNECTION_STATUS("Connection Status"),
    CONTROL_POINT("Control Point"),
    CONTROL_STATUS("Control Status"),
    NEUTRAL_CURRENT("Current (Neutral)"),
    CURRENT("Current"),
    CURRENT_PHASE_A("Current (Phase A)"),
    CURRENT_PHASE_B("Current (Phase B)"),
    CURRENT_PHASE_C("Current (Phase C)"),
    CURRENT_ANGLE_PHASE_A("Current Angle (Phase A)"),
    CURRENT_ANGLE_PHASE_B("Current Angle (Phase B)"),
    CURRENT_ANGLE_PHASE_C("Current Angle (Phase C)"),
    CURRENT_WITHOUT_VOLTAGE_FLAG("Current Without Voltage"),
    DEMAND("Demand"),
    DEMAND_PEAK_KVA_COIN("Demand at Peak kVa Coincidental"),
    DISCONNECT_STATUS("Disconnect Status"),
    FAULT_STATUS("Fault Status"),
    FORWARD_INDUCTIVE_KVARH("Forward Inductive kVArh"),
    GENERAL_ALARM_FLAG("General Alarm Flag"),
    IED_DEMAND_RESET_COUNT("IED Demand Reset Count"),
    // Treating "kVAh" as "Delivered kVAh". May need to created separate attributes in the future.
    KVAH("kVAh"),
    KVAH_RATE_A("kVAh Rate A"),
    KVAH_RATE_B("kVAh Rate B"),
    KVAH_RATE_C("kVAh Rate C"),
    KVAH_RATE_D("kVAh Rate D"),
    KVAR("kVAr"),
    // Treating "kVArh" as "Delivered kVArh". May need to created separate attributes in the future.
    KVARH("kVArh"),
    KVARH_RATE_A("kVArh Rate A"),
    KVARH_RATE_B("kVArh Rate B"),
    KVARH_RATE_C("kVArh Rate C"),
    KVARH_RATE_D("kVArh Rate D"),
    LM_GROUP_STATUS("LM Group Status"),
    LOAD_PROFILE("Load Profile"),
    LOAD_SIDE_VOLTAGE_DETECTED_FLAG("Load Side Voltage Detected"),
    METER_BOX_COVER_REMOVAL_FLAG("Meter Box Cover Removal"),
    MAXIMUM_VOLTAGE("Maximum Voltage"),
    MAXIMUM_VOLTAGE_FROZEN("Maximum Voltage Frozen"),
    MINIMUM_VOLTAGE("Minimum Voltage"),
    MINIMUM_VOLTAGE_FROZEN("Minimum Voltage Frozen"),
    OUTAGE_LOG("Outage Log"),
    OUTAGE_STATUS("Outage Status"),
    OUT_OF_VOLTAGE_FLAG("Out of Voltage"),
    VOLTAGE_OUT_OF_LIMITS_FLAG("Voltage Out of Limits"),
    OVER_VOLTAGE("Over Voltage"),
    OVER_VOLTAGE_MEASURED("Over Voltage Measured"),
    OVER_VOLTAGE_THRESHOLD("Over Voltage Threshold"),
    PEAK_DEMAND("Peak Demand"),
    PEAK_DEMAND_FROZEN("Peak Demand Frozen"),
    PEAK_DEMAND_RATE_A("Peak Demand Rate A"),
    PEAK_DEMAND_FROZEN_RATE_A("Peak Demand Frozen Rate A"),
    PEAK_DEMAND_RATE_B("Peak Demand Rate B"),
    PEAK_DEMAND_FROZEN_RATE_B("Peak Demand Frozen Rate B"),
    PEAK_DEMAND_RATE_C("Peak Demand Rate C"),
    PEAK_DEMAND_FROZEN_RATE_C("Peak Demand Frozen Rate C"),
    PEAK_DEMAND_RATE_D("Peak Demand Rate D"),
    PEAK_DEMAND_FROZEN_RATE_D("Peak Demand Frozen Rate D"),
    PEAK_DEMAND_RATE_E("Peak Demand Rate E"),
    PEAK_KVA("Peak kVA"),
    PEAK_KVA_RATE_A("Peak kVA Rate A"),
    PEAK_KVA_RATE_B("Peak kVA Rate B"),
    PEAK_KVA_RATE_C("Peak kVA Rate C"),
    PEAK_KVA_RATE_D("Peak kVA Rate D"),
    PEAK_KVA_COIN("Peak kVA Coincidental"),
    PEAK_KVAR("Peak kVAr"),
    PEAK_KVAR_COIN("Peak kVAr Coincidental"),
    PEAK_KVAR_RATE_A("Peak kVAr Rate A"),
    PEAK_KVAR_RATE_B("Peak kVAr Rate B"),
    PEAK_KVAR_RATE_C("Peak kVAr Rate C"),
    PEAK_KVAR_RATE_D("Peak kVAr Rate D"),
    PEAK_KVARH("Peak kVArh"),
    PEAK_KVARH_COIN("Peak kVArh Coincidental"),
    PHASE("Phase"),
    POWER_FACTOR("Power Factor"),
    POWER_FACTOR_COIN("Power Factor Coincidental"),
    POWER_FACTOR_PHASE_A("Power Factor (Phase A)"),
    POWER_FACTOR_PHASE_B("Power Factor (Phase B)"),
    POWER_FACTOR_PHASE_C("Power Factor (Phase C)"),
    POWER_FAIL_FLAG("Power Fail Flag"),
    PROFILE_CHANNEL_2("Profile Channel 2"),
    PROFILE_CHANNEL_3("Profile Channel 3"),
    RECORDING_INTERVAL("Recording Interval"),
    HUMIDITY("Relative Humidity"),
    RELAY_1_LOAD_SIZE("Relay 1 kW Load Size"),
    RELAY_1_REMAINING_CONTROL("Relay 1 Remaining Control Time"),
    RELAY_1_RUN_TIME_DATA_LOG("Relay 1 Run Time"),
    RELAY_1_SHED_TIME_DATA_LOG("Relay 1 Shed Time"),
    RELAY_2_LOAD_SIZE("Relay 2 kW Load Size"),
    RELAY_2_REMAINING_CONTROL("Relay 2 Remaining Control Time"),
    RELAY_2_RUN_TIME_DATA_LOG("Relay 2 Run Time"),
    RELAY_2_SHED_TIME_DATA_LOG("Relay 2 Shed Time"),
    RELAY_3_LOAD_SIZE("Relay 3 kW Load Size"),
    RELAY_3_REMAINING_CONTROL("Relay 3 Remaining Control Time"),
    RELAY_3_RUN_TIME_DATA_LOG("Relay 3 Run Time"),
    RELAY_3_SHED_TIME_DATA_LOG("Relay 3 Shed Time"),
    RELAY_4_REMAINING_CONTROL("Relay 4 Remaining Control Time"),
    RELAY_4_RUN_TIME_DATA_LOG("Relay 4 Run Time"),
    RELAY_4_SHED_TIME_DATA_LOG("Relay 4 Shed Time"),
    REPORTING_INTERVAL("Reporting Interval"),
    REVERSE_INDUCTIVE_KVARH("Reverse Inductive kVArh"),
    REVERSE_POWER_FLAG("Reverse Power Flag"),
    RF_DEMAND_RESET_STATUS("RF Demand Reset Status"),
    SERVICE_STATUS("Service Status"),
    TAMPER_FLAG("Tamper Flag"),
    TEMPERATURE("Temperature"),
    TEMPERATURE_DEVICE("Temperature of Device"),
    TOTAL_LUF_COUNT("Total LUF Event Count"),
    TOTAL_LUV_COUNT("Total LUV Event Count"),
    UNDER_VOLTAGE("Under Voltage"),
    UNDER_VOLTAGE_MEASURED("Under Voltage Measured"),
    UNDER_VOLTAGE_THRESHOLD("Under Voltage Threshold"),
    USAGE("Usage Reading"),
    USAGE_FROZEN("Usage Frozen"),
    USAGE_RATE_A("Usage Rate A"),
    USAGE_RATE_B("Usage Rate B"),
    USAGE_RATE_C("Usage Rate C"),
    USAGE_RATE_D("Usage Rate D"),
    USAGE_RATE_E("Usage Rate E"),
    USAGE_WATER("Water Usage Reading"),
    VOLTAGE("Voltage"),
    VOLTAGE_PHASE_A("Voltage (Phase A)"),
    VOLTAGE_PHASE_B("Voltage (Phase B)"),
    VOLTAGE_PHASE_C("Voltage (Phase C)"),
    VOLTAGE_PROFILE("Voltage Profile"),
    WATT_HOUR_PULSE_FAILURE("Watt-Hour Pulse Failure"),
    ZERO_USAGE_FLAG("Zero Usage Flag"),
    ZIGBEE_LINK_STATUS("ZigBee Link Status"),
    TERMINAL_BLOCK_COVER_REMOVAL_FLAG("Terminal Block Cover Removal"),
    INDOOR_TEMPERATURE("Indoor Temperature"),
    OUTDOOR_TEMPERATURE("Outdoor Temperature"),
    COOL_SET_TEMPERATURE("Cool Set Temperature"),
    HEAT_SET_TEMPERATURE("Heat Set Temperature"),

    DELIVERED_KWH("Delivered kWh"),
    // Delivered Rate x kWh is currently using USAGE_RATE_X attributes
    RECEIVED_KWH("Received kWh"),
    RECEIVED_KWH_FROZEN("Received kWh Frozen"),
    RECEIVED_KWH_RATE_A("Received kWh Rate A"),
    RECEIVED_KWH_RATE_B("Received kWh Rate B"),
    RECEIVED_KWH_RATE_C("Received kWh Rate C"),
    RECEIVED_KWH_RATE_D("Received kWh Rate D"),
    RECEIVED_KWH_RATE_E("Received kWh Rate E"),
    RECEIVED_KVAH("Received kVAh"),

    NET_KWH("Net kWh"),
    NET_KWH_RATE_A("Net kWh Rate A"),
    NET_KWH_RATE_B("Net kWh Rate B"),
    NET_KWH_RATE_C("Net kWh Rate C"),
    NET_KWH_RATE_D("Net kWh Rate D"),
    NET_KWH_RATE_E("Net kWh Rate E"),

    SUM_KWH("Sum kWh"),
    SUM_KWH_RATE_A("Sum kWh Rate A"),
    SUM_KWH_RATE_B("Sum kWh Rate B"),
    SUM_KWH_RATE_C("Sum kWh Rate C"),
    SUM_KWH_RATE_D("Sum kWh Rate D"),
    SUM_KVAH("Sum kVAh"),
    SUM_KVARH("Sum kVArh"),

    USAGE_PER_INTERVAL("Usage per Interval"),
    DELIVERED_KWH_PER_INTERVAL("Delivered kWh per Interval"),
    RECEIVED_KWH_PER_INTERVAL("Received kWh per Interval"),
    SUM_KWH_PER_INTERVAL("Sum kWh per Interval"),
    NET_KWH_PER_INTERVAL("Net kWh per Interval"),
    SUM_KVAH_PER_INTERVAL("Sum kVAh per Interval"),
    SUM_KVARH_PER_INTERVAL("Sum kVArh per Interval"),
    WATER_USAGE_PER_INTERVAL("Water Usage per Interval"),
    FORWARD_INDUCTIVE_KVARH_PER_INTERVAL("Forward Inductive kVArh per Interval"),
    FORWARD_CAPACITIVE_KVARH_PER_INTERVAL("Forward Capacitive kVArh per Interval"),
    REVERSE_INDUCTIVE_KVARH_PER_INTERVAL("Reverse Inductive kVArh per Interval"),
    REVERSE_CAPACITIVE_KVARH_PER_INTERVAL("Reverse Capacitive kVArh per Interval"),

    DELIVERED_KW_LOAD_PROFILE("Delivered kW Load Profile"),
    RECEIVED_KW_LOAD_PROFILE("Received kW Load Profile"),
    SUM_KW_LOAD_PROFILE("Sum kW Load Profile"),
    NET_KW_LOAD_PROFILE("Net kW Load Profile"),
    SUM_KVA_LOAD_PROFILE("Sum kVA Load Profile"),
    SUM_KVAR_LOAD_PROFILE("Sum kVAr Load Profile"),

    NET_DELIVERED_KVARH("Net Delivered kVArh"),
    NET_DELIVERED_KVARH_RATE_A("Net Delivered kVArh Rate A"),
    NET_DELIVERED_KVARH_RATE_B("Net Delivered kVArh Rate B"),
    NET_DELIVERED_KVARH_RATE_C("Net Delivered kVArh Rate C"),
    NET_DELIVERED_KVARH_RATE_D("Net Delivered kVArh Rate D"),

    RECEIVED_KVARH("Received kVArh"),
    RECEIVED_KVARH_RATE_A("Received kVArh Rate A"),
    RECEIVED_KVARH_RATE_B("Received kVArh Rate B"),
    RECEIVED_KVARH_RATE_C("Received kVArh Rate C"),
    RECEIVED_KVARH_RATE_D("Received kVArh Rate D"),
    NET_RECEIVED_KVARH("Net Received kVArh"),
    NET_RECEIVED_KVARH_RATE_A("Net Received kVArh Rate A"),
    NET_RECEIVED_KVARH_RATE_B("Net Received kVArh Rate B"),
    NET_RECEIVED_KVARH_RATE_C("Net Received kVArh Rate C"),
    NET_RECEIVED_KVARH_RATE_D("Net Received kVArh Rate D"),

    // RFN Events that map to Event Status points (this list must be kept in sync with both
    // our RFN set below AND its version in RfnConditionType.java. Outages and restores are
    // the only exception to this rule (as in the names don't exactly match))
    ALTERNATE_MODE_ENTRY("Alternate Mode Entry"),
    ANSI_SECURITY_FAILED("ANSI Security Failed"),
    BAD_UPGRADE_SECURITY_PARAM("Bad Upgrade Security Parameter"),
    CONFIGURATION_ERROR("Configuration Error"),
    CLOCK_ERROR("Clock Error"),
    CRYSTAL_OSCILLATOR_ERROR("Crystal Oscillator Error"),
    CURRENT_LOSS("Current Loss"),
    CURRENT_WAVEFORM_DISTORTION("Current Waveform Distortion"),
    DEMAND_OVERLOAD("Demand Overload"),
    DEMAND_READS_AND_RESET("Demand Reads And Reset"),
    DEMAND_THRESHOLD_EXCEEDED_WARNING("Demand Threshold Exceeded Warning"),
    DNP3_ADDRESS_CHANGED("DNP3 Address Changed"),
    DISPLAY_LOCKED_BY_WARNING("Display Locked By Warning"),
    EEPROM_ACCESS_ERROR("Eeprom Access Error"),
    ENCRYPTION_KEY_TABLE_CRC_ERROR("Encryption Key Table Crc Error"),
    END_OF_CALENDAR_WARNING("End Of Calendar Warning"),
    ENERGY_ACCUMULATED_WHILE_IN_STANDBY_MODE("Energy Accumulated While In Standby Mode"),
    FAILED_UPGRADE_SIGNATURE_VERIF("Failed Upgrade Signature Verification"),
    IMPROPER_METER_ENGINE_OPERATION_WARNING("Improper Meter Engine Operation Warning"),
    INACTIVE_PHASE_CURRENT_DIAGNOSTIC_ERROR("Inactive Phase Current Diagnostic Error"),
    INTERNAL_COMMUNICATION_ERROR("Internal Communication Error"),
    INTERNAL_ERROR_FLAG("Internal Error Flag"),
    INVALID_SERVICE("Invalid Service"),
    LINE_FREQUENCY_WARNING("Line Frequency Warning"),
    LOAD_SIDE_VOLTAGE_IS_MISSING("Load Side Voltage Is Missing"),
    LOSS_OF_ALL_CURRENT("Loss Of All Current"),
    LOSS_OF_PHASE_A_CURRENT("Loss Of Phase A Current"),
    LOSS_OF_PHASE_C_CURRENT("Loss Of Phase C Current"),
    LOW_BATTERY_WARNING("Low Battery Warning"),
    LOW_LOSS_POTENTIAL("Low Loss Potential"),
    MASS_MEMORY_ERROR("Mass Memory Error"),
    MEASUREMENT_ERROR("Measurement Error"),
    METER_RECONFIGURE("Meter Reconfigure"),
    METROLOGY_COMM_FAILURE("Metrology Communication Failure"),
    NON_VOLATILE_MEM_FAILURE("Non Volatile Mem Failure"),
    OUTSTATION_DNP3_SERCOMM_LOCKED("Outstation DNP3 SerComm Locked"),
    PASSWORD_TABLE_CRC_ERROR("Password Table Crc Error"),
    PHASE_ANGLE_DISPLACEMENT("Phase Angle Displacement"),
    PHASE_LOSS("Phase Loss"),
    POLARITY_CROSS_PHASE_ENERGY_FLOW_DIAGNOSTIC("Polarity, Cross-phase and Energy Flow Diagnostic"),
    POTENTIAL_INDICATOR_WARNING("Potential Indicator Warning"),
    POWER_FAIL_DATA_SAVE_ERROR("Power Fail Data Save Error"),
    PQM_TEST_FAILURE_WARNING("Pqm Test Failure Warning"),
    RAM_ERROR("Ram Error"),
    REGISTER_FULL_SCALE_EXCEEDED("Register Full-scale Exceeded"),
    REVERSED_AGGREGATE("Reversed Aggregate"),
    REVERSED_PHASE_A("Reversed Phase A"),
    REVERSED_PHASE_C("Reversed Phase C"),
    RFN_BLINK_COUNT("Rfn Blink Count"),
    RFN_BLINK_RESTORE_COUNT("Rfn Blink Restore Count"),
    RFN_TEMPERATURE_ALARM("RFN High Temperature Alarm"),
    RFN_OUTAGE_COUNT("Rfn Outage Count"),
    RFN_OUTAGE_RESTORE_COUNT("Rfn Outage Restore Count"),
    ROM_ERROR("Rom Error"),
    SEASON_CHANGE("Season Change"),
    SECURITY_CONFIGURATION_ERROR("Security Configuration Error"),
    SELF_CHECK_ERROR("Self Check Error"),
    SERVICE_CURRENT_TEST_FAILURE_WARNING("Service Current Test Failure Warning"),
    SERVICE_DISCONNECT_SWITCH_ERROR("Service Disconnect Switch Error"),
    SERVICE_DISCONNECT_SWITCH_OPEN("Service Disconnect Switch Open"),
    SERVICE_DISCONNECT_SWITCH_SENSOR_ERROR("Service Disconnect Switch Sensor Error"),
    SITESCAN_ERROR("SiteScan Error"),
    STUCK_SWITCH("Stuck Switch"),
    TABLE_CRC_ERROR("Table Crc Error"),
    THD_V_OR_TDD_I_ERROR("THD V or TDD I Error"),
    TIME_ADJUSTMENT("Time Adjustment"),
    TIME_SYNC_FAILED("Time Sync Failed"),
    TOU_SCHEDULE_ERROR("TOU Schedule Error"),
    UNCONFIGURED("Unconfigured"),
    UNPROGRAMMED("Unprogrammed"),
    USER_PROGRAMMABLE_TEMPERATURE_THRESHOLD_EXCEEDED("User Programmable Temperature Threshold Exceeded"),
    VOLTAGE_ALERTS("Voltage Alerts"),
    VOLTAGE_LOSS("Voltage Loss"),
    VOLTAGE_PHASE_A_OUT("Voltage Phase A Out"),
    VOLTAGE_PHASE_B_OUT("Voltage Phase B Out"),
    VOLTAGE_PHASE_C_OUT("Voltage Phase C Out"),
    VOLTAGE_PHASE_ERROR("Voltage Phase Error"),

    FIRMWARE_VERSION("Firmware Version"),
    IGNORED_CONTROL_REASON("Ignored Control Reason"),
    IP_ADDRESS("IP Address"),
    LAST_CONTROL_REASON("Last Control Reason"),
    NEUTRAL_CURRENT_SENSOR("Neutral Current Sensor"),
    SERIAL_NUMBER("Serial Number"),
    UDP_PORT("UDP Port"),
    
    // Estimated load
    CONNECTED_LOAD("Connected Load"),
    DIVERSIFIED_LOAD("Diversified Load"),
    MAX_LOAD_REDUCTION("Max Load Reduction"),
    AVAILABLE_LOAD_REDUCTION("Available Load Reduction"),
    ;

    private final String keyPrefix = "yukon.common.attribute.builtInAttribute.";

    // These are informational sets not used for display group purposes.
    private static Set<BuiltInAttribute> rfnEventTypes;
    private static Set<BuiltInAttribute> rfnEventStatusTypes;
    private static Set<BuiltInAttribute> rfnEventAnalogTypes;

    // These are both informational and used for display group purposes.
    private static Set<BuiltInAttribute> profileAttributes;
    private static Set<BuiltInAttribute> readableProfileAttributes;
    private static Set<BuiltInAttribute> accumulatorAttributes;

    // The following maps and sets are used for displaying grouped attribute lists.
    private static Map<AttributeGroup, Set<BuiltInAttribute>> groupedDataAttributes;
    private static Set<BuiltInAttribute> otherAttributes;
    private static Set<BuiltInAttribute> demandAttributes;
    private static Set<BuiltInAttribute> voltageAttributes;
    private static Set<BuiltInAttribute> currentAttributes;
    private static Set<BuiltInAttribute> reactiveAttributes;
    private static Set<BuiltInAttribute> statusAttributes;
    private static Set<BuiltInAttribute> relayAttributes;
    private static Set<BuiltInAttribute> blinkAndOutageCounts;
    private static Set<BuiltInAttribute> estimatedLoadAttributes;

    private static Map<AttributeGroup, Set<BuiltInAttribute>> groupedRfnEventAttributes;
    private static Set<BuiltInAttribute> rfnHardwareAttributes;
    private static Set<BuiltInAttribute> rfnSoftwareAttributes;
    private static Set<BuiltInAttribute> rfnVoltageAttributes;
    private static Set<BuiltInAttribute> rfnCurrentAttributes;
    private static Set<BuiltInAttribute> rfnMeteringAttributes;
    private static Set<BuiltInAttribute> rfnDemandAttributes;
    private static Set<BuiltInAttribute> rfnOtherAttributes;
    private static Set<BuiltInAttribute> rfnNonReadableEvents;

    private static Map<AttributeGroup, Set<BuiltInAttribute>> allGroupedAttributes;


    static {
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
        profileAttributes= ImmutableSet.of(
                DELIVERED_KW_LOAD_PROFILE,
                LOAD_PROFILE,
                NET_KW_LOAD_PROFILE,
                PROFILE_CHANNEL_2,
                PROFILE_CHANNEL_3,
                RECEIVED_KW_LOAD_PROFILE,
                SUM_KVAR_LOAD_PROFILE,
                SUM_KVA_LOAD_PROFILE,
                SUM_KW_LOAD_PROFILE,
                VOLTAGE_PROFILE);

        readableProfileAttributes = ImmutableSet.of(
                LOAD_PROFILE,
                PROFILE_CHANNEL_2,
                PROFILE_CHANNEL_3,
                VOLTAGE_PROFILE);

        // point is an accumulation; Example: Usage
        accumulatorAttributes = ImmutableSet.of(
                DELIVERED_KWH,
                DELIVERED_KWH_PER_INTERVAL,
                KVAH,
                KVAH_RATE_A,
                KVAH_RATE_B,
                KVAH_RATE_C,
                KVAH_RATE_D,
                NET_KWH,
                NET_KWH_RATE_A,
                NET_KWH_RATE_B,
                NET_KWH_RATE_C,
                NET_KWH_RATE_D,
                NET_KWH_RATE_E,
                NET_KWH_PER_INTERVAL,
                RECEIVED_KVAH,
                RECEIVED_KWH,
                RECEIVED_KWH_FROZEN,
                RECEIVED_KWH_PER_INTERVAL,
                RECEIVED_KWH_RATE_A,
                RECEIVED_KWH_RATE_B,
                RECEIVED_KWH_RATE_C,
                RECEIVED_KWH_RATE_D,
                RECEIVED_KWH_RATE_E,
                SUM_KVAH,
                SUM_KVAH_PER_INTERVAL,
                SUM_KVARH,
                SUM_KWH,
                SUM_KWH_RATE_A,
                SUM_KWH_RATE_B,
                SUM_KWH_RATE_C,
                SUM_KWH_RATE_D,
                SUM_KWH_PER_INTERVAL,
                USAGE,
                USAGE_FROZEN,
                USAGE_PER_INTERVAL,
                USAGE_RATE_A,
                USAGE_RATE_B,
                USAGE_RATE_C,
                USAGE_RATE_D,
                USAGE_RATE_E,
                USAGE_WATER,
                WATER_USAGE_PER_INTERVAL);

        blinkAndOutageCounts = ImmutableSet.of(
                BLINK_COUNT,
                OUTAGE_LOG,
                RFN_BLINK_COUNT,
                RFN_BLINK_RESTORE_COUNT,
                RFN_OUTAGE_COUNT,
                RFN_OUTAGE_RESTORE_COUNT);

        reactiveAttributes = ImmutableSet.of(
                FORWARD_INDUCTIVE_KVARH,
                FORWARD_CAPACITIVE_KVARH_PER_INTERVAL,
                FORWARD_INDUCTIVE_KVARH_PER_INTERVAL,
                REVERSE_INDUCTIVE_KVARH,
                REVERSE_INDUCTIVE_KVARH_PER_INTERVAL,
                REVERSE_CAPACITIVE_KVARH_PER_INTERVAL,
                KVAR,
                KVARH,
                KVARH_RATE_A,
                KVARH_RATE_B,
                KVARH_RATE_C,
                KVARH_RATE_D,
                RECEIVED_KVARH,
                RECEIVED_KVARH_RATE_A,
                RECEIVED_KVARH_RATE_B,
                RECEIVED_KVARH_RATE_C,
                RECEIVED_KVARH_RATE_D,
                NET_DELIVERED_KVARH,
                NET_DELIVERED_KVARH_RATE_A,
                NET_DELIVERED_KVARH_RATE_B,
                NET_DELIVERED_KVARH_RATE_C,
                NET_DELIVERED_KVARH_RATE_D,
                NET_RECEIVED_KVARH,
                NET_RECEIVED_KVARH_RATE_A,
                NET_RECEIVED_KVARH_RATE_B,
                NET_RECEIVED_KVARH_RATE_C,
                NET_RECEIVED_KVARH_RATE_D,
                PEAK_KVA,
                PEAK_KVA_COIN,
                PEAK_KVA_RATE_A,
                PEAK_KVA_RATE_B,
                PEAK_KVA_RATE_C,
                PEAK_KVA_RATE_D,
                PEAK_KVAR,
                PEAK_KVAR_RATE_A,
                PEAK_KVAR_RATE_B,
                PEAK_KVAR_RATE_C,
                PEAK_KVAR_RATE_D,
                PEAK_KVAR_COIN,
                PEAK_KVARH,
                PEAK_KVARH_COIN,
                POWER_FACTOR,
                POWER_FACTOR_COIN,
                POWER_FACTOR_PHASE_A,
                POWER_FACTOR_PHASE_B,
                POWER_FACTOR_PHASE_C,
                SUM_KVARH_PER_INTERVAL
                );

        otherAttributes = ImmutableSet.of(
                PHASE,
                RECORDING_INTERVAL,
                REPORTING_INTERVAL,
                TOTAL_LUF_COUNT,
                TOTAL_LUV_COUNT,
                TEMPERATURE,
                INDOOR_TEMPERATURE,
                OUTDOOR_TEMPERATURE,
                COOL_SET_TEMPERATURE,
                HEAT_SET_TEMPERATURE,
                HUMIDITY,
                FIRMWARE_VERSION,
                IP_ADDRESS,
                NEUTRAL_CURRENT_SENSOR,
                SERIAL_NUMBER,
                UDP_PORT,
                LAST_CONTROL_REASON,
                IGNORED_CONTROL_REASON);

        demandAttributes = ImmutableSet.of(
                DEMAND,
                DEMAND_PEAK_KVA_COIN,
                IED_DEMAND_RESET_COUNT,
                PEAK_DEMAND,
                PEAK_DEMAND_FROZEN,
                PEAK_DEMAND_FROZEN_RATE_A,
                PEAK_DEMAND_FROZEN_RATE_B,
                PEAK_DEMAND_FROZEN_RATE_C,
                PEAK_DEMAND_FROZEN_RATE_D,
                PEAK_DEMAND_RATE_A,
                PEAK_DEMAND_RATE_B,
                PEAK_DEMAND_RATE_C,
                PEAK_DEMAND_RATE_D,
                PEAK_DEMAND_RATE_E);

        voltageAttributes = ImmutableSet.of(
                MAXIMUM_VOLTAGE,
                MAXIMUM_VOLTAGE_FROZEN,
                MINIMUM_VOLTAGE,
                MINIMUM_VOLTAGE_FROZEN,
                VOLTAGE,
                VOLTAGE_PHASE_A,
                VOLTAGE_PHASE_B,
                VOLTAGE_PHASE_C);

        currentAttributes = ImmutableSet.of(
                CURRENT,
                CURRENT_PHASE_A,
                CURRENT_PHASE_B,
                CURRENT_PHASE_C,
                CURRENT_ANGLE_PHASE_A,
                CURRENT_ANGLE_PHASE_B,
                CURRENT_ANGLE_PHASE_C,
                NEUTRAL_CURRENT);

        statusAttributes = ImmutableSet.of(
                COMM_STATUS,
                CONNECTION_STATUS,
                CONTROL_POINT,
                CONTROL_STATUS,
                CURRENT_WITHOUT_VOLTAGE_FLAG,
                DISCONNECT_STATUS,
                FAULT_STATUS,
                GENERAL_ALARM_FLAG,
                LM_GROUP_STATUS,
                LOAD_SIDE_VOLTAGE_DETECTED_FLAG,
                METER_BOX_COVER_REMOVAL_FLAG,
                OUTAGE_STATUS,
                OUT_OF_VOLTAGE_FLAG,
                POWER_FAIL_FLAG,
                REVERSE_POWER_FLAG,
                RF_DEMAND_RESET_STATUS,
                SERVICE_STATUS,
                TAMPER_FLAG,
                VOLTAGE_OUT_OF_LIMITS_FLAG,
                ZERO_USAGE_FLAG,
                ZIGBEE_LINK_STATUS,
                TERMINAL_BLOCK_COVER_REMOVAL_FLAG);

        relayAttributes = ImmutableSet.of(
                RELAY_1_LOAD_SIZE,
                RELAY_1_REMAINING_CONTROL,
                RELAY_1_RUN_TIME_DATA_LOG,
                RELAY_1_SHED_TIME_DATA_LOG,
                RELAY_2_LOAD_SIZE,
                RELAY_2_REMAINING_CONTROL,
                RELAY_2_RUN_TIME_DATA_LOG,
                RELAY_2_SHED_TIME_DATA_LOG,
                RELAY_3_LOAD_SIZE,
                RELAY_3_REMAINING_CONTROL,
                RELAY_3_RUN_TIME_DATA_LOG,
                RELAY_3_SHED_TIME_DATA_LOG,
                RELAY_4_REMAINING_CONTROL,
                RELAY_4_RUN_TIME_DATA_LOG,
                RELAY_4_SHED_TIME_DATA_LOG);

        estimatedLoadAttributes = ImmutableSet.of(
                CONNECTED_LOAD,
                DIVERSIFIED_LOAD,
                MAX_LOAD_REDUCTION,
                AVAILABLE_LOAD_REDUCTION);

        // This map defines how attributes are grouped in drop downs and list selectors.
        // Used in conjunction with the selectNameValue or attributeSelector tag and groupItems="true".
        ImmutableMap.Builder<AttributeGroup, Set<BuiltInAttribute>> groupedDataAttributesBuilder =
                ImmutableMap.builder();

        groupedDataAttributesBuilder.put(AttributeGroup.BLINK_AND_OUTAGE, blinkAndOutageCounts);
        groupedDataAttributesBuilder.put(AttributeGroup.CURRENT, currentAttributes);
        groupedDataAttributesBuilder.put(AttributeGroup.DEMAND, demandAttributes);
        groupedDataAttributesBuilder.put(AttributeGroup.OTHER, otherAttributes);
        groupedDataAttributesBuilder.put(AttributeGroup.PROFILE, profileAttributes);
        groupedDataAttributesBuilder.put(AttributeGroup.REACTIVE, reactiveAttributes);
        groupedDataAttributesBuilder.put(AttributeGroup.RELAY, relayAttributes);
        groupedDataAttributesBuilder.put(AttributeGroup.STATUS, statusAttributes);
        groupedDataAttributesBuilder.put(AttributeGroup.USAGE, accumulatorAttributes);
        groupedDataAttributesBuilder.put(AttributeGroup.VOLTAGE, voltageAttributes);
        groupedDataAttributesBuilder.put(AttributeGroup.ESTIMATED_LOAD, estimatedLoadAttributes);

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

        rfnHardwareAttributes = ImmutableSet.of(
                CRYSTAL_OSCILLATOR_ERROR,
                EEPROM_ACCESS_ERROR,
                IMPROPER_METER_ENGINE_OPERATION_WARNING,
                INTERNAL_COMMUNICATION_ERROR,
                INTERNAL_ERROR_FLAG,
                LOW_BATTERY_WARNING,
                METER_RECONFIGURE,
                NON_VOLATILE_MEM_FAILURE,
                OVER_VOLTAGE,
                OVER_VOLTAGE_MEASURED,
                OVER_VOLTAGE_THRESHOLD,
                POWER_FAIL_DATA_SAVE_ERROR,
                RAM_ERROR,
                RFN_TEMPERATURE_ALARM,
                ROM_ERROR,
                SERVICE_DISCONNECT_SWITCH_ERROR,
                SERVICE_DISCONNECT_SWITCH_OPEN,
                SERVICE_DISCONNECT_SWITCH_SENSOR_ERROR,
                STUCK_SWITCH,
                TEMPERATURE_DEVICE,
                UNDER_VOLTAGE,
                UNDER_VOLTAGE_MEASURED,
                UNDER_VOLTAGE_THRESHOLD,
                WATT_HOUR_PULSE_FAILURE);

        rfnSoftwareAttributes = ImmutableSet.of(
                ALTERNATE_MODE_ENTRY,
                ANSI_SECURITY_FAILED,
                BAD_UPGRADE_SECURITY_PARAM,
                CONFIGURATION_ERROR,
                ENCRYPTION_KEY_TABLE_CRC_ERROR,
                END_OF_CALENDAR_WARNING,
                FAILED_UPGRADE_SIGNATURE_VERIF,
                INVALID_SERVICE,
                MASS_MEMORY_ERROR,
                PASSWORD_TABLE_CRC_ERROR,
                SECURITY_CONFIGURATION_ERROR,
                SELF_CHECK_ERROR,
                TABLE_CRC_ERROR,
                TOU_SCHEDULE_ERROR,
                UNCONFIGURED,
                UNPROGRAMMED);

        rfnVoltageAttributes = ImmutableSet.of(
                VOLTAGE_LOSS,
                VOLTAGE_PHASE_A_OUT,
                VOLTAGE_PHASE_B_OUT,
                VOLTAGE_PHASE_C_OUT);

        rfnCurrentAttributes = ImmutableSet.of(
                CURRENT_LOSS,
                LOSS_OF_ALL_CURRENT,
                LOSS_OF_PHASE_A_CURRENT,
                LOSS_OF_PHASE_C_CURRENT);

        rfnMeteringAttributes = ImmutableSet.of(
                ENERGY_ACCUMULATED_WHILE_IN_STANDBY_MODE,
                LINE_FREQUENCY_WARNING,
                LOAD_SIDE_VOLTAGE_IS_MISSING,
                LOW_LOSS_POTENTIAL,
                MEASUREMENT_ERROR,
                POTENTIAL_INDICATOR_WARNING,
                PQM_TEST_FAILURE_WARNING,
                REVERSED_AGGREGATE,
                REVERSED_PHASE_A,
                REVERSED_PHASE_C,
                SERVICE_CURRENT_TEST_FAILURE_WARNING,
                USER_PROGRAMMABLE_TEMPERATURE_THRESHOLD_EXCEEDED,
                VOLTAGE_ALERTS,
                VOLTAGE_PHASE_ERROR);

        rfnDemandAttributes = ImmutableSet.of(
                DEMAND_OVERLOAD,
                DEMAND_READS_AND_RESET,
                DEMAND_THRESHOLD_EXCEEDED_WARNING);

        rfnOtherAttributes = ImmutableSet.of(
                CLOCK_ERROR,
                CURRENT_WAVEFORM_DISTORTION,
                DISPLAY_LOCKED_BY_WARNING,
                DNP3_ADDRESS_CHANGED,
                INACTIVE_PHASE_CURRENT_DIAGNOSTIC_ERROR,
                METROLOGY_COMM_FAILURE,
                OUTSTATION_DNP3_SERCOMM_LOCKED,
                PHASE_ANGLE_DISPLACEMENT,
                PHASE_LOSS,
                POLARITY_CROSS_PHASE_ENERGY_FLOW_DIAGNOSTIC,
                REGISTER_FULL_SCALE_EXCEEDED,
                SEASON_CHANGE,
                SITESCAN_ERROR,
                TIME_ADJUSTMENT,
                TIME_SYNC_FAILED,
                THD_V_OR_TDD_I_ERROR);

        // This map defines how attributes are grouped in drop downs and list selectors.
        // Used in conjunction with the selectNameValue or attributeSelector tag and groupItems="true".
        ImmutableMap.Builder<AttributeGroup, Set<BuiltInAttribute>> groupedRfnEventBuilder = ImmutableMap.builder();

        groupedRfnEventBuilder.put(AttributeGroup.RFN_HARDWARE_EVENT, rfnHardwareAttributes);
        groupedRfnEventBuilder.put(AttributeGroup.RFN_SOFTWARE_EVENT, rfnSoftwareAttributes);
        groupedRfnEventBuilder.put(AttributeGroup.RFN_VOLTAGE_EVENT, rfnVoltageAttributes);
        groupedRfnEventBuilder.put(AttributeGroup.RFN_CURRENT_EVENT, rfnCurrentAttributes);
        groupedRfnEventBuilder.put(AttributeGroup.RFN_DEMAND_EVENT, rfnDemandAttributes);
        groupedRfnEventBuilder.put(AttributeGroup.RFN_OTHER_EVENT, rfnOtherAttributes);
        groupedRfnEventBuilder.put(AttributeGroup.RFN_METERING_EVENT, rfnMeteringAttributes);

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
        allCurrentAttributes.addAll(currentAttributes);
        allCurrentAttributes.addAll(rfnCurrentAttributes);
        allGroupedBuilder.put(AttributeGroup.CURRENT, allCurrentAttributes.build());

        ImmutableSet.Builder<BuiltInAttribute> allVoltageAttributes = ImmutableSet.builder();
        allVoltageAttributes.addAll(voltageAttributes);
        allVoltageAttributes.addAll(rfnVoltageAttributes);
        allGroupedBuilder.put(AttributeGroup.VOLTAGE, allVoltageAttributes.build());

        ImmutableSet.Builder<BuiltInAttribute> allDemandAttributes = ImmutableSet.builder();
        allDemandAttributes.addAll(demandAttributes);
        allDemandAttributes.addAll(rfnDemandAttributes);
        allGroupedBuilder.put(AttributeGroup.DEMAND, allDemandAttributes.build());

        ImmutableSet.Builder<BuiltInAttribute> allOtherAttributes = ImmutableSet.builder();
        allOtherAttributes.addAll(otherAttributes);
        allOtherAttributes.addAll(rfnOtherAttributes);
        allOtherAttributes.addAll(rfnMeteringAttributes);
        allGroupedBuilder.put(AttributeGroup.OTHER, allOtherAttributes.build());

        allGroupedBuilder.put(AttributeGroup.BLINK_AND_OUTAGE, blinkAndOutageCounts);
        allGroupedBuilder.put(AttributeGroup.PROFILE, profileAttributes);
        allGroupedBuilder.put(AttributeGroup.REACTIVE, reactiveAttributes);
        allGroupedBuilder.put(AttributeGroup.RELAY, relayAttributes);
        allGroupedBuilder.put(AttributeGroup.STATUS, statusAttributes);
        allGroupedBuilder.put(AttributeGroup.USAGE, accumulatorAttributes);
        allGroupedBuilder.put(AttributeGroup.ESTIMATED_LOAD, estimatedLoadAttributes);

        allGroupedBuilder.put(AttributeGroup.RFN_HARDWARE_EVENT, rfnHardwareAttributes);
        allGroupedBuilder.put(AttributeGroup.RFN_SOFTWARE_EVENT, rfnSoftwareAttributes);

        // The attribute group map that is created can be used in conjunction with
        // the selectNameValue tag and groupItems="true".
        allGroupedAttributes = allGroupedBuilder.build();
    }

    private String defaultDescription;

    private BuiltInAttribute(String defaultDescription) {
        this.defaultDescription = defaultDescription;
    }

    public boolean isProfile() {
        return profileAttributes.contains(this);
    }

    public boolean isReadableProfile() {
        return readableProfileAttributes.contains(this);
    }

    public boolean isAccumulator() {
        return accumulatorAttributes.contains(this);
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
        return accumulatorAttributes;
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

    public static Set<BuiltInAttribute> getProfileAttributes() {
        return profileAttributes;
    }

    public static Set<BuiltInAttribute> getReadableProfileAttributes() {
        return readableProfileAttributes;
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
}