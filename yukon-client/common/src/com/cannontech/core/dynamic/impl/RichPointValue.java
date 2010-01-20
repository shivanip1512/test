package com.cannontech.core.dynamic.impl;


import com.cannontech.common.pao.definition.model.PaoPointIdentifier;
import com.cannontech.core.dynamic.PointValueQualityHolder;

public class RichPointValue {

	private PointValueQualityHolder pointValueQualityHolder;
	private PaoPointIdentifier paoPointIdentifier;
	
	public RichPointValue(PointValueQualityHolder pointValueQualityHolder,
			PaoPointIdentifier paoPointIdentifier) {
		super();
		this.pointValueQualityHolder = pointValueQualityHolder;
		this.paoPointIdentifier = paoPointIdentifier;
	}
	
	public PaoPointIdentifier getPaoPointIdentifier() {
		return paoPointIdentifier;
	}
	public PointValueQualityHolder getPointValueQualityHolder() {
		return pointValueQualityHolder;
	}
}
