package com.cannontech.common.pao.attribute.model;

import java.util.Comparator;

import com.google.common.base.Function;
import com.google.common.collect.Ordering;

public class AttributeNameComparator implements Comparator<Attribute> {

    public static Comparator<Attribute> attributeComparator() {
        Ordering<Attribute> descriptionOrdering = Ordering.natural()
            .onResultOf(new Function<Attribute, String>() {
                public String apply(Attribute from) {
                    return from.getDescription();
                }
            });
        return descriptionOrdering;
    }
    
	@Override
	public int compare(Attribute attr1, Attribute attr2) {
		return attr1.getKey().compareTo(attr2.getKey());
	}
}
