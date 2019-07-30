package com.cannontech.amr.meter.service.impl;

import java.util.Set;

import com.cannontech.common.pao.attribute.model.AttributeGroup;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;

public class MeterEventStatusTypeGroupings {
    
    private static ImmutableSet<BuiltInAttribute> all;
    private static ImmutableSet<BuiltInAttribute> general;
    private static ImmutableSet<BuiltInAttribute> hardware;
    private static ImmutableSet<BuiltInAttribute> tamper;
    private static ImmutableSet<BuiltInAttribute> outage;
    private static ImmutableSet<BuiltInAttribute> metering;
    
    static {
        buildGeneralTypes();
        buildHardwareTypes();
        buildTamperTypes();
        buildOutageTypes();
        buildMeteringTypes();

        Builder<BuiltInAttribute> allBuilder = ImmutableSet.builder();
        allBuilder.addAll(general);
        allBuilder.addAll(hardware);
        allBuilder.addAll(tamper);
        allBuilder.addAll(outage);
        allBuilder.addAll(metering);
        all = allBuilder.build();
    }

    public static ImmutableSet<BuiltInAttribute> getAll() {
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
    
    private static void buildGeneralTypes() {
        Builder<BuiltInAttribute> builder = ImmutableSet.builder();
        //PLC
        builder.add(BuiltInAttribute.GENERAL_ALARM_FLAG);
        
        //RFN
        builder.addAll(BuiltInAttribute.getRfnEventGroupedAttributes().get(AttributeGroup.RFN_OTHER_EVENT));
        builder.addAll(BuiltInAttribute.getRfnEventGroupedAttributes().get(AttributeGroup.RFN_SOFTWARE_EVENT));
        
        general = builder.build();
    }
    
    private static void buildHardwareTypes() {
        Builder<BuiltInAttribute> builder = ImmutableSet.builder();
        //RFN
        for (BuiltInAttribute hardwareEvent : BuiltInAttribute.getRfnEventGroupedAttributes().get(AttributeGroup.RFN_HARDWARE_EVENT)) {
            if (hardwareEvent == BuiltInAttribute.TAMPER_CABLE_CUT) {
                //skip...these are added in buildTamperTypes()
            } else {
                builder.add(hardwareEvent);
            }
        }        
        hardware = builder.build();
    }
    
    private static void buildTamperTypes() {
        Builder<BuiltInAttribute> builder = ImmutableSet.builder();
        //PLC
        builder.add(BuiltInAttribute.ZERO_USAGE_FLAG);

        //PLC & RFN
        builder.add(BuiltInAttribute.REVERSE_POWER_FLAG);
        builder.add(BuiltInAttribute.TAMPER_FLAG);
        
        // RFN tamper flags, group as Tamper vs Metering
        builder.add(BuiltInAttribute.TAMPER_NO_USAGE_OVER_24_HOURS);
        builder.add(BuiltInAttribute.TAMPER_REVERSE_WH_DETECTED);
        builder.add(BuiltInAttribute.TAMPER_LARGE_INCREASE_AFTER_OUTAGE);
        builder.add(BuiltInAttribute.TAMPER_LARGE_DECREASE_AFTER_OUTAGE);
        builder.add(BuiltInAttribute.VIBRATION_TILT_TAMPER_DETECTED);
        
        // RFN Gen2 Water/Gas
        builder.add(BuiltInAttribute.TAMPER_CABLE_CUT);
        tamper = builder.build();
    }
    
    private static void buildOutageTypes() {
        Builder<BuiltInAttribute> builder = ImmutableSet.builder();
        //PLC & RFN
        builder.add(BuiltInAttribute.POWER_FAIL_FLAG);
        
        //RFN
        builder.add(BuiltInAttribute.OUTAGE_STATUS);
        builder.addAll(BuiltInAttribute.getRfnEventGroupedAttributes().get(AttributeGroup.RFN_CURRENT_EVENT));
        builder.addAll(BuiltInAttribute.getRfnEventGroupedAttributes().get(AttributeGroup.RFN_VOLTAGE_EVENT));
        
        outage = builder.build();
    }
    
    private static void buildMeteringTypes() {
        Builder<BuiltInAttribute> builder = ImmutableSet.builder();
        //RFN
        builder.addAll(BuiltInAttribute.getRfnEventGroupedAttributes().get(AttributeGroup.RFN_DEMAND_EVENT));
        
        for (BuiltInAttribute meteringEvent : BuiltInAttribute.getRfnEventGroupedAttributes().get(AttributeGroup.RFN_METERING_EVENT)) {
            if (meteringEvent == BuiltInAttribute.TAMPER_NO_USAGE_OVER_24_HOURS ||
                meteringEvent == BuiltInAttribute.TAMPER_REVERSE_WH_DETECTED ||
                meteringEvent == BuiltInAttribute.TAMPER_LARGE_INCREASE_AFTER_OUTAGE ||
                meteringEvent == BuiltInAttribute.TAMPER_LARGE_DECREASE_AFTER_OUTAGE ||
                meteringEvent == BuiltInAttribute.VIBRATION_TILT_TAMPER_DETECTED) {
                //skip...these are added in buildTamperTypes()
            } else {
                builder.add(meteringEvent);
            }
        }
        
        metering = builder.build();
    }
    
}
