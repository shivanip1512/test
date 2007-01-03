package com.cannontech.common.device.definition.model;

import com.cannontech.common.device.attribute.model.Attribute;

/**
 * Interface which represents a template which can be used to create point
 * instances
 */
public interface PointTemplate extends Comparable<PointTemplate> {

    public abstract Attribute getAttribute();

    public abstract double getMultiplier();

    public abstract String getName();

    public abstract int getOffset();

    public abstract boolean isShouldInitialize();

    public abstract int getStateGroupId();

    public abstract int getUnitOfMeasure();

    public abstract int getType();

}