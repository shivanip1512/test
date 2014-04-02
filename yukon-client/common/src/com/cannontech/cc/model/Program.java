package com.cannontech.cc.model;

import java.io.Serializable;
import java.util.Comparator;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.cannontech.core.dao.support.Identifiable;
import com.cannontech.util.NaturalOrderComparator;

public class Program implements Identifiable, Serializable, Comparable<Program> {
    private String name = "";
    private ProgramType programType;
    private Integer id;
    private Integer lastIdentifier = 0;
    private String identifierPrefix = "";
    static private Comparator<String> comparator = new NaturalOrderComparator();
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public ProgramType getProgramType() {
        return programType;
    }
    
    public void setProgramType(ProgramType programType) {
        this.programType = programType;
    }

    public String getIdentifierPrefix() {
        return identifierPrefix;
    }

    public void setIdentifierPrefix(String identifierPrefix) {
        this.identifierPrefix = identifierPrefix;
    }

    public Integer getLastIdentifier() {
        return lastIdentifier;
    }

    public void setLastIdentifier(Integer lastIdentifier) {
        this.lastIdentifier = lastIdentifier;
    }

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Program == false) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        Program rhs = (Program) obj;
        return new EqualsBuilder().append(id, rhs.id).isEquals();
    }
    
    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(id).toHashCode();
    }
    
    @Override
    public String toString() {
        return "Program [" + id + "]@" + Integer.toHexString(System.identityHashCode(this));
    }

    public int compareTo(Program o) {
        return comparator.compare(name, o.name);
    }
}
