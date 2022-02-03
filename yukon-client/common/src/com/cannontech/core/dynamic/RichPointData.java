package com.cannontech.core.dynamic;

import java.io.Serializable;

import com.cannontech.common.pao.definition.model.PaoPointIdentifier;

/**
 * A point data object that comes with pao point identifier already included.
 */
public class RichPointData implements Serializable {
    private PointValueQualityTagHolder pointValue;
    private PaoPointIdentifier paoPointIdentifier;
    
    public RichPointData(PointValueQualityTagHolder pointValue,
            PaoPointIdentifier paoPointIdentifier) {
        this.pointValue = pointValue;
        this.paoPointIdentifier = paoPointIdentifier;
    }
    
    public PointValueQualityTagHolder getPointValue() {
        return pointValue;
    }
    
    public PaoPointIdentifier getPaoPointIdentifier() {
        return paoPointIdentifier;
    }

}
