package com.cannontech.amr.rfn.service.pointmapping.icd;

public class NameScale {
    public NameScale(String name, double multiplier) {
        this.name = name;
        this.multiplier = multiplier;
    }
    String name;
    double multiplier;
    @Override
    public String toString() {
        return name + ":" + multiplier;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        NameScale other = (NameScale) obj;
        if (Double.doubleToLongBits(multiplier) != Double.doubleToLongBits(other.multiplier))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }
    public String getName() {
        return name;
    }
    public double getMultiplier() {
        return multiplier;
    }
}