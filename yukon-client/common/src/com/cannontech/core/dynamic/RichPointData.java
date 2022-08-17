package com.cannontech.core.dynamic;

import java.io.Serializable;

import com.cannontech.common.pao.definition.model.PaoPointIdentifier;
import com.cannontech.core.dynamic.PointValueQualityHolder;

public class RichPointData implements Serializable {
    private PointValueQualityHolder pointValue;
    private PaoPointIdentifier paoPointIdentifier;
    
    public RichPointData(PointValueQualityHolder pointValue,
            PaoPointIdentifier paoPointIdentifier) {
        this.pointValue = pointValue;
        this.paoPointIdentifier = paoPointIdentifier;
    }
    
    public PointValueQualityHolder getPointValue() {
        return pointValue;
    }
    
    public PaoPointIdentifier getPaoPointIdentifier() {
        return paoPointIdentifier;
    }

}
