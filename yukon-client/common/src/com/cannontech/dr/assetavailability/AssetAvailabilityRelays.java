package com.cannontech.dr.assetavailability;

import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.google.common.collect.ImmutableMap;

public class AssetAvailabilityRelays {
    public static final int MAX_RELAY = 4; //highest relay supported by asset availability
    public static final ImmutableMap<Integer, ? extends Attribute> RELAY_ATTRIBUTES =
            ImmutableMap.of(
                1, BuiltInAttribute.RELAY_1_RUN_TIME_DATA_LOG,
                2, BuiltInAttribute.RELAY_2_RUN_TIME_DATA_LOG,
                3, BuiltInAttribute.RELAY_3_RUN_TIME_DATA_LOG,
                4, BuiltInAttribute.RELAY_4_RUN_TIME_DATA_LOG);
    
    public static final Attribute getAttributeForRelay(int relay) {
        return RELAY_ATTRIBUTES.get(relay);
    }
    
    public static final boolean isValidRelay(int relay) {
        return RELAY_ATTRIBUTES.containsKey(relay);
    }
}
