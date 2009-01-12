package com.cannontech.cc.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name="CCurtProgramType")
public class ProgramType implements Serializable {
    private String name;
    private String strategy;
    private Integer id;
    private Integer energyCompanyId;
    
    @Column(name="CCurtProgramTypeName")
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    @Column(name="CCurtProgramTypeStrategy")
    public String getStrategy() {
        return strategy;
    }
    
    public void setStrategy(String strategy) {
        this.strategy = strategy;
    }

    @Id
    @GeneratedValue(generator="yukon")
    @GenericGenerator(name="yukon", strategy="com.cannontech.database.incrementer.HibernateIncrementer")
    @Column(name="CCurtProgramTypeId")
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

}
