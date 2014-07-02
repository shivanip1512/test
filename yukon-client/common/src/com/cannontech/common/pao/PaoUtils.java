package com.cannontech.common.pao;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.definition.model.PaoDefinition;
import com.cannontech.common.pao.definition.model.PaoMultiPointIdentifier;
import com.cannontech.common.pao.definition.model.PaoPointIdentifier;
import com.cannontech.common.pao.definition.model.PointIdentifier;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimaps;

public class PaoUtils {
    public final static char[] ILLEGAL_NAME_CHARS = { '\'', ',', '|', '"', '/', '\\' };

    private final static Function<PaoIdentifier, Integer> paoIdentifierToPaoIdFunction =
        new Function<PaoIdentifier, Integer>() {
            @Override
            public Integer apply(PaoIdentifier from) {
                return from.getPaoId();
            }
        };

    private final static Function<YukonPao, PaoType> yukonPaoToPaoTypeFunction = new Function<YukonPao, PaoType>() {
        @Override
        public PaoType apply(YukonPao from) {
            return from.getPaoIdentifier().getPaoType();
        }
    };

    private final static Function<YukonPao, PaoIdentifier> yukonPaoToPaoIdentifierFunction =
        new Function<YukonPao, PaoIdentifier>() {
            @Override
            public PaoIdentifier apply(YukonPao from) {
                return from.getPaoIdentifier();
            }
        };

    @SuppressWarnings("unchecked")
    public final static <T extends YukonPao> Function<T, PaoIdentifier> paoIdentifierFunction() {
        return (Function<T, PaoIdentifier>) yukonPaoToPaoIdentifierFunction;
    }

    private final static Function<PaoDefinition, PaoType> paoDefinitionToPaoTypeFunction =
        new Function<PaoDefinition, PaoType>() {
            @Override
            public PaoType apply(PaoDefinition from) {
                PaoType paoType = from.getType();
                return paoType;
            }
        };

    private final static Function<YukonPao, Integer> yukonPaoToPaoIdFunction = new Function<YukonPao, Integer>() {
        @Override
        public Integer apply(YukonPao from) {
            return from.getPaoIdentifier().getPaoId();
        }
    };

    private final static Function<DisplayablePao, String> paoToNameFunction = new Function<DisplayablePao, String>() {
        @Override
        public String apply(DisplayablePao from) {
            return from.getName();
        }
    };

    public static YukonDevice asYukonDevice(YukonPao pao) {
        if (pao instanceof YukonDevice) {
            YukonDevice device = (YukonDevice) pao;
            return device;
        }
        return new SimpleDevice(pao);
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

    private static void buildSimpleDeviceList(Builder<? super SimpleDevice> builder,
            Iterable<PaoIdentifier> identifiers) {
        for (PaoIdentifier paoIdentifier : identifiers) {
            Validate.isTrue(paoIdentifier.getPaoType().getPaoCategory() == PaoCategory.DEVICE,
                "all identifiers must refer to a DEVICE");
            builder.add(new SimpleDevice(paoIdentifier));
        }
    }

    public static ImmutableMultimap<PointIdentifier, PaoIdentifier> mapPaoPointIdentifiers(
            Iterable<PaoPointIdentifier> paoPointIdentifiers) {
        com.google.common.collect.ImmutableMultimap.Builder<PointIdentifier, PaoIdentifier> builder =
            ImmutableMultimap.builder();
        for (PaoPointIdentifier paoPointIdentifier : paoPointIdentifiers) {
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

    public static ImmutableList<String> asPaoNames(Iterable<? extends DisplayablePao> paos) {
        Iterable<String> transformedList = Iterables.transform(paos, paoToNameFunction);
        return ImmutableList.copyOf(transformedList);
    }
    
    public static <T extends YukonPao> ImmutableMap<PaoIdentifier, T> indexYukonPaos(Iterable<T> paos) {
        return Maps.uniqueIndex(paos, yukonPaoToPaoIdentifierFunction);
    }

    public static <T extends YukonPao> ImmutableMap<Integer, T> indexYukonPaosByPaoId(Iterable<T> paos) {
        return Maps.uniqueIndex(paos, yukonPaoToPaoIdFunction);
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

    public static Iterable<PaoIdentifier> convertPaoMultisToPaoIdentifiers(
            Iterable<PaoMultiPointIdentifier> paoMultiPoints) {
        return Iterables.transform(paoMultiPoints, new Function<PaoMultiPointIdentifier, PaoIdentifier>() {
            @Override
            public PaoIdentifier apply(PaoMultiPointIdentifier from) {
                return from.getPao();
            }
        });
    }

    public static boolean isValidPaoName(String name) {
        return StringUtils.containsNone(name, ILLEGAL_NAME_CHARS);
    }
}
