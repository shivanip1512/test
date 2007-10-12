package com.cannontech.common.device.definition.attribute.lookup;

import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.device.attribute.model.Attribute;

public class BasicLookupAttrDef extends AttributeLookup{

	private String pointName;
	
	/** 
	 * Constructor for BasicAttributeDef
	 * Sets the AttributeLookup to BASIC.
	 * @param attribute
	 */
	public BasicLookupAttrDef(Attribute attribute) {
		super(attribute);//, AttributeLookup.BASIC);
	}
	
	public String getPointName() {
		return pointName;
	}
	
	public void setPointName(String pointName) {
		this.pointName = pointName;
	}

	@Override
	public String getPointRefName(YukonDevice device) {
		return getPointName();
	}
}
