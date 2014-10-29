package com.cannontech.amr.deviceread.service.impl;

import java.util.Set;

import com.cannontech.amr.deviceread.dao.impl.SetCoveringSolver;
import com.cannontech.common.pao.definition.model.CommandDefinition;
import com.cannontech.common.pao.definition.model.PointIdentifier;

public class CommandWrapper implements SetCoveringSolver.HasWeight<PointIdentifier> {

    private final CommandDefinition commandDefinition;

    public CommandWrapper(CommandDefinition commandDefinition) {
        this.commandDefinition = commandDefinition;
    }
    
    @Override
    public Set<PointIdentifier> getAffected() {
        return commandDefinition.getAffectedPointList();
    }

    @Override
    public float getWeight() {
        return commandDefinition.getAffectedPointList().size();
    }
    
    public CommandDefinition getCommandDefinition() {
        return commandDefinition;
    }
    
    @Override
    public String toString() {
        return commandDefinition.toString();
    }
    
}
