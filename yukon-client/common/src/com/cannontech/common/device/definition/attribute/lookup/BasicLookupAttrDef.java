package com.cannontech.common.device.definition.attribute.lookup;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.cannontech.common.device.attribute.model.Attribute;
import com.cannontech.common.device.definition.model.DevicePointTemplate;
import com.cannontech.common.device.definition.model.DevicePointIdentifier;
import com.cannontech.common.device.definition.model.PointTemplate;
import com.cannontech.common.device.model.SimpleDevice;

public class BasicLookupAttrDef extends AttributeDefinition {

    private PointTemplate pointTemplate;
	
	public PointTemplate getPointTemplate() {
        return pointTemplate;
    }

    /** 
	 * Constructor for BasicAttributeDef
	 * Sets the AttributeLookup to BASIC.
	 * @param attribute
	 */
	public BasicLookupAttrDef(Attribute attribute, PointTemplate pointTemplate) {
		super(attribute);
        this.pointTemplate = pointTemplate;
	}
	
	
	@Override
	public DevicePointIdentifier getPointIdentifier(SimpleDevice device) {
	    return new DevicePointIdentifier(device, pointTemplate.getPointIdentifier());
	}
	
	@Override
	public DevicePointTemplate getPointTemplate(SimpleDevice device) {
	    return new DevicePointTemplate(device, pointTemplate);
	}
	
	@Override
	public boolean isPointTemplateAvailable() {
	    return true;
	}
	
	@Override
	public boolean equals(Object obj) {
	    if (obj instanceof BasicLookupAttrDef == false) {
	        return false;
	    }
	    if (this == obj) {
	        return true;
	    }
	    BasicLookupAttrDef rhs = (BasicLookupAttrDef) obj;
	    return new EqualsBuilder()
	        .append(getAttribute(), rhs.getAttribute())
	        .append(getPointTemplate(), rhs.getPointTemplate())
	        .isEquals();
	}
	
	@Override
	public int hashCode() {
        return new HashCodeBuilder(6599, 1289)
            .append(getAttribute())
            .append(getPointTemplate())
            .toHashCode();
	}
	
}
