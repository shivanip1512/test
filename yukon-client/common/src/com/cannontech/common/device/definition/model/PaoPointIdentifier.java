package com.cannontech.common.device.definition.model;

import com.cannontech.common.pao.PaoIdentifier;

public class PaoPointIdentifier {
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
}