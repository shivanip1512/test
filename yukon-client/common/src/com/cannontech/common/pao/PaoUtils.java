package com.cannontech.common.pao;

import org.apache.commons.lang.Validate;

import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.definition.model.PaoDefinition;
import com.cannontech.common.pao.definition.model.PaoPointIdentifier;
import com.cannontech.common.pao.definition.model.PointIdentifier;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimaps;
import com.google.common.collect.ImmutableList.Builder;

public class PaoUtils {
    private static final Function<PaoIdentifier, Integer> paoIdentifierToPaoIdFunction = new Function<PaoIdentifier, Integer>() {
        public Integer apply(PaoIdentifier from) {
            return from.getPaoIdentifier().getPaoId();
        }
    };

    private static final Function<YukonPao, PaoType> yukonPaoToPaoTypeFunction = new Function<YukonPao, PaoType>() {
        public PaoType apply(YukonPao from) {
            return from.getPaoIdentifier().getPaoType();
        }
    };
    
    private static final Function<YukonPao, PaoIdentifier> yukonPaoToPaoIdentifierFunction = new Function<YukonPao, PaoIdentifier>() {
        public PaoIdentifier apply(YukonPao from) {
            return from.getPaoIdentifier();
        }
    };

    private static final Function<PaoDefinition, PaoType> paoDefinitionToPaoTypeFunction = new Function<PaoDefinition, PaoType>() {
		@Override
		public PaoType apply(PaoDefinition from) {
			PaoType paoType = from.getType();
			return paoType;
		}
	};
	
    private static final Function<YukonPao, Integer> yukonPaoToPaoIdFunction = new Function<YukonPao, Integer>() {
        public Integer apply(YukonPao from) {
            return from.getPaoIdentifier().getPaoId();
        }
    };

    public static YukonDevice asYukonDevice(YukonPao pao) {
        if (pao instanceof YukonDevice) {
            YukonDevice device = (YukonDevice)pao;
            return device;
        } else {
            return new SimpleDevice(pao);
        }
    }
    
    public static ImmutableList<SimpleDevice> asSimpleDeviceListFromPaos(Iterable<? extends YukonPao> paos) {
        return asSimpleDeviceList(asPaoIdentifiers(paos));
    }
    
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
    
    public static ImmutableList<Integer> asPaoIdList(Iterable<? extends YukonPao> paos) {
        Iterable<Integer> transformedList = Iterables.transform(paos, yukonPaoToPaoIdFunction);
        return ImmutableList.copyOf(transformedList);
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
    
    public static <T extends YukonPao> ImmutableMultimap<PaoType, T> mapPaoTypes(Iterable<T> paos) {
        ImmutableListMultimap<PaoType, T> result = Multimaps.index(paos, yukonPaoToPaoTypeFunction);
        return result;
    }
    
    public static Iterable<PaoIdentifier> asPaoIdentifiers(Iterable<? extends YukonPao> paos) {
        return Iterables.transform(paos, yukonPaoToPaoIdentifierFunction);
    }
    
    public static void validateDeviceType(YukonPao pao) {
        PaoType paoType = pao.getPaoIdentifier().getPaoType();
        validateDeviceType(paoType);
    }

    public static void validateDeviceType(PaoType paoType) {
        Validate.isTrue(paoType.getPaoCategory() == PaoCategory.DEVICE);
    }
    
    public static Function<YukonPao, PaoType> getPaoTypeFunction() {
        return yukonPaoToPaoTypeFunction;
    }

    public static Function<PaoIdentifier, Integer> getPaoIdFunction() {
        return paoIdentifierToPaoIdFunction;
    }
    
    public static Function<PaoDefinition, PaoType> getPaoDefinitionToPaoTypeFunction() {
		return paoDefinitionToPaoTypeFunction;
	}
    
    public static Function<YukonPao, Integer> getYukonPaoToPaoIdFunction() {
        return yukonPaoToPaoIdFunction;
    }
    
    public static Function<YukonPao, PaoIdentifier> getYukonPaoToPaoIdentifierFunction() {
        return yukonPaoToPaoIdentifierFunction;
    }
}
