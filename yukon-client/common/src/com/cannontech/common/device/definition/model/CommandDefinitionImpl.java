package com.cannontech.common.device.definition.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class CommandDefinitionImpl implements CommandDefinition {

    List<String> commandStringList = new ArrayList<String>();
    List<PointReference> affectedPointList = new ArrayList<PointReference>();

    public List<PointReference> getAffectedPointList() {
        return affectedPointList;
    }

    public void setAffectedPointList(List<PointReference> affectedPointList) {
        this.affectedPointList = affectedPointList;
    }

    public List<String> getCommandStringList() {
        return commandStringList;
    }

    public void setCommandStringList(List<String> commandStringList) {
        this.commandStringList = commandStringList;
    }

    public void addCommandString(String commandString) {
        this.commandStringList.add(commandString);
    }

    public void addAffectedPoint(PointReference pointReference) {
        this.affectedPointList.add(pointReference);
    }

    public boolean affectsPoint(PointTemplate pointTemplate) {

        for (PointReference pointRef : affectedPointList) {
            if (pointRef.getPointName().equals(pointTemplate.getName())) {
                return true;
            }
        }
        return false;
    }

    public boolean equals(Object obj) {
        if (obj instanceof CommandDefinitionImpl == false) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        CommandDefinition commandDefinition = (CommandDefinition) obj;
        return new EqualsBuilder().append(commandStringList,
                                          commandDefinition.getCommandStringList())
                                  .append(affectedPointList,
                                          commandDefinition.getAffectedPointList())
                                  .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder(89, 99).append(commandStringList)
                                          .append(affectedPointList)
                                          .toHashCode();
    }

}
