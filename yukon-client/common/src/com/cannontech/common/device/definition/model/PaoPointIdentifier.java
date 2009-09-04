package com.cannontech.common.device.definition.model;

import java.io.Serializable;

import com.cannontech.common.pao.PaoIdentifier;

public class PaoPointIdentifier implements Serializable {
    private PaoIdentifier paoIdentifier;
    private PointIdentifier pointIdentifier;
    
    public PaoPointIdentifier(PaoIdentifier paoIdentifier,
            PointIdentifier pointIdentifier) {
        super();
		this.paoIdentifier = paoIdentifier;
        this.pointIdentifier = pointIdentifier;
    }
    public void setPaoIdentifier(PaoIdentifier paoIdentifier) {
		this.paoIdentifier = paoIdentifier;
	}
    public PaoIdentifier getPaoIdentifier() {
		return paoIdentifier;
	}
    public PointIdentifier getPointIdentifier() {
        return pointIdentifier;
    }
    public void setPointIdentifier(PointIdentifier pointIdentifier) {
        this.pointIdentifier = pointIdentifier;
    }
    public PaoTypePointIdentifier getPaoTypePointIdentifier() {
        return new PaoTypePointIdentifier(paoIdentifier.getPaoType(), pointIdentifier);
    }
    
}
