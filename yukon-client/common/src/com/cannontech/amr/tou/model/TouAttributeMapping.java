package com.cannontech.amr.tou.model;

import java.util.HashSet;
import java.util.Set;

import com.cannontech.common.device.attribute.model.Attribute;

public class TouAttributeMapping {
    private int touId;
    private String displayName;
    private Attribute energy;
    private Attribute peak;

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Attribute getPeak() {
        return peak;
    }

    public void setPeak(Attribute peak) {
        this.peak = peak;
    }

    public Attribute getEnergy() {
        return energy;
    }

    public void setEnergy(Attribute energy) {
        this.energy = energy;
    }

    public int getTouId() {
        return touId;
    }

    public void setTouId(int touId) {
        this.touId = touId;
    }

    public Set<Attribute> getAllAttributes() {
        HashSet<Attribute> name = new HashSet<Attribute>();
        name.add(getPeak());
        name.add(getEnergy());
        return name;
    }

}
