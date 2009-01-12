package com.cannontech.cc.model;

import java.io.Serializable;
import java.util.Comparator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.GenericGenerator;

import com.cannontech.util.NaturalOrderComparator;

@Entity
@Table(name = "CCurtGroup",
       uniqueConstraints=@UniqueConstraint(columnNames={"energyCompanyId","CCurtGroupName"}))
public class Group implements Serializable, Comparable<Group> {
    private String name;
    private Integer id;
    private Integer energyCompanyId;
    static private Comparator<String> comparator = new NaturalOrderComparator();

    public Integer getEnergyCompanyId() {
        return energyCompanyId;
    }

    public void setEnergyCompanyId(Integer energyCompanyId) {
        this.energyCompanyId = energyCompanyId;
    }

    @Column(nullable = false, name = "CCurtGroupName")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Id
    @GenericGenerator(name="yukon", strategy="com.cannontech.database.incrementer.HibernateIncrementer")
    @GeneratedValue(generator="yukon")
    @Column(name = "CCurtGroupId")
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Group == false) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        Group rhs = (Group) obj;
        return new EqualsBuilder().append(id, rhs.id).isEquals();
    }
    
    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(id).toHashCode();
    }
    @Override
    public String toString() {
        return "Group [" + id + "]@" + Integer.toHexString(System.identityHashCode(this));
    }

    public int compareTo(Group o) {
        return comparator.compare(name, o.name);
    }
    

}
