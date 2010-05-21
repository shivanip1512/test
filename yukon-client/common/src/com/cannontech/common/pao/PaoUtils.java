package com.cannontech.common.pao;

import org.apache.commons.lang.Validate;

import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.definition.model.PaoPointIdentifier;
import com.cannontech.common.pao.definition.model.PointIdentifier;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableList.Builder;

public class PaoUtils {
    private final Function<PaoIdentifier, Integer> paoIdentifierToPaoIdFunction = new Function<PaoIdentifier, Integer>() {
        public Integer apply(PaoIdentifier from) {
            return from.getPaoIdentifier().getPaoId();
        }
    };

    public static ImmutableList<YukonDevice> asDeviceList(Iterable<PaoIdentifier> identifiers) {
        Builder<YukonDevice> builder = ImmutableList.builder();
        buildSimpleDeviceList(builder, identifiers);
        return builder.build();
    }

    public static ImmutableList<SimpleDevice> asSimpleDeviceList(Iterable<PaoIdentifier> identifiers) {
    	Builder<SimpleDevice> builder = ImmutableList.builder();
    	buildSimpleDeviceList(builder, identifiers);
    	return builder.build();
    }
    
    private static void buildSimpleDeviceList(Builder<? super SimpleDevice> builder, Iterable<PaoIdentifier> identifiers) {
    	for (PaoIdentifier paoIdentifier : identifiers) {
    		Validate.isTrue(paoIdentifier.getPaoType().getPaoCategory() == PaoCategory.DEVICE, "all identifiers must refer to a DEVICE");
			builder.add(new SimpleDevice(paoIdentifier));
    	}
    }
    
    
    public static ImmutableMultimap<PointIdentifier, PaoIdentifier> mapPaoPointIdentifiers(Iterable<PaoPointIdentifier> paoPointIdentifiers) {
        com.google.common.collect.ImmutableMultimap.Builder<PointIdentifier, PaoIdentifier>  builder = ImmutableMultimap.builder();
        for(PaoPointIdentifier paoPointIdentifier : paoPointIdentifiers){
            builder.put(paoPointIdentifier.getPointIdentifier(), paoPointIdentifier.getPaoIdentifier());
        }
        return builder.build();
    }

    public static void validateDeviceType(YukonPao pao) {
        PaoType paoType = pao.getPaoIdentifier().getPaoType();
        validateDeviceType(paoType);
    }

    public static void validateDeviceType(PaoType paoType) {
        Validate.isTrue(paoType.getPaoCategory() == PaoCategory.DEVICE);
    }
    
    public Function<PaoIdentifier, Integer> getPaoIdFunction() {
        return paoIdentifierToPaoIdFunction;
    }
}
