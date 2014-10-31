package com.cannontech.common.pao;

import java.util.Comparator;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.definition.model.PaoPointIdentifier;
import com.cannontech.common.pao.definition.model.PointIdentifier;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.data.lite.LiteYukonPAObject;
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
    
    // A place holder for a LiteYukonPAObject used to show a dummy element
    public static final LiteYukonPAObject LITEPAOBJECT_SYSTEM = new LiteYukonPAObject(0, "System Device", PaoType.SYSTEM, CtiUtilities.STRING_NONE, CtiUtilities.STRING_NONE);
    
    // A place holder for a LiteYukonPAObject, mostly used in option lists
    // that allow the user not to choose a LitYukonPAObject
    public static final LiteYukonPAObject LITEPAOBJECT_NONE = new LiteYukonPAObject(0, CtiUtilities.STRING_NONE, PaoType.SYSTEM, CtiUtilities.STRING_NONE, CtiUtilities.STRING_NONE);
    
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
    
    public static void validateDeviceType(YukonPao pao) {
        PaoType paoType = pao.getPaoIdentifier().getPaoType();
        validateDeviceType(paoType);
    }
    
    public static void validateDeviceType(PaoType paoType) {
        Validate.isTrue(paoType.getPaoCategory() == PaoCategory.DEVICE);
    }
    
    public static Function<PaoIdentifier, Integer> getPaoIdFunction() {
        return paoIdentifierToPaoIdFunction;
    }
    
    public static Function<YukonPao, Integer> getYukonPaoToPaoIdFunction() {
        return yukonPaoToPaoIdFunction;
    }
    
    public static boolean isValidPaoName(String name) {
        return StringUtils.containsNone(name, ILLEGAL_NAME_CHARS);
    }
    
    /**
     * Creates a YukonPao with the specified id and type. This is useful for testing and should probably never be used
     * for "real" code.
     */
    public YukonPao createYukonPao(final int paoId, final PaoType paoType) {
        return new YukonPao() {
            @Override
            public PaoIdentifier getPaoIdentifier() {
                return new PaoIdentifier(paoId, paoType);
            }
        };
    }
    
    public static final Comparator<LiteYukonPAObject> NAME_COMPARE = new Comparator<LiteYukonPAObject>() {
        @Override
        public int compare(LiteYukonPAObject o1, LiteYukonPAObject o2) {
            return o1.getPaoName().compareTo(o2.getPaoName());
        }
    };
    
}