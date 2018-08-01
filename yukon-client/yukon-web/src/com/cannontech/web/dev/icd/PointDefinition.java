package com.cannontech.web.dev.icd;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PointDefinition {
    public Units unit;
    @JsonProperty("Unit Modifiers")
    public Set<Modifiers> modifiers = new HashSet<>();
    public Units getUnit() {
        return unit;
    }
    public List<Modifiers> getModifiers() {
        return modifiers.stream().sorted().collect(Collectors.toList());
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((modifiers == null) ? 0 : modifiers.hashCode());
        result = prime * result + ((unit == null) ? 0 : unit.hashCode());
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        PointDefinition other = (PointDefinition) obj;
        return Objects.equals(unit, other.unit) 
                && Objects.equals(modifiers, other.modifiers);
    }
    @Override
    public String toString() {
        String unitString = Optional.ofNullable(unit)
                .map(Object::toString)
                .orElse("NULL_UOM");
        if (modifiers != null && !modifiers.isEmpty()) {
            return unitString + modifiers.stream().sorted().collect(Collectors.toList());
        }
        return unitString;
    }
    public int compareTo(PointDefinition other) {
        if (unit == null) {
            return other.unit == null ? 0 : -1;
        }
        if (other == null || other.unit == null) {
            return 1;
        }
        int nameCompare = unit.name().compareTo(other.unit.name());
        if (nameCompare != 0) {
            return nameCompare;
        }
        if (modifiers.equals(other.modifiers)) {
            return 0;
        }
        int modifierComparison = compareModifiers(other);
        
        return modifierComparison;
    }
    private int compareModifiers(PointDefinition other) {
        Iterator<Modifiers> i1 = modifiers.stream().sorted().iterator();
        Iterator<Modifiers> i2 = other.modifiers.stream().sorted().iterator();
        
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
}