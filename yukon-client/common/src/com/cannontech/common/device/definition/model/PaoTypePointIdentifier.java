package com.cannontech.common.device.definition.model;

import com.cannontech.common.pao.PaoType;

public class PaoTypePointIdentifier {
    private PaoType paoType;
    public PaoTypePointIdentifier(PaoType paoType,
            PointIdentifier pointIdentifier) {
        super();
        this.paoType = paoType;
        this.pointIdentifier = pointIdentifier;
    }
    public PaoType getPaoType() {
        return paoType;
    }
    public PointIdentifier getPointIdentifier() {
        return pointIdentifier;
    }
    private PointIdentifier pointIdentifier;
}
