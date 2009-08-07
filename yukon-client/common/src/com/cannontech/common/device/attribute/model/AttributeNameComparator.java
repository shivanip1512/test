package com.cannontech.common.device.attribute.model;

import java.util.Comparator;

public class AttributeNameComparator implements Comparator<Attribute> {

	@Override
	public int compare(Attribute attr1, Attribute attr2) {
		return attr1.getKey().compareTo(attr2.getKey());
	}
}
