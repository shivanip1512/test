package com.cannontech.common.pao.attribute.model;

import com.cannontech.database.data.lite.LiteStateGroup;

public class AttributeStateGroup {
    private final Attribute attribute;
    private final LiteStateGroup stateGroup;

    public AttributeStateGroup(Attribute attribute, LiteStateGroup stateGroup) {
        this.attribute = attribute;
        this.stateGroup = stateGroup;
    }

    public Attribute getAttribute() {
        return attribute;
    }

    public LiteStateGroup getStateGroup() {
        return stateGroup;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((attribute == null) ? 0 : attribute.hashCode());
        result = prime * result + ((stateGroup == null) ? 0 : stateGroup.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AttributeStateGroup other = (AttributeStateGroup) obj;
        if (attribute == null) {
            if (other.attribute != null)
                return false;
        } else if (!attribute.equals(other.attribute))
            return false;
        if (stateGroup == null) {
            if (other.stateGroup != null)
                return false;
        } else if (!stateGroup.equals(other.stateGroup))
            return false;
        return true;
    }
}