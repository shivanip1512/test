package com.cannontech.web.amr.rfnEventsReport.model;

import java.util.Set;

import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;

public class RfnEventStatusTypeGroupings {
    
    private static ImmutableSet<BuiltInAttribute> all;
    private static ImmutableSet<BuiltInAttribute> general;
    private static ImmutableSet<BuiltInAttribute> hardware;
    private static ImmutableSet<BuiltInAttribute> tamper;
    private static ImmutableSet<BuiltInAttribute> outage;
    private static ImmutableSet<BuiltInAttribute> metering;
    
    static {
        Builder<BuiltInAttribute> allBuilder = ImmutableSet.builder();
        allBuilder.addAll(BuiltInAttribute.getRfnEventStatusTypes());
        all = allBuilder.build();
        
        buildGeneralTypes();
        buildHardwareTypes();
        buildTamperTypes();
        buildOutageTypes();
        buildMeteringTypes();
    }
    
    public static Set<BuiltInAttribute> getAllTypes() {
        return all;
    }
    
    public  static Set<BuiltInAttribute> getGeneral() {
        return general;
    }
    
    public static Set<BuiltInAttribute> getHardware() {
        return hardware;
    }
    
    public static Set<BuiltInAttribute> getTamper() {
        return tamper;
    }
    
    public static Set<BuiltInAttribute> getOutage() {
        return outage;
    }
    
    public static Set<BuiltInAttribute> getMetering() {
        return metering;
    }
    
    private static void buildHardwareTypes() {
        Builder<BuiltInAttribute> builder = ImmutableSet.builder();
        builder.add(BuiltInAttribute.CRYSTAL_OSCILLATOR_ERROR);
        builder.add(BuiltInAttribute.EEPROM_ACCESS_ERROR);
        builder.add(BuiltInAttribute.IMPROPER_METER_ENGINE_OPERATION_WARNING);
        builder.add(BuiltInAttribute.INTERNAL_COMMUNICATION_ERROR);
        builder.add(BuiltInAttribute.LOW_BATTERY_WARNING);
        builder.add(BuiltInAttribute.NON_VOLATILE_MEM_FAILURE);
        builder.add(BuiltInAttribute.POWER_FAILURE);
        builder.add(BuiltInAttribute.POWER_FAIL_DATA_SAVE_ERROR);
        builder.add(BuiltInAttribute.RAM_ERROR);
        builder.add(BuiltInAttribute.ROM_ERROR);
        builder.add(BuiltInAttribute.SERVICE_DISCONNECT_SWITCH_ERROR);
        builder.add(BuiltInAttribute.SERVICE_DISCONNECT_SWITCH_OPEN);
        builder.add(BuiltInAttribute.SERVICE_DISCONNECT_SWITCH_SENSOR_ERROR);
        builder.add(BuiltInAttribute.STUCK_SWITCH);
        hardware = builder.build();
    }
    
    private static void buildGeneralTypes() {
        Builder<BuiltInAttribute> builder = ImmutableSet.builder();
        builder.add(BuiltInAttribute.CONFIGURATION_ERROR);
        builder.add(BuiltInAttribute.CLOCK_ERROR);
        builder.add(BuiltInAttribute.DISPLAY_LOCKED_BY_WARNING);
        builder.add(BuiltInAttribute.ENCRYPTION_KEY_TABLE_CRC_ERROR);
        builder.add(BuiltInAttribute.END_OF_CALENDAR_WARNING);
        builder.add(BuiltInAttribute.INVALID_SERVICE);
        builder.add(BuiltInAttribute.PASSWORD_TABLE_CRC_ERROR);
        builder.add(BuiltInAttribute.POTENTIAL_INDICATOR_WARNING);
        builder.add(BuiltInAttribute.PQM_TEST_FAILURE_WARNING);
        builder.add(BuiltInAttribute.SECURITY_CONFIGURATION_ERROR);
        builder.add(BuiltInAttribute.SELF_CHECK_ERROR);
        builder.add(BuiltInAttribute.TABLE_CRC_ERROR);
        builder.add(BuiltInAttribute.TIME_ADJUSTMENT);
        builder.add(BuiltInAttribute.UNCONFIGURED);
        builder.add(BuiltInAttribute.UNPROGRAMMED);
        general = builder.build();
    }
    
    private static void buildTamperTypes() {
        Builder<BuiltInAttribute> builder = ImmutableSet.builder();
        builder.add(BuiltInAttribute.TAMPER_DETECT);
        tamper = builder.build();
    }
    
    private static void buildOutageTypes() {
        Builder<BuiltInAttribute> builder = ImmutableSet.builder();
        builder.add(BuiltInAttribute.CURRENT_LOSS);
        builder.add(BuiltInAttribute.LOAD_SIDE_VOLTAGE_IS_MISSING);
        builder.add(BuiltInAttribute.LOSS_OF_ALL_CURRENT);
        builder.add(BuiltInAttribute.LOSS_OF_PHASE_A_CURRENT);
        builder.add(BuiltInAttribute.LOSS_OF_PHASE_C_CURRENT);
        builder.add(BuiltInAttribute.OUTAGE_STATUS);
        builder.add(BuiltInAttribute.VOLTAGE_LOSS);
        builder.add(BuiltInAttribute.VOLTAGE_PHASE_A_OUT);
        builder.add(BuiltInAttribute.VOLTAGE_PHASE_B_OUT);
        builder.add(BuiltInAttribute.VOLTAGE_PHASE_C_OUT);
        outage = builder.build();
    }
    
    private static void buildMeteringTypes() {
        Builder<BuiltInAttribute> builder = ImmutableSet.builder();
        builder.add(BuiltInAttribute.DEMAND_OVERLOAD);
        builder.add(BuiltInAttribute.DEMAND_READS_AND_RESET);
        builder.add(BuiltInAttribute.DEMAND_THRESHOLD_EXCEEDED_WARNING);
        builder.add(BuiltInAttribute.ENERGY_ACCUMULATED_WHILE_IN_STANDBY_MODE);
        builder.add(BuiltInAttribute.LINE_FREQUENCY_WARNING);
        builder.add(BuiltInAttribute.LOW_LOSS_POTENTIAL);
        builder.add(BuiltInAttribute.MEASUREMENT_ERROR);
        builder.add(BuiltInAttribute.REVERSE_ROTATION);
        builder.add(BuiltInAttribute.REVERSED_AGGREGATE);
        builder.add(BuiltInAttribute.REVERSED_PHASE_A);
        builder.add(BuiltInAttribute.REVERSED_PHASE_C);
        builder.add(BuiltInAttribute.SERVICE_CURRENT_TEST_FAILURE_WARNING);
        builder.add(BuiltInAttribute.USER_PROGRAMMABLE_TEMPERATURE_THRESHOLD_EXCEEDED);
        builder.add(BuiltInAttribute.VOLTAGE_ALERTS);
        builder.add(BuiltInAttribute.VOLTAGE_PHASE_ERROR);
        metering = builder.build();
    }
    
}
