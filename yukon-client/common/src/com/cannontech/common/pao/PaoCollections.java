package com.cannontech.common.pao;

import org.apache.commons.lang.Validate;

import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.definition.model.PaoPointIdentifier;
import com.cannontech.common.pao.definition.model.PointIdentifier;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableList.Builder;

public class PaoCollections {
    
    
    public static ImmutableList<YukonDevice> asDeviceList(Iterable<PaoIdentifier> identifiers) {
        Builder<YukonDevice> builder = ImmutableList.builder();
        for (PaoIdentifier paoIdentifier : identifiers) {
            Validate.isTrue(paoIdentifier.getPaoType().getPaoCategory() == PaoCategory.DEVICE, "all identifiers must refer to a DEVICE");
            builder.add(new SimpleDevice(paoIdentifier));
        }
        return builder.build();
    }
    
    
    public static ImmutableMultimap<PointIdentifier, PaoIdentifier> mapPaoPointIdentifiers(Iterable<PaoPointIdentifier> paoPointIdentifiers) {
        com.google.common.collect.ImmutableMultimap.Builder<PointIdentifier, PaoIdentifier>  builder = ImmutableMultimap.builder();
        for(PaoPointIdentifier paoPointIdentifier : paoPointIdentifiers){
            builder.put(paoPointIdentifier.getPointIdentifier(), paoPointIdentifier.getPaoIdentifier());
        }
        return builder.build();
    }
}
