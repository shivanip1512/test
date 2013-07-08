package com.cannontech.common.pao.attribute.model;

import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import org.springframework.context.MessageSourceResolvable;

import com.cannontech.common.i18n.Displayable;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import com.google.common.collect.Sets;


public enum BuiltInAttribute implements Attribute {

    /* NOTE: Remember to add any new attributes to point.xml for i18n'ing, too */
    
    BLINK_COUNT("Blink Count"), 
    CONNECTION_STATUS("Connection Status"),
    CONTROL_POINT("Control Point"),
    CONTROL_STATUS("Control Status"),
    NEUTRAL_CURRENT("Current (Neutral)"),
    CURRENT("Current"),
    CURRENT_PHASE_A("Current (Phase A)"),
    CURRENT_PHASE_B("Current (Phase B)"),
    CURRENT_PHASE_C("Current (Phase C)"),
    CURRENT_WITHOUT_VOLTAGE_FLAG("Current Without Voltage"),
    DEMAND("Demand"), 
    DISCONNECT_STATUS("Disconnect Status"),
    ENERGY_GENERATED("Energy Generated"),
    FAULT_STATUS("Fault Status"),
    FORWARD_INDUCTIVE_KVARH("Forward Inductive kVARh"),
    GENERAL_ALARM_FLAG("General Alarm Flag"),
    IED_DEMAND_RESET_COUNT("IED Demand Reset Count"),
    KVAR("kVAr"),
    KVARH("kVArh"),
    LM_GROUP_STATUS("LM Group Status"),
    LOAD_PROFILE("Load Profile"),
    LOAD_SIDE_VOLTAGE_DETECTED_FLAG("Load Side Voltage Detected"),
    METER_BOX_COVER_REMOVAL_FLAG("Meter Box Cover Removal"),
    MAXIMUM_VOLTAGE("Maximum Voltage"),
    MINIMUM_VOLTAGE("Minimum Voltage"),
    OUTAGE_LOG("Outage Log"),
    OUTAGE_STATUS("Outage Status"),
    OUT_OF_VOLTAGE_FLAG("Out of Voltage"),
    VOLTAGE_OUT_OF_LIMITS_FLAG("Voltage Out of Limits"),
    OVER_VOLTAGE("Over Voltage"),
    OVER_VOLTAGE_MEASURED("Over Voltage Measured"),
    OVER_VOLTAGE_THRESHOLD("Over Voltage Threshold"),
    PEAK_DEMAND("Peak Demand"),
    PEAK_KVAR("Peak kVAr"),
    PHASE("Phase"),
    POWER_FACTOR("Power Factor"),
    POWER_FACTOR_PHASE_A("Power Factor (Phase A)"),
    POWER_FACTOR_PHASE_B("Power Factor (Phase B)"),
    POWER_FACTOR_PHASE_C("Power Factor (Phase C)"),
    POWER_FAIL_FLAG("Power Fail Flag"),
    PROFILE_CHANNEL_2("Profile Channel 2"),
    PROFILE_CHANNEL_3("Profile Channel 3"),
    RECORDING_INTERVAL("Recording Interval"),
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
    REVERSE_INDUCTIVE_KVARH("Reverse Inductive kVARh"),
    REVERSE_POWER_FLAG("Reverse Power Flag"),
    RF_DEMAND_RESET_STATUS("RF Demand Reset Status"),
    SERVICE_STATUS("Service Status"),
    TAMPER_FLAG("Tamper Flag"),
    TEMPORARY_OUT_OF_SERVICE("Temp Out of Service Duration"),
    TOTAL_LUF_COUNT("Total LUF Event Count"),
    TOTAL_LUV_COUNT("Total LUV Event Count"),
    TOU_RATE_A_PEAK_DEMAND("Tou Rate A Peak"),
    TOU_RATE_B_PEAK_DEMAND("Tou Rate B Peak"),
    TOU_RATE_C_PEAK_DEMAND("Tou Rate C Peak"),
    TOU_RATE_D_PEAK_DEMAND("Tou Rate D Peak"),
    TOU_RATE_A_USAGE("Tou Rate A Usage"), 
    TOU_RATE_B_USAGE("Tou Rate B Usage"), 
    TOU_RATE_C_USAGE("Tou Rate C Usage"), 
    TOU_RATE_D_USAGE("Tou Rate D Usage"), 
    TOU_RATE_A_ENERGY_GENERATED("Tou Rate A Energy Generated"), 
    TOU_RATE_B_ENERGY_GENERATED("Tou Rate B Energy Generated"), 
    TOU_RATE_C_ENERGY_GENERATED("Tou Rate C Energy Generated"), 
    TOU_RATE_D_ENERGY_GENERATED("Tou Rate D Energy Generated"), 
    UNDER_VOLTAGE("Under Voltage"),
    UNDER_VOLTAGE_MEASURED("Under Voltage Measured"),
    UNDER_VOLTAGE_THRESHOLD("Under Voltage Threshold"),
    USAGE("Usage Reading"), 
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

    DELIVERED_KWH("Delivered kWh"),
    RECEIVED_KWH("Received kWh"),
    NET_KWH("Net kWh"),
    SUM_KWH("Sum kWh"),
    SUM_KVAH("Sum kVAh"),
    SUM_KVARH("Sum kVArh"),
    
    USAGE_PER_INTERVAL("Usage per Interval"),
    DELIVERED_KWH_PER_INTERVAL("Delivered kWh per Interval"),
    RECEIVED_KWH_PER_INTERVAL("Received kWh per Interval"),
    GENERATED_KWH_PER_INTERVAL("Generated kWh per Interval"),
    SUM_KWH_PER_INTERVAL("Sum kWh per Interval"),
    NET_KWH_PER_INTERVAL("Net kWh per Interval"),
    SUM_KVAH_PER_INTERVAL("Sum kVAh per Interval"),
    SUM_KVARH_PER_INTERVAL("Sum kVArh per Interval"),
    WATER_USAGE_PER_INTERVAL("Water Usage per Interval"),
    FORWARD_INDUCTIVE_KVARH_PER_INTERVAL("Forward Inductive kVARh per Interval"),
    FORWARD_CAPACITIVE_KVARH_PER_INTERVAL("Forward Capacitive kVARh per Interval"),
    REVERSE_INDUCTIVE_KVARH_PER_INTERVAL("Reverse Inductive kVARh per Interval"),
    REVERSE_CAPACITIVE_KVARH_PER_INTERVAL("Reverse Capacitive kVARh per Interval"),
    
    DELIVERED_KW_LOAD_PROFILE("Delivered kW Load Profile"),
    RECEIVED_KW_LOAD_PROFILE("Received kW Load Profile"),
    SUM_KW_LOAD_PROFILE("Sum kW Load Profile"),
    NET_KW_LOAD_PROFILE("Net kW Load Profile"),
    SUM_KVA_LOAD_PROFILE("Sum kVA Load Profile"),
    SUM_KVAR_LOAD_PROFILE("Sum kVAr Load Profile"),

    // Rfn Events that map to Event Status points (this list must be kept in sync with both 
    // our rfn set below AND its version in RfnConditionType.java. Outages and restores are
    // the only exception to this rule (as in the names don't exactly match))
    CONFIGURATION_ERROR("Configuration Error"),
    CLOCK_ERROR("Clock Error"),
    CRYSTAL_OSCILLATOR_ERROR("Crystal Oscillator Error"),
    CURRENT_LOSS("Current Loss"),
    DEMAND_OVERLOAD("Demand Overload"),
    DEMAND_READS_AND_RESET("Demand Reads And Reset"),
    DEMAND_THRESHOLD_EXCEEDED_WARNING("Demand Threshold Exceeded Warning"),
    DISPLAY_LOCKED_BY_WARNING("Display Locked By Warning"),
    EEPROM_ACCESS_ERROR("Eeprom Access Error"),
    ENCRYPTION_KEY_TABLE_CRC_ERROR("Encryption Key Table Crc Error"),
    END_OF_CALENDAR_WARNING("End Of Calendar Warning"),
    ENERGY_ACCUMULATED_WHILE_IN_STANDBY_MODE("Energy Accumulated While In Standby Mode"),
    IMPROPER_METER_ENGINE_OPERATION_WARNING("Improper Meter Engine Operation Warning"),
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
    MEASUREMENT_ERROR("Measurement Error"),
    NON_VOLATILE_MEM_FAILURE("Non Volatile Mem Failure"),
    PASSWORD_TABLE_CRC_ERROR("Password Table Crc Error"),
    POTENTIAL_INDICATOR_WARNING("Potential Indicator Warning"),
    POWER_FAIL_DATA_SAVE_ERROR("Power Fail Data Save Error"),
    PQM_TEST_FAILURE_WARNING("Pqm Test Failure Warning"),
    RAM_ERROR("Ram Error"),
    REVERSED_AGGREGATE("Reversed Aggregate"),
    REVERSED_PHASE_A("Reversed Phase A"),
    REVERSED_PHASE_C("Reversed Phase C"),
    RFN_BLINK_COUNT("Rfn Blink Count"),
    RFN_BLINK_RESTORE_COUNT("Rfn Blink Restore Count"),
    RFN_OUTAGE_COUNT("Rfn Outage Count"),
    RFN_OUTAGE_RESTORE_COUNT("Rfn Outage Restore Count"),
    ROM_ERROR("Rom Error"),
    SECURITY_CONFIGURATION_ERROR("Security Configuration Error"),
    SELF_CHECK_ERROR("Self Check Error"),
    SERVICE_CURRENT_TEST_FAILURE_WARNING("Service Current Test Failure Warning"),
    SERVICE_DISCONNECT_SWITCH_ERROR("Service Disconnect Switch Error"),
    SERVICE_DISCONNECT_SWITCH_OPEN("Service Disconnect Switch Open"),
    SERVICE_DISCONNECT_SWITCH_SENSOR_ERROR("Service Disconnect Switch Sensor Error"),
    STUCK_SWITCH("Stuck Switch"),
    TABLE_CRC_ERROR("Table Crc Error"),
    TIME_ADJUSTMENT("Time Adjustment"),
    UNCONFIGURED("Unconfigured"),
    UNPROGRAMMED("Unprogrammed"),
    USER_PROGRAMMABLE_TEMPERATURE_THRESHOLD_EXCEEDED("User Programmable Temperature Threshold Exceeded"),
    VOLTAGE_ALERTS("Voltage Alerts"),
    VOLTAGE_LOSS("Voltage Loss"),
    VOLTAGE_PHASE_A_OUT("Voltage Phase A Out"),
    VOLTAGE_PHASE_B_OUT("Voltage Phase B Out"),
    VOLTAGE_PHASE_C_OUT("Voltage Phase C Out"),
    VOLTAGE_PHASE_ERROR("Voltage Phase Error");
    
    private final String keyPrefix = "yukon.common.attribute.builtInAttribute.";
    
    // These are informational sets not used for display group purposes.
    private static ImmutableSet<BuiltInAttribute> rfnEventTypes;
    private static ImmutableSet<BuiltInAttribute> rfnEventStatusTypes;
    private static ImmutableSet<BuiltInAttribute> rfnEventAnalogTypes;

    // These are both informational and used for display group purposes.
    private static ImmutableSet<BuiltInAttribute> profileAttributes;
    private static ImmutableSet<BuiltInAttribute> readableProfileAttributes;
    private static ImmutableSet<BuiltInAttribute> accumulatorAttributes;
    
    // The following maps and sets are used for displaying grouped attribute lists.
    private static ImmutableMap<AttributeGroup, ImmutableSet<BuiltInAttribute>> groupedDataAttributes;
    private static ImmutableSet<BuiltInAttribute> otherAttributes;
    private static ImmutableSet<BuiltInAttribute> demandAttributes;
    private static ImmutableSet<BuiltInAttribute> voltageAttributes; 
    private static ImmutableSet<BuiltInAttribute> currentAttributes;
    private static ImmutableSet<BuiltInAttribute> reactiveAttributes;
    private static ImmutableSet<BuiltInAttribute> statusAttributes;
    private static ImmutableSet<BuiltInAttribute> relayAttributes;
    private static ImmutableSet<BuiltInAttribute> blinkAndOutageCounts;
    
    private static ImmutableMap<AttributeGroup, ImmutableSet<BuiltInAttribute>> groupedRfnEventAttributes;
    private static ImmutableSet<BuiltInAttribute> rfnHardwareAttributes;
    private static ImmutableSet<BuiltInAttribute> rfnSoftwareAttributes;
    private static ImmutableSet<BuiltInAttribute> rfnVoltageAttributes;
    private static ImmutableSet<BuiltInAttribute> rfnCurrentAttributes;
    private static ImmutableSet<BuiltInAttribute> rfnDisconnectAttributes;
    private static ImmutableSet<BuiltInAttribute> rfnDemandAttributes;
    private static ImmutableSet<BuiltInAttribute> rfnOtherAttributes;
    private static ImmutableSet<BuiltInAttribute> rfnNonReadableEvents;
    
    private static ImmutableMap<AttributeGroup, ImmutableSet<BuiltInAttribute>> allGroupedAttributes;


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
        
        profileAttributes = ImmutableSet.of(
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
        
        //point is an accumulation; Example: Usage 
        accumulatorAttributes = ImmutableSet.of(
                DELIVERED_KWH,
                DELIVERED_KWH_PER_INTERVAL,
                ENERGY_GENERATED,
                GENERATED_KWH_PER_INTERVAL,
                NET_KWH,
                NET_KWH_PER_INTERVAL,
                RECEIVED_KWH,
                RECEIVED_KWH_PER_INTERVAL,
                SUM_KVAH,
                SUM_KVAH_PER_INTERVAL,
                SUM_KVARH,
                SUM_KWH,
                SUM_KWH_PER_INTERVAL,
                TOU_RATE_A_ENERGY_GENERATED,
                TOU_RATE_A_USAGE,
                TOU_RATE_B_ENERGY_GENERATED,
                TOU_RATE_B_USAGE,
                TOU_RATE_C_ENERGY_GENERATED,
                TOU_RATE_C_USAGE,
                TOU_RATE_D_ENERGY_GENERATED,
                TOU_RATE_D_USAGE,
                USAGE,
                USAGE_PER_INTERVAL,
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
                PEAK_KVAR,
                POWER_FACTOR,
                POWER_FACTOR_PHASE_A,
                POWER_FACTOR_PHASE_B,
                POWER_FACTOR_PHASE_C,
                SUM_KVARH_PER_INTERVAL
                );
        
        otherAttributes = ImmutableSet.of(
                PHASE,
                RECORDING_INTERVAL,
                REPORTING_INTERVAL,
                TEMPORARY_OUT_OF_SERVICE,
                TOTAL_LUF_COUNT,
                TOTAL_LUV_COUNT);
        
        demandAttributes = ImmutableSet.of(
                DEMAND,
                IED_DEMAND_RESET_COUNT,
                PEAK_DEMAND,
                TOU_RATE_A_PEAK_DEMAND,
                TOU_RATE_B_PEAK_DEMAND,
                TOU_RATE_C_PEAK_DEMAND,
                TOU_RATE_D_PEAK_DEMAND);
        
        voltageAttributes = ImmutableSet.of(
                MAXIMUM_VOLTAGE,
                MINIMUM_VOLTAGE,
                VOLTAGE,
                VOLTAGE_PHASE_A,
                VOLTAGE_PHASE_B,
                VOLTAGE_PHASE_C);
        
        currentAttributes = ImmutableSet.of(
                CURRENT,
                CURRENT_PHASE_A,
                CURRENT_PHASE_B,
                CURRENT_PHASE_C,
                NEUTRAL_CURRENT);
        
        statusAttributes = ImmutableSet.of(
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
        
        // This map defines how attributes are grouped in drop downs and list selectors. 
        // Used in conjunction with the selectNameValue or attributeSelector tag and groupItems="true".
        ImmutableMap.Builder<AttributeGroup, ImmutableSet<BuiltInAttribute>> groupedDataAttributesBuilder = ImmutableMap.builder();
        
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
        
        groupedDataAttributes = groupedDataAttributesBuilder.build();
    }

    /**
     * This method builds ImmutableSet's of RFN event attributes.  An event attribute 
     * indicates that some significant event has occurred on a device.
     */
    private static void buildRfnEventAttributeSets() {
        
        rfnEventStatusTypes = ImmutableSet.of(
                CLOCK_ERROR,
                CONFIGURATION_ERROR,
                CRYSTAL_OSCILLATOR_ERROR,
                CURRENT_LOSS,
                DEMAND_OVERLOAD,
                DEMAND_READS_AND_RESET,
                DEMAND_THRESHOLD_EXCEEDED_WARNING,
                DISPLAY_LOCKED_BY_WARNING,
                EEPROM_ACCESS_ERROR,
                ENCRYPTION_KEY_TABLE_CRC_ERROR,
                END_OF_CALENDAR_WARNING,
                ENERGY_ACCUMULATED_WHILE_IN_STANDBY_MODE,
                IMPROPER_METER_ENGINE_OPERATION_WARNING,
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
                MEASUREMENT_ERROR,
                NON_VOLATILE_MEM_FAILURE,
                OUTAGE_STATUS,                 //[PLC & RFN] Shared
                OVER_VOLTAGE,
                PASSWORD_TABLE_CRC_ERROR,
                POTENTIAL_INDICATOR_WARNING,
                POWER_FAIL_DATA_SAVE_ERROR,
                POWER_FAIL_FLAG,             //[PLC & RFN] Shared
                PQM_TEST_FAILURE_WARNING,
                RAM_ERROR,
                REVERSED_AGGREGATE,
                REVERSED_PHASE_A,
                REVERSED_PHASE_C,
                REVERSE_POWER_FLAG,          //[PLC & RFN] Shared
                ROM_ERROR,
                SECURITY_CONFIGURATION_ERROR,
                SELF_CHECK_ERROR,
                SERVICE_CURRENT_TEST_FAILURE_WARNING,
                SERVICE_DISCONNECT_SWITCH_ERROR,
                SERVICE_DISCONNECT_SWITCH_OPEN,
                SERVICE_DISCONNECT_SWITCH_SENSOR_ERROR,
                STUCK_SWITCH,
                TABLE_CRC_ERROR,
                TAMPER_FLAG,                 //[PLC & RFN] Shared
                TIME_ADJUSTMENT,
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
                OVER_VOLTAGE_MEASURED,
                OVER_VOLTAGE_THRESHOLD,
                RFN_BLINK_COUNT,
                RFN_BLINK_RESTORE_COUNT,
                RFN_OUTAGE_COUNT,
                RFN_OUTAGE_RESTORE_COUNT,
                UNDER_VOLTAGE_MEASURED,
                UNDER_VOLTAGE_THRESHOLD);
        
        Builder<BuiltInAttribute> builder = ImmutableSet.builder();
        builder.addAll(rfnEventStatusTypes);
        builder.addAll(rfnEventAnalogTypes);
        rfnEventTypes = builder.build();
        
        rfnHardwareAttributes = ImmutableSet.of(
                CLOCK_ERROR,
                CRYSTAL_OSCILLATOR_ERROR,
                EEPROM_ACCESS_ERROR,
                INTERNAL_COMMUNICATION_ERROR,
                INTERNAL_ERROR_FLAG,
                LOW_BATTERY_WARNING,
                NON_VOLATILE_MEM_FAILURE,
                OVER_VOLTAGE,
                OVER_VOLTAGE_MEASURED,
                OVER_VOLTAGE_THRESHOLD,
                RAM_ERROR,
                ROM_ERROR,
                STUCK_SWITCH,
                UNDER_VOLTAGE,
                UNDER_VOLTAGE_MEASURED,
                UNDER_VOLTAGE_THRESHOLD,
                WATT_HOUR_PULSE_FAILURE);
        
        rfnSoftwareAttributes = ImmutableSet.of(
                CONFIGURATION_ERROR,
                ENCRYPTION_KEY_TABLE_CRC_ERROR,
                END_OF_CALENDAR_WARNING,
                INVALID_SERVICE,
                MEASUREMENT_ERROR,
                PASSWORD_TABLE_CRC_ERROR,
                POWER_FAIL_DATA_SAVE_ERROR,
                SECURITY_CONFIGURATION_ERROR,
                SELF_CHECK_ERROR,
                TABLE_CRC_ERROR,
                UNCONFIGURED,
                UNPROGRAMMED,
                USER_PROGRAMMABLE_TEMPERATURE_THRESHOLD_EXCEEDED);
        
        rfnVoltageAttributes = ImmutableSet.of(
                LOAD_SIDE_VOLTAGE_IS_MISSING,
                VOLTAGE_ALERTS,
                VOLTAGE_LOSS,
                VOLTAGE_PHASE_A_OUT,
                VOLTAGE_PHASE_B_OUT,
                VOLTAGE_PHASE_C_OUT,
                VOLTAGE_PHASE_ERROR);
        
        rfnCurrentAttributes = ImmutableSet.of(
                CURRENT_LOSS,
                LOSS_OF_ALL_CURRENT,
                LOSS_OF_PHASE_A_CURRENT,
                LOSS_OF_PHASE_C_CURRENT,
                REVERSED_AGGREGATE,
                REVERSED_PHASE_A,
                REVERSED_PHASE_C);
        
        rfnDisconnectAttributes = ImmutableSet.of(
                SERVICE_DISCONNECT_SWITCH_ERROR,
                SERVICE_DISCONNECT_SWITCH_OPEN,
                SERVICE_DISCONNECT_SWITCH_SENSOR_ERROR);
        
        rfnDemandAttributes = ImmutableSet.of(
                DEMAND_OVERLOAD,
                DEMAND_READS_AND_RESET,
                DEMAND_THRESHOLD_EXCEEDED_WARNING);
        
        rfnOtherAttributes = ImmutableSet.of(
                DISPLAY_LOCKED_BY_WARNING,
                ENERGY_ACCUMULATED_WHILE_IN_STANDBY_MODE,
                IMPROPER_METER_ENGINE_OPERATION_WARNING,
                LINE_FREQUENCY_WARNING,
                LOW_LOSS_POTENTIAL,
                POTENTIAL_INDICATOR_WARNING,
                PQM_TEST_FAILURE_WARNING,
                SERVICE_CURRENT_TEST_FAILURE_WARNING,
                TIME_ADJUSTMENT);
        
        // This map defines how attributes are grouped in drop downs and list selectors. 
        // Used in conjunction with the selectNameValue or attributeSelector tag and groupItems="true".
        ImmutableMap.Builder<AttributeGroup, ImmutableSet<BuiltInAttribute>> groupedRfnEventBuilder = ImmutableMap.builder();

        groupedRfnEventBuilder.put(AttributeGroup.RFN_HARDWARE_EVENT, rfnHardwareAttributes);
        groupedRfnEventBuilder.put(AttributeGroup.RFN_SOFTWARE_EVENT, rfnSoftwareAttributes);
        groupedRfnEventBuilder.put(AttributeGroup.RFN_VOLTAGE_EVENT, rfnVoltageAttributes);
        groupedRfnEventBuilder.put(AttributeGroup.RFN_CURRENT_EVENT, rfnCurrentAttributes);
        groupedRfnEventBuilder.put(AttributeGroup.RFN_DISCONNECT_EVENT, rfnDisconnectAttributes);
        groupedRfnEventBuilder.put(AttributeGroup.RFN_DEMAND_EVENT, rfnDemandAttributes);
        groupedRfnEventBuilder.put(AttributeGroup.RFN_OTHER_EVENT, rfnOtherAttributes);
        
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
        ImmutableMap.Builder<AttributeGroup, ImmutableSet<BuiltInAttribute>> allGroupedBuilder = ImmutableMap.builder();
        
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
        allGroupedBuilder.put(AttributeGroup.OTHER, allOtherAttributes.build());
        
        allGroupedBuilder.put(AttributeGroup.BLINK_AND_OUTAGE, blinkAndOutageCounts);
        allGroupedBuilder.put(AttributeGroup.PROFILE, profileAttributes);
        allGroupedBuilder.put(AttributeGroup.REACTIVE, reactiveAttributes);
        allGroupedBuilder.put(AttributeGroup.RELAY, relayAttributes);
        allGroupedBuilder.put(AttributeGroup.STATUS, statusAttributes);
        allGroupedBuilder.put(AttributeGroup.USAGE, accumulatorAttributes);
        
        allGroupedBuilder.put(AttributeGroup.RFN_HARDWARE_EVENT, rfnHardwareAttributes);
        allGroupedBuilder.put(AttributeGroup.RFN_SOFTWARE_EVENT, rfnSoftwareAttributes);
        allGroupedBuilder.put(AttributeGroup.RFN_DISCONNECT_EVENT, rfnDisconnectAttributes);

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

    public static ImmutableMap<AttributeGroup, ImmutableSet<BuiltInAttribute>> getStandardGroupedAttributes() {
        return groupedDataAttributes;
    }
    
    public static ImmutableMap<AttributeGroup, ImmutableSet<BuiltInAttribute>> getRfnEventGroupedAttributes() {
        return groupedRfnEventAttributes;
    }

    public static ImmutableMap<AttributeGroup, ImmutableSet<BuiltInAttribute>> getAllGroupedAttributes() {
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
        return this.name();
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