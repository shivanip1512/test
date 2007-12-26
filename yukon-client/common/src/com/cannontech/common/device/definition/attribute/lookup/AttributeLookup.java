package com.cannontech.common.device.definition.attribute.lookup;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.springframework.core.style.ToStringCreator;

import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.device.attribute.model.Attribute;

public abstract class AttributeLookup {

    private Attribute attribute = null;
        
    public AttributeLookup(Attribute attribute) {
		super();
		this.attribute = attribute;
	}

	public Attribute getAttribute() {
		return attribute;
	}

	public void setAttribute(Attribute attribute) {
		this.attribute = attribute;
	}

	public boolean equals(Object obj) {
        if (obj instanceof AttributeLookup == false) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        AttributeLookup attrDefinition = (AttributeLookup) obj;
        return new EqualsBuilder().append(attribute,
        								  attrDefinition.getAttribute())
                                  .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder(17, 39).append(getAttribute())
                                          .toHashCode();
    }
    
    @Override
    public String toString() {
        ToStringCreator tsc = new ToStringCreator(this);
        tsc.append("attribute", getAttribute());
        return tsc.toString();
    }

    // this probably isn't sufficient for what this was designed for, probably needs to return a LitePoint/pointId
    public abstract String getPointRefName(YukonDevice device);
}
