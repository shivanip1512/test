package com.cannontech.common.device.programming.model;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.util.DatabaseRepresentationSource;

public enum MeterProgramSource implements DisplayableEnum, DatabaseRepresentationSource {
    YUKON("R"), 
    OPTICAL("P"), 
    NEW("N"), 
    UNPROGRAMMED("U"),
    OLD_FIRMWARE("X")
    ;

    private static Map<String, MeterProgramSource> prefixToSource = Arrays.stream(MeterProgramSource.values())
            .collect(Collectors.toMap(MeterProgramSource::getPrefix,Function.identity()));

    private String prefix;

    private MeterProgramSource(String prefix) {
        this.prefix = prefix;
    }

    public String getPrefix() {
        return prefix;
    }

    public static MeterProgramSource getByPrefix(String prefix) {
        return prefixToSource.get(prefix);
    }

    public boolean isYukon() {
        return this == YUKON;
    }

    public boolean isNotYukon() {
        return !isYukon();
    }
    
    public boolean isOldFirmware() {
        return this == OLD_FIRMWARE;
    }
    
    public boolean isFailure() {
        return isOldFirmware() || this == UNPROGRAMMED;
    }
    
    @Override
    public String getFormatKey() {
        return "yukon.web.modules.amr.meterProgramming.source." + name();
    }

    @Override
    public Object getDatabaseRepresentation() {
        return getPrefix();
    }
}
