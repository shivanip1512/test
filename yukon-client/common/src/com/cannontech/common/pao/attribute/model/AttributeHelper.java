package com.cannontech.common.pao.attribute.model;

import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;

public class AttributeHelper {
    public static Set<Attribute> getTouAttributes(){
        Builder<Attribute> builder = ImmutableSet.builder();
        return builder.add(BuiltInAttribute.TOU_RATE_A_USAGE,
                           BuiltInAttribute.TOU_RATE_A_PEAK_DEMAND,
                           BuiltInAttribute.TOU_RATE_B_USAGE,
                           BuiltInAttribute.TOU_RATE_B_PEAK_DEMAND,
                           BuiltInAttribute.TOU_RATE_C_USAGE,
                           BuiltInAttribute.TOU_RATE_C_PEAK_DEMAND,
                           BuiltInAttribute.TOU_RATE_D_USAGE,
                           BuiltInAttribute.TOU_RATE_D_PEAK_DEMAND).build();
    }
    
    public static Set<Attribute> getTouUsageAttributes(){
        Builder<Attribute> builder = ImmutableSet.builder();
        return builder.add(BuiltInAttribute.TOU_RATE_A_USAGE, 
                           BuiltInAttribute.TOU_RATE_B_USAGE, 
                           BuiltInAttribute.TOU_RATE_C_USAGE, 
                           BuiltInAttribute.TOU_RATE_D_USAGE).build();
    }
    
}
