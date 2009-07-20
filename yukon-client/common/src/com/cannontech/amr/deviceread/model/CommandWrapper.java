package com.cannontech.amr.deviceread.model;

import java.util.Set;

import com.cannontech.amr.deviceread.dao.impl.SetCoveringSolver;
import com.cannontech.common.device.definition.model.CommandDefinition;
import com.cannontech.common.device.definition.model.PointIdentifier;

public class CommandWrapper implements SetCoveringSolver.HasWeight<PointIdentifier> {

    private final CommandDefinition commandDefinition;

    public CommandWrapper(CommandDefinition commandDefinition) {
        this.commandDefinition = commandDefinition;
    }
    public Set<PointIdentifier> getAffected() {
        return commandDefinition.getAffectedPointList();
    }

    public float getWeight() {
        // according to Matt, this is a good estimate
        // but, there will be lots of ties
        return commandDefinition.getCommandStringList().size();
    }
    
    public CommandDefinition getCommandDefinition() {
        return commandDefinition;
    }
    
    @Override
    public String toString() {
        return commandDefinition.toString();
    }
    
}
