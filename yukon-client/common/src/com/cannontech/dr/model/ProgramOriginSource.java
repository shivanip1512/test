package com.cannontech.dr.model;

import com.cannontech.common.util.DatabaseRepresentationSource;

public enum ProgramOriginSource implements DatabaseRepresentationSource{

    MANUAL("Manual"),
    AUTOMATIC("Automatic"),
    EIM("EIM"),
    MULTISPEAK("Multispeak"),
    OPENADR("OpenADR");

    private final String originSource;

    private ProgramOriginSource(String originSource) {
        this.originSource = originSource;
    }

    private String getOriginSource() {
        return originSource;
    }

    @Override
    public String getDatabaseRepresentation() {
        return getOriginSource();
    }

}
