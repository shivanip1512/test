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
    private String javaConstant = null;
    private boolean changeable = false;
    private String changeGroup = null;
    private boolean createable = true;

    public PaoDefinitionImpl(PaoType type, String displayName, String displayGroup,
            String javaConstant, String changeGroup, boolean createable) {
        this.type = type;
        this.displayName = displayName;
        this.displayGroup = displayGroup;
        this.javaConstant = javaConstant;
        this.changeGroup = changeGroup;
        this.changeable = changeGroup != null;
        this.createable = createable;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public PaoType getType() {
        return type;
    }

    public void setType(PaoType type) {
        this.type = type;
    }

    public boolean isChangeable() {
        return changeable;
    }

    public void setChangeable(boolean changeable) {
        this.changeable = changeable;
    }

    public String getDisplayGroup() {
        return displayGroup;
    }

    public void setDisplayGroup(String displayGroup) {
        this.displayGroup = displayGroup;
    }

    public String getJavaConstant() {
        return javaConstant;
    }

    public void setJavaConstant(String javaConstant) {
        this.javaConstant = javaConstant;
    }

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

    public String toString() {
        return this.displayName;
    }

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
                                  .append(javaConstant, paoDefinition.getJavaConstant())
                                  .append(changeGroup, paoDefinition.getChangeGroup())
                                  .append(changeable, paoDefinition.isChangeable())
                                  .append(createable, paoDefinition.isCreatable())
                                  .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder(49, 69).append(type)
                                          .append(displayName)
                                          .append(displayGroup)
                                          .append(javaConstant)
                                          .append(changeGroup)
                                          .append(changeable)
                                          .append(createable)
                                          .toHashCode();
    }

    public int compareTo(PaoDefinition o) {

        if (o == null) {
            return 0;
        }

        return o.getDisplayName().compareTo(displayName);

    }
}
