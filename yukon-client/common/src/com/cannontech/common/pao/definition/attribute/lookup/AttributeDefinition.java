package com.cannontech.common.pao.definition.attribute.lookup;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.springframework.core.style.ToStringCreator;

import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.definition.model.PaoPointIdentifier;
import com.cannontech.common.pao.definition.model.PaoPointTemplate;
import com.cannontech.core.dao.NotFoundException;

public abstract class AttributeDefinition implements Comparable<AttributeDefinition> {

    protected BuiltInAttribute attribute = null;
        
    public AttributeDefinition(BuiltInAttribute attribute) {
		super();
		this.attribute = attribute;
	}

	public BuiltInAttribute getAttribute() {
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

    public abstract boolean isPointTemplateAvailable();
    public abstract PaoPointTemplate getPointTemplate(YukonPao pao);

    /**
     * Returns a PaoPointIdentifier for the attribute. Depending on the type
     * of attribute, this may or may not represent a point that actually
     * exists in the database. This method may throw an exception.
     * @param pao
     * @return
     */
    public abstract PaoPointIdentifier getPointIdentifier(YukonPao pao);

    /**
     * Will go to the database to ensure that a point actually exists. If a point
     * doesn't exist, will return null.
     * @param pao
     * @return
     */
    public abstract PaoPointIdentifier findActualPointIdentifier(YukonPao pao);
    
    /**
     * Attempts to return the pointId of the point for the attribute. Throws
     * an exception if the point does not exist.
     * @param pao
     * @return
     * @throws NotFoundException
     */
    public abstract int getPointId(YukonPao pao) throws NotFoundException;

}
