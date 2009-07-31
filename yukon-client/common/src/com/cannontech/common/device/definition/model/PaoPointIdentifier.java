package com.cannontech.common.device.definition.model;

import com.cannontech.common.pao.PaoIdentifier;

public class PaoPointIdentifier {
    private PaoIdentifier paoIdentifier;
    private PointIdentifier devicePointIdentifier;
    
    public PaoPointIdentifier(PaoIdentifier paoIdentifier,
            PointIdentifier devicePointIdentifier) {
        super();
		this.paoIdentifier = paoIdentifier;
        this.devicePointIdentifier = devicePointIdentifier;
    }
    public void setPaoIdentifier(PaoIdentifier paoIdentifier) {
		this.paoIdentifier = paoIdentifier;
	}
    public PaoIdentifier getPaoIdentifier() {
		return paoIdentifier;
	}
    public PointIdentifier getDevicePointIdentifier() {
        return devicePointIdentifier;
    }
    public void setDevicePointIdentifier(PointIdentifier devicePointIdentifier) {
        this.devicePointIdentifier = devicePointIdentifier;
    }
    
}
