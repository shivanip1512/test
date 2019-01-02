package com.cannontech.amr.rfn.service.pointmapping.icd;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BasePointDefinition {

    private Units unit = null;
    private Set<Modifiers> modifiers = new HashSet<>();

    public BasePointDefinition() {
    }

    public BasePointDefinition(Units unit, Set<Modifiers> modifiers) {
        this.unit = unit;
        this.modifiers = modifiers;
    }

    @JsonProperty("Unit")
    public void setUnit(Units unit) {
        this.unit = unit;
    }
    
    public Units getUnit() {
        return unit;
    }

    protected void addModifier(Modifiers modifier) {
        modifiers.add(modifier);
    }
    
    public List<Modifiers> getModifiers() {
        return modifiers.stream().sorted().collect(Collectors.toList());
    }

    public int compareTo(BasePointDefinition other) {
        if (getUnit() == null) {
            return other.getUnit() == null ? 0 : -1;
        }
        if (other == null || other.getUnit() == null) {
            return 1;
        }
        int nameCompare = getUnit().name().compareTo(other.getUnit().name());
        if (nameCompare != 0) {
            return nameCompare;
        }
        if (getModifiers().equals(other.getModifiers())) {
            return 0;
        }
        int modifierComparison = compareModifiers(other);
        
        return modifierComparison;
    }
    private int compareModifiers(BasePointDefinition other) {
        Iterator<Modifiers> i1 = getModifiers().stream().sorted().iterator();
        Iterator<Modifiers> i2 = other.getModifiers().stream().sorted().iterator();
        
        while (i1.hasNext() && i2.hasNext()) {
            int compared = i1.next().compareTo(i2.next());
            if (compared != 0) {
                return compared;
            }
        }
        
        if (i1.hasNext()) {
            return 1;
        }
        if( i2.hasNext()) {
            return -1;
        }
        return 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof BasePointDefinition))
            return false;
        BasePointDefinition other = (BasePointDefinition) obj;
        if (modifiers == null) {
            if (other.modifiers != null)
                return false;
        } else if (!modifiers.equals(other.modifiers))
            return false;
        if (unit != other.unit)
            return false;
        return true;
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getModifiers() == null) ? 0 : getModifiers().hashCode());
        result = prime * result + ((getUnit() == null) ? 0 : getUnit().hashCode());
        return result;
    }
    @Override
    public String toString() {
        String unitString = Optional.ofNullable(getUnit())
                .map(Units::getCommonName)
                .orElse("NULL_UOM");
        if (getModifiers() != null && !getModifiers().isEmpty()) {
            return unitString + getModifiers().stream().sorted().map(Modifiers::getCommonName).collect(Collectors.toList());
        }
        return unitString;
    }
}