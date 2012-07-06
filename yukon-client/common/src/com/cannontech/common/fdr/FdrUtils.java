package com.cannontech.common.fdr;

import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

/**
 * A utility class for FDR-related methods and constants.
 */
public class FdrUtils {
    
    public static final String EMPTY = "EMPTY";
    public static final ImmutableMap<FdrInterfaceOption, FdrInterfaceType> OPTION_TO_INTERFACE_MAP;
    
    static {
        Map<FdrInterfaceOption, FdrInterfaceType> tempMap = Maps.newEnumMap(FdrInterfaceOption.class);
        for(FdrInterfaceType fdrInterface : FdrInterfaceType.values()) {
            for(FdrInterfaceOption option : fdrInterface.getInterfaceOptionsList()) {
                tempMap.put(option, fdrInterface);
            }
        }
        OPTION_TO_INTERFACE_MAP = ImmutableMap.copyOf(tempMap);
    }
}
