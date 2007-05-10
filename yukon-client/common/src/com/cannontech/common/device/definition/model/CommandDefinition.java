package com.cannontech.common.device.definition.model;

import java.util.List;

public interface CommandDefinition {

    public abstract List<PointReference> getAffectedPointList();

    public abstract List<String> getCommandStringList();

    public abstract void addCommandString(String commandString);

    public abstract void addAffectedPoint(PointReference pointReference);

    /**
     * Method used to determine whether this command affects the given point
     * @param pointTemplate - Point in question
     * @return True if point is affected by this command
     */
    public abstract boolean affectsPoint(PointTemplate pointTemplate);

}