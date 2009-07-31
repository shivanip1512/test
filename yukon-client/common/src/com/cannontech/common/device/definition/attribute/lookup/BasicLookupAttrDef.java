package com.cannontech.common.device.definition.attribute.lookup;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.cannontech.common.device.attribute.model.Attribute;
import com.cannontech.common.device.definition.model.PaoPointIdentifier;
import com.cannontech.common.device.definition.model.PaoPointTemplate;
import com.cannontech.common.device.definition.model.PointTemplate;
import com.cannontech.common.pao.YukonDevice;

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
	public PaoPointIdentifier getPointIdentifier(YukonDevice device) {
	    return new PaoPointIdentifier(device.getPaoIdentifier(), pointTemplate.getPointIdentifier());
	}
	
	@Override
	public PaoPointTemplate getPointTemplate(YukonDevice device) {
	    return new PaoPointTemplate(device.getPaoIdentifier(), pointTemplate);
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
