package com.cannontech.cc.model;

import java.io.Serializable;
import java.util.Comparator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.GenericGenerator;

import com.cannontech.util.NaturalOrderComparator;

@Entity
@Table(name="CCurtProgram", 
       uniqueConstraints=@UniqueConstraint(columnNames={"CCurtProgramName","CCurtProgramTypeId"}))

public class Program implements Serializable, Comparable<Program> {
    private String name = "";
    private ProgramType programType;
    private Integer id;
    private Integer lastIdentifier;
    private String identifierPrefix;
    static private Comparator<String> comparator = new NaturalOrderComparator();
    
    @Column(name="CCurtProgramName", nullable=false)
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    @ManyToOne()
    @JoinColumn(name="CCurtProgramTypeId")
    public ProgramType getProgramType() {
        return programType;
    }
    
    public void setProgramType(ProgramType programType) {
        this.programType = programType;
    }

    @Column(nullable=false)
    public String getIdentifierPrefix() {
        return identifierPrefix;
    }

    public void setIdentifierPrefix(String identifierPrefix) {
        this.identifierPrefix = identifierPrefix;
    }

    @Column(nullable=false)
    public Integer getLastIdentifier() {
        return lastIdentifier;
    }

    public void setLastIdentifier(Integer lastIdentifier) {
        this.lastIdentifier = lastIdentifier;
    }

    @Id
    @GenericGenerator(name="yukon", strategy="com.cannontech.database.incrementer.HibernateIncrementer")
    @GeneratedValue(generator="yukon")
    @Column(name="CCurtProgramId")
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
