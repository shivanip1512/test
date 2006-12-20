package com.cannontech.common.device.definition.model;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.cannontech.common.device.attribute.Attribute;
import com.cannontech.database.data.point.PointUnits;
import com.cannontech.database.db.state.StateGroupUtils;

/**
 * Class which represents a template which can be used to create point instances
 */
public class PointTemplate implements Comparable<PointTemplate> {

    private String name = null;
    private int type = -1;
    private int offset = -1;
    private double multiplier = 1.0;
    private int unitOfMeasure = PointUnits.UOMID_INVALID;
    private int stateGroupId = StateGroupUtils.SYSTEM_STATEGROUPID;
    private boolean shouldInitialize = false;
    private Attribute attribute = null;

    public PointTemplate() {
    }

    public PointTemplate(String name, int type, int offset, double multiplier, int unitOfMeasure,
            int stateGroupId, boolean shouldInitialize, Attribute attribute) {
        this.name = name;
        this.type = type;
        this.offset = offset;
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

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String toString() {
        return ("\t" + type + ", name: " + name + ", offset: " + offset + ", multiplier: "
                + multiplier + ", unitOfMeasure: " + unitOfMeasure + ", stateGroupId: "
                + stateGroupId + ", init: " + shouldInitialize + ((attribute != null)
                ? ", attribute: " + attribute.getKey() : ""));
    }

    public boolean equals(Object obj) {
        if (obj instanceof PointTemplate == false) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        PointTemplate pointTemplate = (PointTemplate) obj;
        return new EqualsBuilder().append(name, pointTemplate.getName())
                                  .append(type, pointTemplate.getType())
                                  .append(offset, pointTemplate.getOffset())
                                  .append(multiplier, pointTemplate.getMultiplier())
                                  .append(unitOfMeasure, pointTemplate.getUnitOfMeasure())
                                  .append(stateGroupId, pointTemplate.getStateGroupId())
                                  .append(shouldInitialize, pointTemplate.isShouldInitialize())
                                  .append(attribute, pointTemplate.getAttribute())
                                  .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder(19, 39).append(name)
                                          .append(type)
                                          .append(offset)
                                          .append(multiplier)
                                          .append(unitOfMeasure)
                                          .append(stateGroupId)
                                          .append(shouldInitialize)
                                          .append(attribute)
                                          .toHashCode();
    }

    public int compareTo(PointTemplate o) {

        if (o == null) {
            return 0;
        }

        return o.getName().compareTo(name);

    }
}
