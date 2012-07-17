package com.cannontech.common.pao.attribute.model;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.context.MessageSourceResolvable;

import com.cannontech.common.i18n.Displayable;
import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;


public enum BuiltInAttribute implements Attribute, DisplayableEnum {

	/* NOTE: Remember to add any new attributes to point.xml for i18n'ing, too */
	
    BLINK_COUNT("Blink Count"), 
    CONNECTION_STATUS("Connection Status"),
    CONTROL_POINT("Control Point"),
    CONTROL_STATUS("Control Status"),
    NEUTRAL_CURRENT("Current (Neutral)"),
    CURRENT_PHASE_A("Current (Phase A)"),
    CURRENT_PHASE_B("Current (Phase B)"),
    CURRENT_PHASE_C("Current (Phase C)"),
    DEMAND("Demand"), 
    DISCONNECT_STATUS("Disconnect Status"),
    ENERGY_GENERATED("Energy Generated"),
    FAULT_STATUS("Fault Status"),
    GENERAL_ALARM_FLAG("General Alarm Flag"),
    IED_DEMAND_RESET_COUNT("IED Demand Reset Count"),
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
    RECORDING_INTERVAL("Recording Interval"),
    RELAY_1_CONTROL_TIME("Relay 1 Control Time"),
    RELAY_1_LOAD_SIZE("Relay 1 kW Load Size"),
    RELAY_1_REMAINING_CONTROL("Relay 1 Remaining Control Time"),
    RELAY_1_RUN_TIME("Relay 1 Run Time"),
    RELAY_1_SHED_TIME("Relay 1 Shed Time"),
    RELAY_2_CONTROL_TIME("Relay 2 Control Time"),
    RELAY_2_LOAD_SIZE("Relay 2 kW Load Size"),
    RELAY_2_REMAINING_CONTROL("Relay 2 Remaining Control Time"),
    RELAY_2_RUN_TIME("Relay 2 Run Time"),
    RELAY_2_SHED_TIME("Relay 2 Shed Time"),
    RELAY_3_CONTROL_TIME("Relay 3 Control Time"),
    RELAY_3_LOAD_SIZE("Relay 3 kW Load Size"),
    RELAY_3_REMAINING_CONTROL("Relay 3 Remaining Control Time"),
    RELAY_3_RUN_TIME("Relay 3 Run Time"),
    RELAY_3_SHED_TIME("Relay 3 Shed Time"),
    REPORTING_INTERVAL("Reporting Interval"),
    REVERSE_POWER_FLAG("Reverse Power Flag"),
    RF_DEMAND_RESET("RF Demand Reset"),
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
    
    private final String keyPrefix = "yukon.common.attribute.builtInAttribute.";
    
    private static ImmutableSet<BuiltInAttribute> rfnEventTypes;
    private static ImmutableSet<BuiltInAttribute> rfnEventStatusTypes;
    private static ImmutableSet<BuiltInAttribute> rfnEventAnalogTypes;
    private static ImmutableSet<BuiltInAttribute> profileAttributes;
    private static ImmutableSet<BuiltInAttribute> accumulatorAttributes;
    
    private static Map<AttributeGroup, List<BuiltInAttribute>> standardGroupedAttributes;
    private static ImmutableList<BuiltInAttribute> otherAttributes;
    private static ImmutableList<BuiltInAttribute> demandAttributes;
    private static ImmutableList<BuiltInAttribute> voltageAttributes; 
    private static ImmutableList<BuiltInAttribute> currentAttributes;
    private static ImmutableList<BuiltInAttribute> statusAttributes;
    
    private static Map<AttributeGroup, List<BuiltInAttribute>> rfnEventGroupedAttributes;
    private static ImmutableList<BuiltInAttribute> rfnHardwareAttributes;
    private static ImmutableList<BuiltInAttribute> rfnSoftwareAttributes;
    private static ImmutableList<BuiltInAttribute> rfnVoltageAttributes;
    private static ImmutableList<BuiltInAttribute> rfnCurrentAttributes;
    private static ImmutableList<BuiltInAttribute> rfnDisconnectAttributes;
    private static ImmutableList<BuiltInAttribute> rfnDemandAttributes;
    private static ImmutableList<BuiltInAttribute> rfnOtherAttributes;
    
    private static Map<AttributeGroup, List<BuiltInAttribute>> allGroupedAttributes;

    static {
        buildStandardAttributeSets();
        buildRfnEventAttributeSets();
        buildAllAttributeGroups();
    }

    private static void buildAllAttributeGroups() {
        allGroupedAttributes = Maps.newHashMap();

        List<BuiltInAttribute> allCurrentAttributes = Lists.newArrayList();
        allCurrentAttributes.addAll(currentAttributes);
        allCurrentAttributes.addAll(rfnCurrentAttributes);
        allGroupedAttributes.put(AttributeGroup.CURRENT, allCurrentAttributes);
        
        List<BuiltInAttribute> allVoltageAttributes = Lists.newArrayList();
        allVoltageAttributes.addAll(voltageAttributes);
        allVoltageAttributes.addAll(rfnVoltageAttributes);
        allGroupedAttributes.put(AttributeGroup.VOLTAGE, allVoltageAttributes);

        List<BuiltInAttribute> allDemandAttributes = Lists.newArrayList();
        allDemandAttributes.addAll(demandAttributes);
        allDemandAttributes.addAll(rfnDemandAttributes);
        allGroupedAttributes.put(AttributeGroup.DEMAND, allDemandAttributes);
        
        List<BuiltInAttribute> allOtherAttributes = Lists.newArrayList();
        allOtherAttributes.addAll(otherAttributes);
        allOtherAttributes.addAll(rfnOtherAttributes);
        allGroupedAttributes.put(AttributeGroup.OTHER, allOtherAttributes);
        
        allGroupedAttributes.put(AttributeGroup.PROFILE, Lists.newArrayList(profileAttributes));
        allGroupedAttributes.put(AttributeGroup.USAGE, Lists.newArrayList(accumulatorAttributes));
        allGroupedAttributes.put(AttributeGroup.STATUS, statusAttributes);
        
        allGroupedAttributes.put(AttributeGroup.RFN_HARDWARE, rfnHardwareAttributes);
        allGroupedAttributes.put(AttributeGroup.RFN_SOFTWARE, rfnSoftwareAttributes);
        allGroupedAttributes.put(AttributeGroup.RFN_BLINK_AND_OUTAGE, Lists.newArrayList(rfnEventAnalogTypes));
        allGroupedAttributes.put(AttributeGroup.RFN_DISCONNECT, rfnDisconnectAttributes);
    }
    
    private static void buildRfnEventAttributeSets() {
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
        
        ImmutableList.Builder<BuiltInAttribute> hardware = ImmutableList.builder();
        hardware.add(CLOCK_ERROR);
        hardware.add(CRYSTAL_OSCILLATOR_ERROR);
        hardware.add(EEPROM_ACCESS_ERROR);
        hardware.add(INTERNAL_COMMUNICATION_ERROR);
        hardware.add(LOW_BATTERY_WARNING);
        hardware.add(RAM_ERROR);
        hardware.add(ROM_ERROR);
        hardware.add(NON_VOLATILE_MEM_FAILURE);
        rfnHardwareAttributes = hardware.build();
        
        ImmutableList.Builder<BuiltInAttribute> software = ImmutableList.builder();
        software.add(CONFIGURATION_ERROR);
        software.add(ENCRYPTION_KEY_TABLE_CRC_ERROR);
        software.add(END_OF_CALENDAR_WARNING);
        software.add(MEASUREMENT_ERROR);
        software.add(PASSWORD_TABLE_CRC_ERROR);
        software.add(POWER_FAIL_DATA_SAVE_ERROR);
        software.add(SECURITY_CONFIGURATION_ERROR);
        software.add(SELF_CHECK_ERROR);
        software.add(TABLE_CRC_ERROR);
        software.add(UNCONFIGURED);
        software.add(UNPROGRAMMED);
        software.add(USER_PROGRAMMABLE_TEMPERATURE_THRESHOLD_EXCEEDED);
        software.add(INVALID_SERVICE);
        rfnSoftwareAttributes = software.build();
        
        ImmutableList.Builder<BuiltInAttribute> voltage = ImmutableList.builder();
        voltage.add(VOLTAGE_ALERTS);
        voltage.add(VOLTAGE_LOSS);
        voltage.add(VOLTAGE_PHASE_A_OUT);
        voltage.add(VOLTAGE_PHASE_B_OUT);
        voltage.add(VOLTAGE_PHASE_C_OUT);
        voltage.add(VOLTAGE_PHASE_ERROR);
        voltage.add(LOAD_SIDE_VOLTAGE_IS_MISSING);
        rfnVoltageAttributes = voltage.build();
        
        ImmutableList.Builder<BuiltInAttribute> current = ImmutableList.builder();
        current.add(CURRENT_LOSS);
        current.add(LOSS_OF_ALL_CURRENT);
        current.add(LOSS_OF_PHASE_A_CURRENT);
        current.add(LOSS_OF_PHASE_C_CURRENT);
        current.add(REVERSED_AGGREGATE);
        current.add(REVERSED_PHASE_A);
        current.add(REVERSED_PHASE_C);
        rfnCurrentAttributes = current.build();

        ImmutableList.Builder<BuiltInAttribute> disconnect = ImmutableList.builder();
        disconnect.add(SERVICE_DISCONNECT_SWITCH_ERROR);
        disconnect.add(SERVICE_DISCONNECT_SWITCH_OPEN);
        disconnect.add(SERVICE_DISCONNECT_SWITCH_SENSOR_ERROR);
        rfnDisconnectAttributes = disconnect.build();
        
        ImmutableList.Builder<BuiltInAttribute> demand = ImmutableList.builder();
        demand.add(DEMAND_OVERLOAD);
        demand.add(DEMAND_READS_AND_RESET);
        demand.add(DEMAND_THRESHOLD_EXCEEDED_WARNING);
        rfnDemandAttributes = demand.build();
                
        ImmutableList.Builder<BuiltInAttribute> other = ImmutableList.builder();
        other.add(DISPLAY_LOCKED_BY_WARNING);
        other.add(ENERGY_ACCUMULATED_WHILE_IN_STANDBY_MODE);
        other.add(IMPROPER_METER_ENGINE_OPERATION_WARNING);
        other.add(LINE_FREQUENCY_WARNING);
        other.add(LOW_LOSS_POTENTIAL);
        other.add(POTENTIAL_INDICATOR_WARNING);
        other.add(PQM_TEST_FAILURE_WARNING);
        other.add(SERVICE_CURRENT_TEST_FAILURE_WARNING);
        other.add(TIME_ADJUSTMENT);
        rfnOtherAttributes = other.build();
        
        // Build headings & sub-options relationships for RFN event attributes.
        rfnEventGroupedAttributes = Maps.newHashMap();

        rfnEventGroupedAttributes.put(AttributeGroup.RFN_HARDWARE, rfnHardwareAttributes);
        rfnEventGroupedAttributes.put(AttributeGroup.RFN_SOFTWARE, rfnSoftwareAttributes);
        rfnEventGroupedAttributes.put(AttributeGroup.RFN_VOLTAGE, rfnVoltageAttributes);
        rfnEventGroupedAttributes.put(AttributeGroup.RFN_CURRENT, rfnCurrentAttributes);
        rfnEventGroupedAttributes.put(AttributeGroup.RFN_BLINK_AND_OUTAGE, Lists.newArrayList(rfnEventAnalogTypes));
        rfnEventGroupedAttributes.put(AttributeGroup.RFN_DISCONNECT, rfnDisconnectAttributes);
        rfnEventGroupedAttributes.put(AttributeGroup.RFN_DEMAND, rfnDemandAttributes);
        rfnEventGroupedAttributes.put(AttributeGroup.RFN_OTHER, rfnOtherAttributes);
    }
    
    private static void buildStandardAttributeSets() {
        ImmutableSet.Builder<BuiltInAttribute> profile = ImmutableSet.builder();
        profile.add(LOAD_PROFILE);
        profile.add(PROFILE_CHANNEL_2);
        profile.add(PROFILE_CHANNEL_3);
        profile.add(VOLTAGE_PROFILE);
        profileAttributes = profile.build();
        
        //point is an accumulation; Example: Usage 
        ImmutableSet.Builder<BuiltInAttribute> accumulators = ImmutableSet.builder();
        accumulators.add(TOU_RATE_A_USAGE);
        accumulators.add(TOU_RATE_B_USAGE);
        accumulators.add(TOU_RATE_C_USAGE);
        accumulators.add(TOU_RATE_D_USAGE);
        accumulators.add(USAGE);
        accumulators.add(USAGE_WATER);
        accumulatorAttributes = accumulators.build();
        
        ImmutableList.Builder<BuiltInAttribute> other = ImmutableList.builder();
        other.add(BLINK_COUNT);
        other.add(ENERGY_GENERATED);
        other.add(KVAR);
        other.add(KVARH);
        other.add(OUTAGE_LOG);
        other.add(PHASE);
        otherAttributes = other.build();
        
        ImmutableList.Builder<BuiltInAttribute> demand = ImmutableList.builder();
        demand.add(DEMAND);
        demand.add(PEAK_DEMAND);
        demand.add(TOU_RATE_A_PEAK_DEMAND);
        demand.add(TOU_RATE_B_PEAK_DEMAND);
        demand.add(TOU_RATE_C_PEAK_DEMAND);
        demand.add(TOU_RATE_D_PEAK_DEMAND);
        demandAttributes = demand.build();
        
        ImmutableList.Builder<BuiltInAttribute> voltage = ImmutableList.builder();
        voltage.add(VOLTAGE);
        voltage.add(VOLTAGE_PHASE_A);
        voltage.add(VOLTAGE_PHASE_B);
        voltage.add(VOLTAGE_PHASE_C);
        voltage.add(MAXIMUM_VOLTAGE);
        voltage.add(MINIMUM_VOLTAGE);
        voltageAttributes = voltage.build();
        
        ImmutableList.Builder<BuiltInAttribute> current = ImmutableList.builder();
        current.add(CURRENT_PHASE_A);
        current.add(CURRENT_PHASE_B);
        current.add(CURRENT_PHASE_C);
        current.add(NEUTRAL_CURRENT);
        currentAttributes = current.build();
        
        ImmutableList.Builder<BuiltInAttribute> status = ImmutableList.builder();
        status.add(CONNECTION_STATUS);
        status.add(CONTROL_POINT);
        status.add(DISCONNECT_STATUS);
        status.add(FAULT_STATUS);
        status.add(GENERAL_ALARM_FLAG);
        status.add(LM_GROUP_STATUS);
        status.add(OUTAGE_STATUS);
        status.add(POWER_FAIL_FLAG);
        status.add(REVERSE_POWER_FLAG);
        status.add(TAMPER_FLAG);
        status.add(ZERO_USAGE_FLAG);
        status.add(ZIGBEE_LINK_STATUS);
        statusAttributes = status.build();
        
        // Build headings & sub-options relationships for data (non-event) attributes.
        standardGroupedAttributes = Maps.newHashMap();
        
        standardGroupedAttributes.put(AttributeGroup.PROFILE, Lists.newArrayList(profileAttributes));
        standardGroupedAttributes.put(AttributeGroup.USAGE, Lists.newArrayList(accumulatorAttributes));
        standardGroupedAttributes.put(AttributeGroup.OTHER, otherAttributes);
        standardGroupedAttributes.put(AttributeGroup.DEMAND, demandAttributes);
        standardGroupedAttributes.put(AttributeGroup.VOLTAGE, voltageAttributes);
        standardGroupedAttributes.put(AttributeGroup.CURRENT, currentAttributes);
        standardGroupedAttributes.put(AttributeGroup.STATUS, statusAttributes);
    }

    private String description;
    
    private BuiltInAttribute(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
    
    public boolean isProfile() {
        return profileAttributes.contains(this);
    }

    public boolean isAccumulator() {
        return accumulatorAttributes.contains(this);
    }

    public static Map<AttributeGroup, List<BuiltInAttribute>> getStandardGroupedAttributes() {
        return standardGroupedAttributes;
    }
    
    public static Map<AttributeGroup, List<BuiltInAttribute>> getRfnEventGroupedAttributes() {
        return rfnEventGroupedAttributes;
    }

    public static Map<AttributeGroup, List<BuiltInAttribute>> getAllGroupedAttributes() {
        return allGroupedAttributes;
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
        return YukonMessageSourceResolvable.createDefault(keyPrefix + name(), description);
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

    @Override
    public String getFormatKey() {
        return keyPrefix + name();
    }
    
    // List of attribute groups for data & rfn event attributes
    public enum AttributeGroup implements DisplayableEnum {
        //PLC data & event attribute groups
        OTHER("general"),
        DEMAND("demand"),
        VOLTAGE("voltage"),
        CURRENT("current"),
        PROFILE("profile"),
        USAGE("usage"),
        STATUS("status"),
        
        //RFN event attribute groups
        RFN_HARDWARE("rfnHardware"),
        RFN_SOFTWARE("rfnSoftware"),
        RFN_VOLTAGE("rfnVoltage"),
        RFN_CURRENT("rfnCurrent"),
        RFN_BLINK_AND_OUTAGE("rfnBlinkAndOutage"),
        RFN_DISCONNECT("rfnDisconnect"),
        RFN_DEMAND("rfnDemand"),
        RFN_OTHER("rfnOther");

        private static final String groupPrefix = "yukon.common.attributeGroups.";
        private String groupName;
        
        private AttributeGroup(String group) {
            this.groupName = group;
        }

        @Override
        public String getFormatKey() {
            return groupPrefix + groupName;
        }
    }
}