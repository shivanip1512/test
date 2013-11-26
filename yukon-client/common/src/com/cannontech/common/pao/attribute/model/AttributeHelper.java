package com.cannontech.common.pao.attribute.model;

import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;

public class AttributeHelper {
    public static Set<Attribute> getTouAttributes(){
        Builder<Attribute> builder = ImmutableSet.builder();
        return builder.add(BuiltInAttribute.USAGE_RATE_A,
                           BuiltInAttribute.PEAK_DEMAND_RATE_A,
                           BuiltInAttribute.PEAK_DEMAND_FROZEN_RATE_A,
                           BuiltInAttribute.RECEIVED_KWH_RATE_A,
                           BuiltInAttribute.NET_KWH_RATE_A,
                           BuiltInAttribute.SUM_KWH_RATE_A,
                           BuiltInAttribute.PEAK_KVAR_RATE_A,
                           BuiltInAttribute.KVARH_RATE_A,
                           BuiltInAttribute.RECEIVED_KVARH_RATE_A,
                           BuiltInAttribute.NET_DELIVERED_KVARH_RATE_A,
                           BuiltInAttribute.NET_RECEIVED_KVARH_RATE_A,
                           
                           BuiltInAttribute.USAGE_RATE_B,
                           BuiltInAttribute.PEAK_DEMAND_RATE_B,
                           BuiltInAttribute.PEAK_DEMAND_FROZEN_RATE_B,
                           BuiltInAttribute.RECEIVED_KWH_RATE_B,
                           BuiltInAttribute.NET_KWH_RATE_B,
                           BuiltInAttribute.SUM_KWH_RATE_B,
                           BuiltInAttribute.PEAK_KVAR_RATE_B,
                           BuiltInAttribute.KVARH_RATE_B,
                           BuiltInAttribute.RECEIVED_KVARH_RATE_B,
                           BuiltInAttribute.NET_DELIVERED_KVARH_RATE_B,
                           BuiltInAttribute.NET_RECEIVED_KVARH_RATE_B,
                           
                           BuiltInAttribute.USAGE_RATE_C,
                           BuiltInAttribute.PEAK_DEMAND_RATE_C,
                           BuiltInAttribute.PEAK_DEMAND_FROZEN_RATE_C,
                           BuiltInAttribute.RECEIVED_KWH_RATE_C,
                           BuiltInAttribute.NET_KWH_RATE_C,
                           BuiltInAttribute.SUM_KWH_RATE_C,
                           BuiltInAttribute.PEAK_KVAR_RATE_C,
                           BuiltInAttribute.KVARH_RATE_C,
                           BuiltInAttribute.RECEIVED_KVARH_RATE_C,
                           BuiltInAttribute.NET_DELIVERED_KVARH_RATE_C,
                           BuiltInAttribute.NET_RECEIVED_KVARH_RATE_C,
                           
                           BuiltInAttribute.USAGE_RATE_D,
                           BuiltInAttribute.PEAK_DEMAND_RATE_D,
                           BuiltInAttribute.PEAK_DEMAND_FROZEN_RATE_D,
                           BuiltInAttribute.RECEIVED_KWH_RATE_D,
                           BuiltInAttribute.NET_KWH_RATE_D,
                           BuiltInAttribute.SUM_KWH_RATE_D,
                           BuiltInAttribute.PEAK_KVAR_RATE_D,
                           BuiltInAttribute.KVARH_RATE_D,
                           BuiltInAttribute.RECEIVED_KVARH_RATE_D,
                           BuiltInAttribute.NET_DELIVERED_KVARH_RATE_D,
                           BuiltInAttribute.NET_RECEIVED_KVARH_RATE_D,
                           
                           BuiltInAttribute.USAGE_RATE_E,
                           BuiltInAttribute.PEAK_DEMAND_RATE_E,
                           BuiltInAttribute.RECEIVED_KWH_RATE_E,
                           BuiltInAttribute.NET_KWH_RATE_E
                           ).build();
    }
    
    public static Set<Attribute> getTouUsageAttributes(){
        Builder<Attribute> builder = ImmutableSet.builder();
        return builder.add(BuiltInAttribute.USAGE_RATE_A,
                           BuiltInAttribute.USAGE_RATE_B,
                           BuiltInAttribute.USAGE_RATE_C,
                           BuiltInAttribute.USAGE_RATE_D,
                           BuiltInAttribute.USAGE_RATE_E
                           ).build();
    }
}
