package com.cannontech.common.pao.definition.attribute.lookup;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.springframework.core.style.ToStringCreator;

import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.definition.model.PaoPointIdentifier;
import com.cannontech.common.pao.definition.model.PaoPointTemplate;
import com.cannontech.common.pao.definition.model.PointTemplate;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PointDao;

public class AttributeDefinition implements Comparable<AttributeDefinition> {

    private BuiltInAttribute attribute = null;
    private PointTemplate pointTemplate;
    private PointDao pointDao;
    
    public AttributeDefinition(BuiltInAttribute attribute, PointTemplate pointTemplate, PointDao pointDao) {
        this.attribute = attribute;
        this.pointTemplate = pointTemplate;
        this.pointDao = pointDao;
    }
    
    public PointTemplate getPointTemplate() {
        return pointTemplate;
    }

	public BuiltInAttribute getAttribute() {
		return attribute;
	}

    public PaoPointTemplate getPointTemplate(YukonPao pao) {
        return new PaoPointTemplate(pao.getPaoIdentifier(), pointTemplate);
    }

    /**
     * Returns a PaoPointIdentifier for the attribute. Depending on the type
     * of attribute, this may or may not represent a point that actually
     * exists in the database. This method may throw an exception.
     * @param pao
     * @return
     */
    public PaoPointIdentifier getPointIdentifier(YukonPao pao) {
        return new PaoPointIdentifier(pao.getPaoIdentifier(), pointTemplate.getPointIdentifier());
    }

    /**
     * Will go to the database to ensure that a point actually exists. If a point
     * doesn't exist, will return null.
     * @param pao
     * @return
     */
    public PaoPointIdentifier findActualPointIdentifier(YukonPao pao) {
        try {
            PaoPointIdentifier paoPointIdentifier = getPointIdentifier(pao);
            pointDao.getPointId(paoPointIdentifier);
            return paoPointIdentifier;
        } catch (NotFoundException nfe) {
            return null;
        }
    }
    
    /**
     * Attempts to return the pointId of the point for the attribute. Throws
     * an exception if the point does not exist.
     * @param pao
     * @return
     * @throws NotFoundException
     */
    public int getPointId(YukonPao pao) throws NotFoundException {
        PaoPointIdentifier paoPointIdentifier = getPointIdentifier(pao);
        int pointId = pointDao.getPointId(paoPointIdentifier);
        return pointId;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof AttributeDefinition == false) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        AttributeDefinition rhs = (AttributeDefinition) obj;
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
    
}