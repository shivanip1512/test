package com.cannontech.cc.model;

import java.io.Serializable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class AvailableProgramGroup implements Serializable {
    private Group group;
    private Program program;
    private boolean defaultGroup;
    private Integer id;
    
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public boolean isDefaultGroup() {
        return defaultGroup;
    }
    
    public void setDefaultGroup(boolean defaultGroup) {
        this.defaultGroup = defaultGroup;
    }
    
    public Group getGroup() {
        return group;
    }
    
    public void setGroup(Group group) {
        this.group = group;
    }
    
    public Program getProgram() {
        return program;
    }
    
    public void setProgram(Program program) {
        this.program = program;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof AvailableProgramGroup == false) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        AvailableProgramGroup rhs = (AvailableProgramGroup) obj;
        return new EqualsBuilder().append(id, rhs.id).isEquals();
    }
    
    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(id).toHashCode();
    }
    
    @Override
    public String toString() {
        return "AvailableProgramGroup [" + id + "]@" + Integer.toHexString(System.identityHashCode(this));
    }

}
