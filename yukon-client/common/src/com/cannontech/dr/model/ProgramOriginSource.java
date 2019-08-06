package com.cannontech.dr.model;

import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.util.DatabaseRepresentationSource;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public enum ProgramOriginSource implements DatabaseRepresentationSource, DisplayableEnum {

    MANUAL("Manual"),
    AUTOMATIC("Automatic"),
    EIM("EIM"),
    MULTISPEAK("Multispeak"),
    OPENADR("OpenADR"),
    NONE("(none)");

    private final String originSource;

    private ProgramOriginSource(String originSource) {
        this.originSource = originSource;
    }

    private String getOriginSource() {
        return originSource;
    }

    private final static ImmutableMap<String, ProgramOriginSource> lookupByOriginSource;
    static {
        Builder<String, ProgramOriginSource> builder = ImmutableMap.builder();
        for (ProgramOriginSource programOriginSource : values()) {
            builder.put(programOriginSource.originSource, programOriginSource);
        }
        lookupByOriginSource = builder.build();
    }

    public static ProgramOriginSource getProgramOriginSource(String originSource) {
        ProgramOriginSource programOriginSource = lookupByOriginSource.get(originSource);
        if (programOriginSource == null) {
            return ProgramOriginSource.NONE;
        }
        return programOriginSource;
    }

    @Override
    public String getDatabaseRepresentation() {
        return getOriginSource();
    }

    @Override
    public String getFormatKey() {
        return "yukon.web.modules.dr.program.origin." + name();
    }

}
