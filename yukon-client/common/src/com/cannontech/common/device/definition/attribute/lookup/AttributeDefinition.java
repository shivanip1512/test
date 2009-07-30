package com.cannontech.common.device.definition.attribute.lookup;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.springframework.core.style.ToStringCreator;

import com.cannontech.common.device.attribute.model.Attribute;
import com.cannontech.common.device.definition.model.PaoPointTemplate;
import com.cannontech.common.device.definition.model.PaoPointIdentifier;
import com.cannontech.common.device.model.SimpleDevice;

public abstract class AttributeDefinition implements Comparable<AttributeDefinition> {

    private Attribute attribute = null;
        
    public AttributeDefinition(Attribute attribute) {
		super();
		this.attribute = attribute;
	}

	public Attribute getAttribute() {
		return attribute;
	}

    @Override
    public String toString() {
        ToStringCreator tsc = new ToStringCreator(this);
        tsc.append("attribute", getAttribute());
        return tsc.toString();
    }
    
    @Override
    public int compareTo(AttributeDefinition o) {
        return new CompareToBuilder()
            .append(getAttribute(), o.getAttribute())
            .toComparison();
    }

    public abstract PaoPointIdentifier getPointIdentifier(SimpleDevice device);
    public abstract boolean isPointTemplateAvailable();
    public abstract PaoPointTemplate getPointTemplate(SimpleDevice device);
}
