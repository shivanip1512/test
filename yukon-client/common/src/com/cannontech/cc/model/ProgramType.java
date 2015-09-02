package com.cannontech.cc.model;

import java.io.Serializable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.cannontech.core.dao.support.Identifiable;

public class ProgramType implements Identifiable, Serializable, Comparable<ProgramType> {
    private String name;
    private String strategy;
    private Integer id;
    private Integer energyCompanyId;
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getStrategy() {
        return strategy;
    }
    
    public void setStrategy(String strategy) {
        this.strategy = strategy;
    }

    @Override
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public Integer getEnergyCompanyId() {
        return energyCompanyId;
    }
    
    public void setEnergyCompanyId(Integer energyCompanyId) {
        this.energyCompanyId = energyCompanyId;
    }
    
    @Override
    public String toString() {
        return "ProgramType [" + id + "]@" + Integer.toHexString(System.identityHashCode(this));
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ProgramType == false) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        ProgramType rhs = (ProgramType) obj;
        return new EqualsBuilder().append(id, rhs.id).isEquals();
    }
    
    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(id).toHashCode();
    }

    @Override
    public int compareTo(ProgramType other) {
        return name.compareTo(other.name);
    }

}
