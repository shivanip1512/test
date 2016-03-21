package com.cannontech.dr.rfn.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.cannontech.amr.rfn.message.read.RfnMeterReadingType;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;

public enum RfnMeterSimulatorConfiguration {

    USAGE(BuiltInAttribute.USAGE, 900, GeneratedValueType.INCREASING,100.00, 1000000.00, 20.00, false, RfnMeterReadingType.INTERVAL),
    DELIVERED_KWH(BuiltInAttribute.DELIVERED_KWH, 1, GeneratedValueType.INCREASING, 100.00, 10000.00, 20.00, false, RfnMeterReadingType.INTERVAL),
    RECEIVED_KWH(BuiltInAttribute.RECEIVED_KWH, 3600, GeneratedValueType.INCREASING, 100.00, 10000.00, 20.00, false, RfnMeterReadingType.INTERVAL),
    SUM_KWH(BuiltInAttribute.SUM_KWH, 3600, GeneratedValueType.INCREASING, 100.00, 10000.00, 20.00, false, RfnMeterReadingType.INTERVAL),
    NET_KWH(BuiltInAttribute.NET_KWH ,3600, GeneratedValueType.INCREASING, 100.00, 10000.00, 20.00, false, RfnMeterReadingType.INTERVAL),
    DEMAND(BuiltInAttribute.DEMAND, 900, GeneratedValueType.INCREASING, 100.00, 10000.00, 20.00, false, RfnMeterReadingType.INTERVAL),
    USAGE_RATE_A(BuiltInAttribute.USAGE_RATE_A, 3600, GeneratedValueType.INCREASING, 100.00, 10000.00, 20.00, false, RfnMeterReadingType.INTERVAL),
    RECEIVED_KWH_RATE_A(BuiltInAttribute.RECEIVED_KWH_RATE_A, 3600, GeneratedValueType.INCREASING, 100.00, 10000.00, 20.00, false, RfnMeterReadingType.INTERVAL),
    SUM_KWH_RATE_A(BuiltInAttribute.SUM_KWH_RATE_A, 3600, GeneratedValueType.INCREASING, 1000.00, 100000.00, 100.00, false, RfnMeterReadingType.INTERVAL),
    NET_KWH_RATE_A(BuiltInAttribute.NET_KWH_RATE_A, 3600, GeneratedValueType.INCREASING, 1000.00, 100000.00, 100.00, false, RfnMeterReadingType.INTERVAL),
    USAGE_RATE_B(BuiltInAttribute.USAGE_RATE_B, 3600, GeneratedValueType.INCREASING, 1000.00, 100000.00, 100.00, false, RfnMeterReadingType.INTERVAL),
    RECEIVED_KWH_RATE_B(BuiltInAttribute.RECEIVED_KWH_RATE_B, 3600, GeneratedValueType.INCREASING, 1000.00, 100000.00, 100.00, false, RfnMeterReadingType.INTERVAL),
    SUM_KWH_RATE_B(BuiltInAttribute.SUM_KWH_RATE_B, 3600, GeneratedValueType.INCREASING, 1000.00, 100000.00, 100.00, false, RfnMeterReadingType.INTERVAL),
    NET_KWH_RATE_B(BuiltInAttribute.NET_KWH_RATE_B, 3600, GeneratedValueType.INCREASING, 1000.00, 100000.00, 100.00, false, RfnMeterReadingType.INTERVAL),
    USAGE_RATE_C(BuiltInAttribute.USAGE_RATE_C, 3600, GeneratedValueType.INCREASING, 100.0, 120.0, 1.0, false, RfnMeterReadingType.INTERVAL),
    RECEIVED_KWH_RATE_C(BuiltInAttribute.RECEIVED_KWH_RATE_C, 3600, GeneratedValueType.INCREASING, 1000.00, 100000.00, 100.00, false, RfnMeterReadingType.INTERVAL),
    SUM_KWH_RATE_C(BuiltInAttribute.SUM_KWH_RATE_C, 3600, GeneratedValueType.INCREASING, 1000.00, 100000.00, 100.00, false, RfnMeterReadingType.INTERVAL),
    NET_KWH_RATE_C(BuiltInAttribute.NET_KWH_RATE_C, 3600, GeneratedValueType.INCREASING, 1000.00, 100000.00, 100.00, false, RfnMeterReadingType.INTERVAL),
    USAGE_RATE_D(BuiltInAttribute.USAGE_RATE_D, 3600, GeneratedValueType.INCREASING, 100.0, 120.0, 1.0, false, RfnMeterReadingType.INTERVAL),
    RECEIVED_KWH_RATE_D(BuiltInAttribute.RECEIVED_KWH_RATE_D, 3600, GeneratedValueType.INCREASING, 1000.00, 100000.00, 100.00, false, RfnMeterReadingType.INTERVAL),
    SUM_KWH_RATE_D(BuiltInAttribute.SUM_KWH_RATE_D, 3600, GeneratedValueType.INCREASING, 1000.00, 100000.00, 100.00, false, RfnMeterReadingType.INTERVAL),
    NET_KWH_RATE_D(BuiltInAttribute.NET_KWH_RATE_D, 3600, GeneratedValueType.INCREASING, 1000.00, 100000.00, 100.00, false, RfnMeterReadingType.INTERVAL),
    PEAK_DEMAND(BuiltInAttribute.PEAK_DEMAND, 86400, GeneratedValueType.INCREASING, 1000.00, 100000.00, 100.00, true, RfnMeterReadingType.BILLING),
    PEAK_DEMAND_FROZEN(BuiltInAttribute.PEAK_DEMAND_FROZEN, 86400, GeneratedValueType.INCREASING, 1000.00, 100000.00, 100.00, true, RfnMeterReadingType.BILLING),
    PEAK_DEMAND_RATE_A(BuiltInAttribute.PEAK_DEMAND_RATE_A, 86400, GeneratedValueType.INCREASING, 1000.00, 100000.00, 100.00, true, RfnMeterReadingType.BILLING),
    PEAK_DEMAND_FROZEN_RATE_A(BuiltInAttribute.PEAK_DEMAND_FROZEN_RATE_A, 86400, GeneratedValueType.INCREASING, 1000.00, 100000.00, 100.00, true, RfnMeterReadingType.BILLING),
    PEAK_DEMAND_RATE_B(BuiltInAttribute.PEAK_DEMAND_RATE_B, 86400, GeneratedValueType.INCREASING, 1000.00, 100000.00, 100.00, true, RfnMeterReadingType.BILLING),
    PEAK_DEMAND_FROZEN_RATE_B(BuiltInAttribute.PEAK_DEMAND_FROZEN_RATE_B, 86400, GeneratedValueType.INCREASING, 1000.00, 100000.00, 100.00, true, RfnMeterReadingType.BILLING),
    PEAK_DEMAND_RATE_C(BuiltInAttribute.PEAK_DEMAND_RATE_C, 86400, GeneratedValueType.INCREASING, 1000.00, 100000.00, 100.00, true, RfnMeterReadingType.BILLING),
    PEAK_DEMAND_FROZEN_RATE_C(BuiltInAttribute.PEAK_DEMAND_FROZEN_RATE_C, 86400, GeneratedValueType.INCREASING, 1000.00, 100000.00, 100.00, true, RfnMeterReadingType.BILLING),
    PEAK_DEMAND_RATE_D(BuiltInAttribute.PEAK_DEMAND_RATE_D, 86400, GeneratedValueType.INCREASING, 1000.00, 100000.00, 100.00, true, RfnMeterReadingType.BILLING),
    PEAK_DEMAND_FROZEN_RATE_D(BuiltInAttribute.PEAK_DEMAND_FROZEN_RATE_D, 86400, GeneratedValueType.INCREASING, 1000.00, 100000.00, 100.00, true, RfnMeterReadingType.BILLING),
;
    
    BuiltInAttribute attribute = null;
    GeneratedValueType valueType = null;
    Double minValue = null;
    Double maxValue = null;
    Double changeBy = 0.0;
    int intervalSeconds; // 15 min duration for increasing the kwh data for USAGE and DEMAND 
    boolean dated; 
    RfnMeterReadingType rfnMeterReadingType = null;
    

    private RfnMeterSimulatorConfiguration(BuiltInAttribute attribute, int intervalSeconds, GeneratedValueType valueType,
            Double minValue, Double maxValue, Double changeBy, boolean dated,RfnMeterReadingType rfnMeterReadingType) {
        this.attribute = attribute;
        this.intervalSeconds = intervalSeconds;
        this.valueType = valueType;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.changeBy = changeBy;
        this.dated = dated;
        this.rfnMeterReadingType = rfnMeterReadingType;
        this.intervalSeconds =  intervalSeconds;
    }
    
 
    public Double getMinValue() {
        return minValue;
    }

    public Double getMaxValue() {
        return maxValue;
    }
    
    public int getIntervalSeconds() {
        return intervalSeconds;
    }

    public Double getChangeBy() {
        return changeBy;
    }

    public boolean isDated() {
        return dated;
    }
    
    public RfnMeterReadingType getRfnMeterReadingType() {
        return rfnMeterReadingType;
    }

    
    public GeneratedValueType getValueType() {
        return valueType;
    }

    public BuiltInAttribute getAttribute() {
        return attribute;
    }
    

    public static List<BuiltInAttribute> getValuesByMeterReadingType(RfnMeterReadingType rfnMeterReadingType) {
        List<BuiltInAttribute> attributes = new ArrayList<BuiltInAttribute>();
        for(RfnMeterSimulatorConfiguration value: RfnMeterSimulatorConfiguration.values()) {
            if(value.getRfnMeterReadingType() == rfnMeterReadingType) {
                attributes.add(value.getAttribute());
            }
        } 
        // Adding interval attribute if rfnMeterReadingType is billing
        if(rfnMeterReadingType.equals(RfnMeterReadingType.BILLING)) {
            for(RfnMeterSimulatorConfiguration value: RfnMeterSimulatorConfiguration.values()) {
                if(value.getRfnMeterReadingType() == RfnMeterReadingType.INTERVAL) {
                    attributes.add(value.getAttribute());
                }
            }  
        }
        
        return attributes;
    }

    public static boolean attributeExists(String attribute) {
        try {
            RfnMeterSimulatorConfiguration.valueOf(attribute);
            return true;
        } catch(IllegalArgumentException e) {
            return false;
        }
    }
    
    public static RfnMeterSimulatorConfiguration getRfnMeterValueGenerator(Attribute attribute) {
        for (RfnMeterSimulatorConfiguration value : RfnMeterSimulatorConfiguration.values()) {
            if (value.getAttribute() == attribute) {
                return value;
            }
        }
        return null;
    }
}
