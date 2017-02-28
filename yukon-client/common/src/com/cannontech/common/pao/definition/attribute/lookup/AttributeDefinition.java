package com.cannontech.common.pao.definition.attribute.lookup;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.StandardToStringStyle;
import org.apache.commons.lang3.builder.ToStringBuilder;

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
    public PaoPointIdentifier getPaoPointIdentifier(YukonPao pao) {
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
            PaoPointIdentifier paoPointIdentifier = getPaoPointIdentifier(pao);
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
        PaoPointIdentifier paoPointIdentifier = getPaoPointIdentifier(pao);
        int pointId = pointDao.getPointId(paoPointIdentifier);
        return pointId;
    }
    


    @Override
    public int compareTo(AttributeDefinition o) {
        return new CompareToBuilder()
            .append(getAttribute(), o.getAttribute())
            .toComparison();
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((attribute == null) ? 0 : attribute.hashCode());
        result = prime * result + ((pointTemplate == null) ? 0 : pointTemplate.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        AttributeDefinition other = (AttributeDefinition) obj;
        if (attribute != other.attribute) {
            return false;
        }
        if (pointTemplate == null) {
            if (other.pointTemplate != null) {
                return false;
            }
        } else if (!pointTemplate.equals(other.pointTemplate)) {
            return false;
        }
        return true;
    }
    
    @Override
    public String toString() {
        StandardToStringStyle style = new StandardToStringStyle();
        style.setFieldSeparator(", ");
        style.setUseShortClassName(true);
        ToStringBuilder builder = new ToStringBuilder(this, style);
        builder.append("attribute", getAttribute());
        builder.append("pointTemplate", pointTemplate);
        return builder.toString();
    }    
}