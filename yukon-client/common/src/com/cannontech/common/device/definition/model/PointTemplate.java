package com.cannontech.common.device.definition.model;

import com.cannontech.common.device.attribute.model.Attribute;
import com.cannontech.database.data.point.PointUnits;
import com.cannontech.database.db.state.StateGroupUtils;

/**
 * Interface which represents a template which can be used to create point
 * instances
 */
public class PointTemplate extends DevicePointIdentifier implements Comparable<PointTemplate> {

    private String name = null;
    private double multiplier = 1.0;
    private int unitOfMeasure = PointUnits.UOMID_INVALID;
    private int stateGroupId = StateGroupUtils.SYSTEM_STATEGROUPID;
    private boolean shouldInitialize = false;
    private Attribute attribute = null;

    public PointTemplate(int type, int offset) {
        super(type, offset);
    }

    public PointTemplate(String name, int type, int offset, double multiplier,
            int unitOfMeasure, int stateGroupId, boolean shouldInitialize, Attribute attribute) {
        super(type, offset);
        this.name = name;
        this.multiplier = multiplier;
        this.unitOfMeasure = unitOfMeasure;
        this.stateGroupId = stateGroupId;
        this.shouldInitialize = shouldInitialize;
        this.attribute = attribute;
    }

    public Attribute getAttribute() {
        return attribute;
    }

    public void setAttribute(Attribute attribute) {
        this.attribute = attribute;
    }

    public double getMultiplier() {
        return multiplier;
    }

    public void setMultiplier(double multiplier) {
        this.multiplier = multiplier;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isShouldInitialize() {
        return shouldInitialize;
    }

    public void setShouldInitialize(boolean shouldInitialize) {
        this.shouldInitialize = shouldInitialize;
    }

    public int getStateGroupId() {
        return stateGroupId;
    }

    public void setStateGroupId(int stateGroupId) {
        this.stateGroupId = stateGroupId;
    }

    public int getUnitOfMeasure() {
        return unitOfMeasure;
    }

    public void setUnitOfMeasure(int unitOfMeasure) {
        this.unitOfMeasure = unitOfMeasure;
    }

    public int compareTo(PointTemplate o) {

        if (o == null) {
            return 0;
        }

        return o.getName().compareTo(name);

    }


}