package com.cannontech.common.pao.definition.attribute.lookup;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.definition.model.PaoPointIdentifier;
import com.cannontech.common.pao.definition.model.PaoPointTemplate;
import com.cannontech.common.pao.definition.model.PointTemplate;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PointDao;

public class BasicAttributeDefinition extends AttributeDefinition {

    private PointTemplate pointTemplate;
    private PointDao pointDao;
	
	public PointTemplate getPointTemplate() {
        return pointTemplate;
    }

	public BasicAttributeDefinition(BuiltInAttribute attribute, PointTemplate pointTemplate, 
	                                PointDao pointDao) {
		super(attribute);
        this.pointTemplate = pointTemplate;
        this.pointDao = pointDao;
	}
	
	@Override
	public PaoPointIdentifier getPointIdentifier(YukonPao pao) {
	    return new PaoPointIdentifier(pao.getPaoIdentifier(), pointTemplate.getPointIdentifier());
	}

    @Override
    public PaoPointIdentifier findActualPointIdentifier(YukonPao pao) {
        try {
            PaoPointIdentifier paoPointIdentifier = getPointIdentifier(pao);
            return paoPointIdentifier;
        } catch (NotFoundException nfe) {
            return null;
        }
    }

    @Override
    public int getPointId(YukonPao pao) throws NotFoundException {
        PaoPointIdentifier paoPointIdentifier = getPointIdentifier(pao);
        int pointId = pointDao.getPointId(paoPointIdentifier);
        return pointId;
    }

	@Override
	public PaoPointTemplate getPointTemplate(YukonPao pao) {
	    return new PaoPointTemplate(pao.getPaoIdentifier(), pointTemplate);
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
