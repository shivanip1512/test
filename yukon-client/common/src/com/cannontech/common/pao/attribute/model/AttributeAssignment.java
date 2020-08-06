package com.cannontech.common.pao.attribute.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.cannontech.common.pao.definition.model.PaoTypePointIdentifier;

public class AttributeAssignment extends Assignment {

    private CustomAttribute customAttribute;

    public CustomAttribute getCustomAttribute() {
        return customAttribute;
    }

    public void setCustomAttribute(CustomAttribute customAttribute) {
        this.customAttribute = customAttribute;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE)
                + System.getProperty("line.separator");
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((customAttribute == null) ? 0 : customAttribute.hashCode());
        return result;
    }
    
    public boolean isAssignedTo(PaoTypePointIdentifier paoTypePointIdentifier) {
        return getPaoType() == paoTypePointIdentifier.getPaoType()
                && getOffset() == paoTypePointIdentifier.getPointIdentifier().getOffset()
                && getPointType() == paoTypePointIdentifier.getPointIdentifier().getPointType();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        AttributeAssignment other = (AttributeAssignment) obj;
        if (customAttribute == null) {
            if (other.customAttribute != null)
                return false;
        } else if (!customAttribute.equals(other.customAttribute))
            return false;
        return true;
    }
}
