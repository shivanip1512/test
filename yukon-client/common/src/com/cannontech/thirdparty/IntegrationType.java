package com.cannontech.thirdparty;

import com.cannontech.common.util.DatabaseRepresentationSource;

public enum IntegrationType implements DatabaseRepresentationSource {
    DIGI,
    ELSTER;

    @Override
    public Object getDatabaseRepresentation() {
        return name();
    }

}
