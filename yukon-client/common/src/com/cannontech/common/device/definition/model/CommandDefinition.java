package com.cannontech.common.device.definition.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class CommandDefinition {

    List<String> commandStringList = new ArrayList<String>();
    Set<DevicePointIdentifier> affectedPointList = new HashSet<DevicePointIdentifier>();

    public Set<DevicePointIdentifier> getAffectedPointList() {
        return affectedPointList;
    }

    public void setAffectedPointList(Set<DevicePointIdentifier> affectedPointList) {
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

    public void addAffectedPoint(DevicePointIdentifier pointReference) {
        this.affectedPointList.add(pointReference);
    }

    public boolean affectsPoint(DevicePointIdentifier pointTemplate) {
        boolean affected = this.affectedPointList.contains(pointTemplate);
        return affected;
    }

    public boolean equals(Object obj) {
        if (obj instanceof CommandDefinition == false) {
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
