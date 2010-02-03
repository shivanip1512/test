package com.cannontech.common.pao.definition.attribute.lookup;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.cannontech.common.pao.YukonDevice;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.definition.model.PaoPointIdentifier;
import com.cannontech.common.pao.definition.model.PaoPointTemplate;
import com.cannontech.common.pao.definition.model.PointTemplate;

public class BasicAttributeDefinition extends AttributeDefinition {

    private PointTemplate pointTemplate;
	
	public PointTemplate getPointTemplate() {
        return pointTemplate;
    }

	public BasicAttributeDefinition(Attribute attribute, PointTemplate pointTemplate) {
		super(attribute);
        this.pointTemplate = pointTemplate;
	}
	
	
	@Override
	public PaoPointIdentifier getPointIdentifier(YukonPao pao) {
	    return new PaoPointIdentifier(pao.getPaoIdentifier(), pointTemplate.getPointIdentifier());
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
	    if (obj instanceof BasicAttributeDefinition == false) {
	        return false;
	    }
	    if (this == obj) {
	        return true;
	    }
	    BasicAttributeDefinition rhs = (BasicAttributeDefinition) obj;
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
