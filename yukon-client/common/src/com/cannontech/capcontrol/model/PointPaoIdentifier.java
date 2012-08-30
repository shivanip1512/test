package com.cannontech.capcontrol.model;

import com.cannontech.common.pao.PaoIdentifier;

public class PointPaoIdentifier {

    private Integer pointId;
    private PaoIdentifier paoIdentifier;
    private String paoName;
    
    public Integer getPointId() {
        return pointId;
    }
    public void setPointId(Integer pointId) {
        this.pointId = pointId;
    }
    public PaoIdentifier getPaoIdentifier() {
        return paoIdentifier;
    }
    public void setPaoIdentifier(PaoIdentifier paoIdentifier) {
        this.paoIdentifier = paoIdentifier;
    }
    public String getPaoName() {
        return paoName;
    }
    public void setPaoName(String paoName) {
        this.paoName = paoName;
    }
}
