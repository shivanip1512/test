package com.cannontech.cc.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "CCurtProgramGroup",
       uniqueConstraints=@UniqueConstraint(columnNames={"CCurtGroupId","CCurtProgramId"}))
public class AvailableProgramGroup implements Serializable {
    private Group group;
    private Program program;
    private boolean defaultGroup;
    private Integer id;
    
    @Id
    @GenericGenerator(name="yukon", strategy="com.cannontech.database.incrementer.HibernateIncrementer")
    @GeneratedValue(generator="yukon")
    @Column(name = "CCurtProgramGroupId")
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    @Transient
    public boolean isDefaultGroup() {
        return defaultGroup;
    }
    
    public void setDefaultGroup(boolean defaultGroup) {
        this.defaultGroup = defaultGroup;
    }
    
    @ManyToOne()
    @JoinColumn(name="CCurtGroupId")
    public Group getGroup() {
        return group;
    }
    
    public void setGroup(Group group) {
        this.group = group;
    }
    
    @ManyToOne()
    @JoinColumn(name="CCurtProgramId")
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
