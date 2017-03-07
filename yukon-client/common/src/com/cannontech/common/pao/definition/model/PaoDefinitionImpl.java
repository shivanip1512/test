package com.cannontech.common.pao.definition.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.cannontech.common.pao.PaoType;

/**
 * Class which contains pao definition data
 */
public class PaoDefinitionImpl implements PaoDefinition {

    private PaoType type = null;
    private String displayName = null;
    private String displayGroup = null;
    private boolean changeable = false;
    private String changeGroup = null;
    private boolean createable = true;

    public PaoDefinitionImpl(PaoType type, String displayName, String displayGroup, String changeGroup,
            boolean createable) {
        this.type = type;
        this.displayName = displayName;
        this.displayGroup = displayGroup;
        this.changeGroup = changeGroup;
        this.changeable = changeGroup != null;
        this.createable = createable;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public PaoType getType() {
        return type;
    }

    public void setType(PaoType type) {
        this.type = type;
    }

    @Override
    public boolean isChangeable() {
        return changeable;
    }

    public void setChangeable(boolean changeable) {
        this.changeable = changeable;
    }

    @Override
    public String getDisplayGroup() {
        return displayGroup;
    }

    public void setDisplayGroup(String displayGroup) {
        this.displayGroup = displayGroup;
    }
    
    @Override
    public String getChangeGroup() {
        return changeGroup;
    }

    public void setChangeGroup(String changeGroup) {
        this.changeGroup = changeGroup;
    }
    
    public void setCreateable(boolean createable) {
        this.createable = createable;
    }
    
    @Override
    public boolean isCreatable() {
        return createable;
    }

    @Override
    public String toString() {
        return this.displayName;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof PaoDefinitionImpl == false) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        PaoDefinitionImpl paoDefinition = (PaoDefinitionImpl) obj;
        return new EqualsBuilder().append(type, paoDefinition.getType())
                                  .append(displayName, paoDefinition.getDisplayName())
                                  .append(displayGroup, paoDefinition.getDisplayGroup())
                                  .append(changeGroup, paoDefinition.getChangeGroup())
                                  .append(changeable, paoDefinition.isChangeable())
                                  .append(createable, paoDefinition.isCreatable())
                                  .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(49, 69).append(type)
                                          .append(displayName)
                                          .append(displayGroup)
                                          .append(changeGroup)
                                          .append(changeable)
                                          .append(createable)
                                          .toHashCode();
    }

    @Override
    public int compareTo(PaoDefinition o) {

        if (o == null) {
            return 0;
        }

        return o.getDisplayName().compareTo(displayName);

    }
}
