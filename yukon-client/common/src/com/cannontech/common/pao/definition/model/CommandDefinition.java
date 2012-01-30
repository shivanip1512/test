package com.cannontech.common.pao.definition.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.springframework.core.style.ToStringCreator;

public class CommandDefinition implements Comparable<CommandDefinition> {

	private String name = null;
	private List<String> commandStringList = new ArrayList<String>();
	private Set<PointIdentifier> affectedPointList = new HashSet<PointIdentifier>();

    public CommandDefinition(String name) {
		super();
		this.name = name;
	}

	public Set<PointIdentifier> getAffectedPointList() {
        return affectedPointList;
    }

    public void setAffectedPointList(Set<PointIdentifier> affectedPointList) {
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

    public void addAffectedPoint(PointIdentifier pointReference) {
        this.affectedPointList.add(pointReference);
    }

    public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean affectsPoint(PointIdentifier pointTemplate) {

		for(PointIdentifier identifier: affectedPointList) {
			if( identifier.isComparableTo(pointTemplate))
				return true;
		}
		return false;
    }
	
	public int compareTo(CommandDefinition o) {

        if (o == null) {
            return 0;
        }
        return getName().compareTo(o.getName());
    }

    public boolean equals(Object obj) {
        if (obj instanceof CommandDefinition == false) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        CommandDefinition commandDefinition = (CommandDefinition) obj;
        return new EqualsBuilder().append(name, commandDefinition.getName())
        						  .append(commandStringList,
                                          commandDefinition.getCommandStringList())
                                  .append(affectedPointList,
                                          commandDefinition.getAffectedPointList())
                                  .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder(89, 99).append(name)
        								  .append(commandStringList)
                                          .append(affectedPointList)
                                          .toHashCode();
    }
    
    @Override
    public String toString() {
        ToStringCreator tsc = new ToStringCreator(this);
        tsc.append("name", getName());
        tsc.append("commandStringList", getCommandStringList());
        tsc.append("affectedPointList", getAffectedPointList());
        return tsc.toString();
    }
}
