package com.cannontech.capcontrol.model;

import com.cannontech.common.pao.PaoIdentifier;

public class CymePaoPoint {

    private final String paoName;
    private final int pointId;
    private final PaoIdentifier paoIdentifier;
    
    public CymePaoPoint(PaoIdentifier paoIdentifier, String paoName, int pointId) {
        this.paoName = paoName;
        this.paoIdentifier = paoIdentifier;
        this.pointId = pointId;
    }

    public String getPaoName() {
        return paoName;
    }

    public PaoIdentifier getPaoIdentifier() {
        return paoIdentifier;
    }
    
    public int getPointId() {
        return pointId;
    }
}
