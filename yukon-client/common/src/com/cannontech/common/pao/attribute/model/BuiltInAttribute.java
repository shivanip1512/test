package com.cannontech.common.pao.attribute.model;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.springframework.context.MessageSourceResolvable;

import com.cannontech.common.i18n.Displayable;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;


public enum BuiltInAttribute implements Attribute {
    
    BLINK_COUNT("Blink Count"), 
    CONNECTION_STATUS("Connection Status"),
    CONTROL_POINT("Control Point"),
    NEUTRAL_CURRENT("Current (Neutral)"),
    CURRENT_PHASE_A("Current (Phase A)"),
    CURRENT_PHASE_B("Current (Phase B)"),
    CURRENT_PHASE_C("Current (Phase C)"),
    DEMAND("Demand"), 
    DISCONNECT_STATUS("Disconnect Status"),
    FAULT_STATUS("Fault Status"),
    GENERAL_ALARM_FLAG("General Alarm Flag"),
    KVAR("kVAr"),
    KVARH("kVArh"),
    LM_GROUP_STATUS("LM Group Status"),
    LOAD_PROFILE("Load Profile"),
    MAXIMUM_VOLTAGE("Maximum Voltage"),
    MINIMUM_VOLTAGE("Minimum Voltage"),
    OUTAGE_LOG("Outage Log"),
    OUTAGE_STATUS("Outage Status"),
    PEAK_DEMAND("Peak Demand"),
    PHASE("Phase"),
    POWER_FAIL_FLAG("Power Fail Flag"),
    PROFILE_CHANNEL_2("Profile Channel 2"),
    PROFILE_CHANNEL_3("Profile Channel 3"),
    REVERSE_POWER_FLAG("Reverse Power Flag"),
    TAMPER_FLAG("Tamper Flag"),
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
    VOLTAGE_PHASE_A("Voltage (Phase A)"),
    VOLTAGE_PHASE_B("Voltage (Phase B)"),
    VOLTAGE_PHASE_C("Voltage (Phase C)"),
    VOLTAGE_PROFILE("Voltage Profile"),
    USAGE_WATER("Water Usage Reading"),
    ZERO_USAGE_FLAG("Zero Usage Flag"),
    ZIGBEE_LINK_STATUS("ZigBee Link Status"),
    
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
    
    private static ImmutableSet<BuiltInAttribute> rfnEventTypes;
    private static ImmutableSet<BuiltInAttribute> rfnEventStatusTypes;
    private static ImmutableSet<BuiltInAttribute> rfnEventAnalogTypes;
    private static ImmutableSet<BuiltInAttribute> profileAttributes;
    private static ImmutableSet<BuiltInAttribute> accumulatorAttributes;
    static {
        Builder<BuiltInAttribute> builder = ImmutableSet.builder();
        builder.add(CONFIGURATION_ERROR);
        builder.add(CLOCK_ERROR);
        builder.add(CRYSTAL_OSCILLATOR_ERROR);
        builder.add(CURRENT_LOSS);
        builder.add(DEMAND_OVERLOAD);
        builder.add(DEMAND_READS_AND_RESET);
        builder.add(DEMAND_THRESHOLD_EXCEEDED_WARNING);
        builder.add(DISPLAY_LOCKED_BY_WARNING);
        builder.add(EEPROM_ACCESS_ERROR);
        builder.add(ENCRYPTION_KEY_TABLE_CRC_ERROR);
        builder.add(END_OF_CALENDAR_WARNING);
        builder.add(ENERGY_ACCUMULATED_WHILE_IN_STANDBY_MODE);
        builder.add(IMPROPER_METER_ENGINE_OPERATION_WARNING);
        builder.add(INTERNAL_COMMUNICATION_ERROR);
        builder.add(INVALID_SERVICE);
        builder.add(LINE_FREQUENCY_WARNING);
        builder.add(LOAD_SIDE_VOLTAGE_IS_MISSING);
        builder.add(LOSS_OF_ALL_CURRENT);
        builder.add(LOSS_OF_PHASE_A_CURRENT);
        builder.add(LOSS_OF_PHASE_C_CURRENT);
        builder.add(LOW_BATTERY_WARNING);
        builder.add(LOW_LOSS_POTENTIAL);
        builder.add(MEASUREMENT_ERROR);
        builder.add(NON_VOLATILE_MEM_FAILURE);
        builder.add(PASSWORD_TABLE_CRC_ERROR);
        builder.add(POTENTIAL_INDICATOR_WARNING);
        builder.add(POWER_FAIL_DATA_SAVE_ERROR);
        builder.add(POWER_FAIL_FLAG);             //[PLC & RFN] Shared
        builder.add(PQM_TEST_FAILURE_WARNING);
        builder.add(RAM_ERROR);
        builder.add(REVERSE_POWER_FLAG);          //[PLC & RFN] Shared
        builder.add(REVERSED_AGGREGATE);
        builder.add(REVERSED_PHASE_A);
        builder.add(REVERSED_PHASE_C);
        builder.add(ROM_ERROR);
        builder.add(SECURITY_CONFIGURATION_ERROR);
        builder.add(SELF_CHECK_ERROR);
        builder.add(SERVICE_CURRENT_TEST_FAILURE_WARNING);
        builder.add(SERVICE_DISCONNECT_SWITCH_ERROR);
        builder.add(SERVICE_DISCONNECT_SWITCH_OPEN);
        builder.add(SERVICE_DISCONNECT_SWITCH_SENSOR_ERROR);
        builder.add(STUCK_SWITCH);
        builder.add(TABLE_CRC_ERROR);
        builder.add(TAMPER_FLAG);                 //[PLC & RFN] Shared
        builder.add(TIME_ADJUSTMENT);
        builder.add(UNCONFIGURED);
        builder.add(UNPROGRAMMED);
        builder.add(USER_PROGRAMMABLE_TEMPERATURE_THRESHOLD_EXCEEDED);
        builder.add(VOLTAGE_ALERTS);
        builder.add(VOLTAGE_LOSS);
        builder.add(VOLTAGE_PHASE_A_OUT);
        builder.add(VOLTAGE_PHASE_B_OUT);
        builder.add(VOLTAGE_PHASE_C_OUT);
        builder.add(VOLTAGE_PHASE_ERROR);
        
        builder.add(OUTAGE_STATUS);                 //[PLC & RFN] Shared
        
        rfnEventStatusTypes = builder.build();
        
        Builder<BuiltInAttribute> analogBuilder = ImmutableSet.builder();
        analogBuilder.add(RFN_BLINK_COUNT);
        analogBuilder.add(RFN_BLINK_RESTORE_COUNT);
        analogBuilder.add(RFN_OUTAGE_COUNT);
        analogBuilder.add(RFN_OUTAGE_RESTORE_COUNT);

        rfnEventAnalogTypes = analogBuilder.build();
        
        builder.addAll(rfnEventAnalogTypes);
        rfnEventTypes = builder.build();
        
        Builder<BuiltInAttribute> profile = ImmutableSet.builder();
        profile.add(LOAD_PROFILE);
        profile.add(PROFILE_CHANNEL_2);
        profile.add(PROFILE_CHANNEL_3);
        profile.add(VOLTAGE_PROFILE);
        profileAttributes = profile.build();
        
      //point is an accumulation; Example: Usage 
        Builder<BuiltInAttribute> accumulators = ImmutableSet.builder();
        accumulators.add(TOU_RATE_A_USAGE);
        accumulators.add(TOU_RATE_B_USAGE);
        accumulators.add(TOU_RATE_C_USAGE);
        accumulators.add(TOU_RATE_D_USAGE);
        accumulators.add(USAGE);
        accumulators.add(USAGE_WATER);
        accumulatorAttributes = accumulators.build();
    }

    private BuiltInAttribute(String description) {
    	this.description = description;
    }
    
    private String description;
    
    public String getDescription() {
        return description;
    }
    
    public boolean isProfile() {
		return profileAttributes.contains(this);
	}

    public boolean isAccumulator() {
		return accumulatorAttributes.contains(this);
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
    
    public boolean isRfnEventType() {
        return rfnEventTypes.contains(this);
    }
    
    public boolean isRfnEventStatusType() {
        return rfnEventStatusTypes.contains(this);
    }
    
    public boolean isRfnEventAnalogType() {
        return rfnEventAnalogTypes.contains(this);
    }
    
    public String getKey() {
        return this.name();
    }

    @Override
    public MessageSourceResolvable getMessage() {
        return YukonMessageSourceResolvable.createDefault("yukon.common.attribute.builtInAttribute." + name(), description);
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
    
}