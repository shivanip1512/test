package com.cannontech.multispeak.data;

import java.util.HashSet;
import java.util.Set;

import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;

/**
 * Class which represents billing data for an MCT2XX
 */
public class MCT2XX extends MeterReadBase {
	
	@Override
	public Set<Attribute> getMeterReadCompatibleAttributes() {

		Set<Attribute> meterReadCompatibleAttributes = new HashSet<Attribute>();
		meterReadCompatibleAttributes.add(BuiltInAttribute.USAGE);
		return meterReadCompatibleAttributes;
	}
}