package com.cannontech.dr.rfn.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.cannontech.amr.rfn.message.read.RfnMeterReadingType;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;

public enum RfnMeterSimulatorConfiguration {

    DELIVERED_KWH(BuiltInAttribute.DELIVERED_KWH, GenerationType.HOURLY, false, RfnMeterReadingType.INTERVAL, AttributeGroupType.USAGE), // Remove USAGE as it is duplicate of DELIVERED_KWH
    RECEIVED_KWH(BuiltInAttribute.RECEIVED_KWH, GenerationType.HOURLY, false, RfnMeterReadingType.INTERVAL, AttributeGroupType.USAGE),
    SUM_KWH(BuiltInAttribute.SUM_KWH, GenerationType.HOURLY, false, RfnMeterReadingType.INTERVAL, AttributeGroupType.USAGE),
    NET_KWH(BuiltInAttribute.NET_KWH, GenerationType.HOURLY, false, RfnMeterReadingType.INTERVAL, AttributeGroupType.USAGE),
    DEMAND(BuiltInAttribute.DEMAND, GenerationType.HOURLY, false, RfnMeterReadingType.INTERVAL, AttributeGroupType.DEMAND),
    RECEIVED_KWH_RATE_A(BuiltInAttribute.RECEIVED_KWH_RATE_A, GenerationType.HOURLY, false, RfnMeterReadingType.INTERVAL, AttributeGroupType.RATE_USAGE),
    SUM_KWH_RATE_A(BuiltInAttribute.SUM_KWH_RATE_A, GenerationType.HOURLY, false, RfnMeterReadingType.INTERVAL, AttributeGroupType.RATE_USAGE),
    NET_KWH_RATE_A(BuiltInAttribute.NET_KWH_RATE_A, GenerationType.HOURLY, false, RfnMeterReadingType.INTERVAL, AttributeGroupType.RATE_USAGE),
    RECEIVED_KWH_RATE_B(BuiltInAttribute.RECEIVED_KWH_RATE_B, GenerationType.HOURLY, false, RfnMeterReadingType.INTERVAL, AttributeGroupType.RATE_USAGE),
    SUM_KWH_RATE_B(BuiltInAttribute.SUM_KWH_RATE_B, GenerationType.HOURLY, false, RfnMeterReadingType.INTERVAL, AttributeGroupType.RATE_USAGE),
    NET_KWH_RATE_B(BuiltInAttribute.NET_KWH_RATE_B, GenerationType.HOURLY, false, RfnMeterReadingType.INTERVAL, AttributeGroupType.RATE_USAGE),
    RECEIVED_KWH_RATE_C(BuiltInAttribute.RECEIVED_KWH_RATE_C, GenerationType.HOURLY, false, RfnMeterReadingType.INTERVAL, AttributeGroupType.RATE_USAGE),
    SUM_KWH_RATE_C(BuiltInAttribute.SUM_KWH_RATE_C, GenerationType.HOURLY, false, RfnMeterReadingType.INTERVAL, AttributeGroupType.RATE_USAGE),
    NET_KWH_RATE_C(BuiltInAttribute.NET_KWH_RATE_C, GenerationType.HOURLY, false, RfnMeterReadingType.INTERVAL, AttributeGroupType.RATE_USAGE),
    RECEIVED_KWH_RATE_D(BuiltInAttribute.RECEIVED_KWH_RATE_D, GenerationType.HOURLY, false, RfnMeterReadingType.INTERVAL, AttributeGroupType.RATE_USAGE),
    SUM_KWH_RATE_D(BuiltInAttribute.SUM_KWH_RATE_D, GenerationType.HOURLY, false, RfnMeterReadingType.INTERVAL, AttributeGroupType.RATE_USAGE),
    NET_KWH_RATE_D(BuiltInAttribute.NET_KWH_RATE_D, GenerationType.HOURLY, false, RfnMeterReadingType.INTERVAL, AttributeGroupType.RATE_USAGE),
    PEAK_DEMAND(BuiltInAttribute.PEAK_DEMAND, GenerationType.DAILY, true, RfnMeterReadingType.BILLING, AttributeGroupType.DEMAND),
    PEAK_DEMAND_FROZEN(BuiltInAttribute.PEAK_DEMAND_FROZEN, GenerationType.DAILY, true, RfnMeterReadingType.BILLING, AttributeGroupType.RATE_DEMAND),
    PEAK_DEMAND_RATE_A(BuiltInAttribute.PEAK_DEMAND_RATE_A, GenerationType.DAILY, true, RfnMeterReadingType.BILLING, AttributeGroupType.RATE_DEMAND),
    PEAK_DEMAND_FROZEN_RATE_A(BuiltInAttribute.PEAK_DEMAND_FROZEN_RATE_A, GenerationType.DAILY, true, RfnMeterReadingType.BILLING, AttributeGroupType.RATE_DEMAND),
    PEAK_DEMAND_RATE_B(BuiltInAttribute.PEAK_DEMAND_RATE_B, GenerationType.DAILY, true, RfnMeterReadingType.BILLING, AttributeGroupType.RATE_DEMAND),
    PEAK_DEMAND_FROZEN_RATE_B(BuiltInAttribute.PEAK_DEMAND_FROZEN_RATE_B, GenerationType.DAILY, true, RfnMeterReadingType.BILLING, AttributeGroupType.RATE_DEMAND),
    PEAK_DEMAND_RATE_C(BuiltInAttribute.PEAK_DEMAND_RATE_C, GenerationType.DAILY, true, RfnMeterReadingType.BILLING, AttributeGroupType.RATE_DEMAND),
    PEAK_DEMAND_FROZEN_RATE_C(BuiltInAttribute.PEAK_DEMAND_FROZEN_RATE_C, GenerationType.DAILY, true, RfnMeterReadingType.BILLING, AttributeGroupType.RATE_DEMAND),
    PEAK_DEMAND_RATE_D(BuiltInAttribute.PEAK_DEMAND_RATE_D, GenerationType.DAILY, true, RfnMeterReadingType.BILLING, AttributeGroupType.RATE_DEMAND),
    PEAK_DEMAND_FROZEN_RATE_D(BuiltInAttribute.PEAK_DEMAND_FROZEN_RATE_D, GenerationType.DAILY, true, RfnMeterReadingType.BILLING, AttributeGroupType.RATE_DEMAND),
;
    
    BuiltInAttribute attribute = null;
    GenerationType generationType = null;
    boolean dated; 
    RfnMeterReadingType rfnMeterReadingType = null;
    AttributeGroupType attributeGroupType =  null;
    private final static List<BuiltInAttribute> demandAttributeType = new ArrayList<BuiltInAttribute>();
    private final static List<BuiltInAttribute> rateUsageAttributeType = new ArrayList<BuiltInAttribute>();
    private final static List<BuiltInAttribute> usageAttributeType = new ArrayList<BuiltInAttribute>();
    private final static List<BuiltInAttribute> rateDemandAttributeType = new ArrayList<BuiltInAttribute>();

    private RfnMeterSimulatorConfiguration(BuiltInAttribute attribute, GenerationType generationType, boolean dated,
            RfnMeterReadingType rfnMeterReadingType, AttributeGroupType attributeGroupType) {
        this.attribute = attribute;
        this.generationType = generationType;
        this.dated = dated;
        this.rfnMeterReadingType = rfnMeterReadingType;
        this.attributeGroupType = attributeGroupType;
    }

    static {

        for (RfnMeterSimulatorConfiguration value : RfnMeterSimulatorConfiguration.values()) {
            if (value.getAttributeGroupType() == AttributeGroupType.DEMAND) {
                demandAttributeType.add(value.getAttribute());
            } else if (value.getAttributeGroupType() == AttributeGroupType.RATE_USAGE) {
                rateUsageAttributeType.add(value.getAttribute());
            } else if (value.getAttributeGroupType() == AttributeGroupType.RATE_DEMAND) {
                rateDemandAttributeType.add(value.getAttribute());
            } else {
                usageAttributeType.add(value.getAttribute());
            }
        }

    }
    
    public GenerationType getGenerationType() {
        return generationType;
    }
    
    public boolean isDated() {
        return dated;
    }
    
    public RfnMeterReadingType getRfnMeterReadingType() {
        return rfnMeterReadingType;
    }

    public BuiltInAttribute getAttribute() {
        return attribute;
    }

    public AttributeGroupType getAttributeGroupType() {
        return attributeGroupType;
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
    
    public static List<BuiltInAttribute> getDemandTypes() {

        return demandAttributeType;
    }

    public static List<BuiltInAttribute> getRateUsageTypes() {

        return rateUsageAttributeType;
    }

    public static List<BuiltInAttribute> getUsageTypes() {

        return usageAttributeType;
    }
    
    public static List<BuiltInAttribute> getRateDemandTypes() {

        return rateDemandAttributeType;
    }
}
