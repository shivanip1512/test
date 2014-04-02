package com.cannontech.cc.model;

import java.io.Serializable;
import java.util.Comparator;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.cannontech.core.dao.support.Identifiable;
import com.cannontech.util.NaturalOrderComparator;

public class Group implements Identifiable, Serializable, Comparable<Group> {
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

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
