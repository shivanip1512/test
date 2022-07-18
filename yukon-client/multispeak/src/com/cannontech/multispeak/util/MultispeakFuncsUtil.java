package com.cannontech.multispeak.util;

import java.util.EnumSet;
import java.util.List;

import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.multispeak.client.MspAttribute;

public class MultispeakFuncsUtil {
    
    public static EnumSet<BuiltInAttribute> getBuiltInAttributesForVendor(List<MspAttribute> vendorAttributes) {
        EnumSet<BuiltInAttribute> attributesToLoad = EnumSet.noneOf(BuiltInAttribute.class);

        for (MspAttribute attribute : vendorAttributes) {
            attributesToLoad.addAll(attribute.getBuiltInAttributes());
        }
        return attributesToLoad;
    }
    
}
