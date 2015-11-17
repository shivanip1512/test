package com.cannontech.capcontrol.model;

import com.cannontech.common.util.DatabaseRepresentationSource;

public enum CcEventSubType implements DatabaseRepresentationSource {
    StandardOperation(0),
    StandardFlipOperation(1),
    ManualOperation(2),
    ManualFlipOperation(3),
    ManualConfirmOperation(4);

    private int id;

    private CcEventSubType(int id) {
        this.id = id;
    }

    @Override
    public Object getDatabaseRepresentation() {
        return id;
    }
}
