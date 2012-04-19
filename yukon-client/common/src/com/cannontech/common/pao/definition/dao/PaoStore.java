package com.cannontech.common.pao.definition.dao;

import java.util.ArrayList;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.cannontech.common.pao.definition.model.PointIdentifier;
import com.cannontech.common.pao.definition.model.jaxb.AttributesType.Attribute;
import com.cannontech.common.pao.definition.model.jaxb.CommandsType.Command;
import com.cannontech.common.pao.definition.model.jaxb.Pao;
import com.cannontech.common.pao.definition.model.jaxb.PointsType.Point;
import com.cannontech.common.pao.definition.model.jaxb.TagType.Tag;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.IterableUtils;
import com.cannontech.database.data.point.PointType;

public class PaoStore {

	// unique per pao
	private String id;
	private boolean enabled = true;
	private boolean creatable = true;
	private List<String> inheritedIds = new ArrayList<String>();
	private boolean isAbstract = false;
	private PaoType paoType = null;
	
	// override-able if not already set by a child
	private String displayName;
	private String displayGroup;
	private String changeGroup;
	private Map<PointIdentifier, Point> points = new HashMap<PointIdentifier, Point>(); // Non-calculated points
	private Map<PointIdentifier, Point> calcPoints = new HashMap<PointIdentifier, Point>(); // calculated points
	private Map<String, Command> commands = new HashMap<String, Command>();
	private Map<String, Attribute> attributes = new HashMap<String, Attribute>();
	private Map<String, Tag> tags = new HashMap<String, Tag>();
	
	public PaoStore(Pao jaxbPao) { 
		this.setId(jaxbPao.getId());
		this.setEnabled(jaxbPao.isEnabled());
		this.setCreatable(jaxbPao.isCreatable());
		this.setAbstract(jaxbPao.isAbstract());
		this.setInheritedIds(jaxbPao.getInherits());
		
		if (!isAbstract()) {
		    PaoType paoType = PaoType.valueOf(getId());
		    this.setPaoType(paoType);
		}
		
		// initial apply of jaxb object
		this.applyDisplayName(jaxbPao.getDisplayName());
		this.applyDisplayGroup(jaxbPao.getDisplayGroup());
		this.applyChangeGroup(jaxbPao.getChangeGroup());
		if (jaxbPao.getPoints() != null) {
			this.applyPoints(IterableUtils.safeList(jaxbPao.getPoints().getPoint()));
		}
		if (jaxbPao.getCommands() != null) {
			this.applyCommands(IterableUtils.safeList(jaxbPao.getCommands().getCommand()));
		}
		if (jaxbPao.getAttributes() != null) {
			this.applyAttributes(IterableUtils.safeList(jaxbPao.getAttributes().getAttribute()));
		}
		if (jaxbPao.getTags() != null) {
			this.applyTags(IterableUtils.safeList(jaxbPao.getTags().getTag()));
		}
	}

	public void mergePaoStore(PaoStore inheritedPao) {

		this.applyDisplayName(inheritedPao.getDisplayName());
		this.applyDisplayGroup(inheritedPao.getDisplayGroup());
		this.applyChangeGroup(inheritedPao.getChangeGroup());
		this.applyPoints(inheritedPao.getEnabledPoints());
		this.applyPoints(inheritedPao.getEnabledCalcPoints());
		this.applyCommands(inheritedPao.getEnabledCommands());
		this.applyAttributes(inheritedPao.getEnabledAttributes());
		this.applyTags(inheritedPao.getTags());
	}
	
	// SETTERS
	// these items are defined at the pao level or are unique per pao and are not overridden by inherited abstract paos
	private void setId(String id) {
		this.id = id;
	}
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	private void setCreatable(boolean creatable) {
        this.creatable = creatable;
    }
	private void setInheritedIds(String inherits) {
		if (inherits == null) {
			return;
		}
		String[] parents = StringUtils.split(StringUtils.remove(inherits, " "), ",");
		this.inheritedIds = Arrays.asList(parents);
	}
	public void setAbstract(boolean isAbstract) {
		this.isAbstract = isAbstract;
	}
	private void setPaoType(PaoType paoType) {
		this.paoType = paoType;
	}
	
	// APPLIERS
	// these items can be set by an inherited pao only if they have not already been set by some child
	private void applyDisplayName(String displayName) {
		if (this.displayName == null) {
			this.displayName = displayName;
		}
	}
	
	private void applyDisplayGroup(String displayGroup) {
		if (this.displayGroup == null) {
			this.displayGroup = displayGroup;
		}
	}
	
	private void applyChangeGroup(String changeGroup) {
		if (this.changeGroup == null) {
			this.changeGroup = changeGroup;
		}
	}
	
	private void applyPoints(List<Point> points) {
		
		for (Point inheritedPoint : points) {
			
			PointIdentifier id = new PointIdentifier(PointType.getForString(inheritedPoint.getType()), inheritedPoint.getOffset());
			Point existingPoint = null;
			
			// Just Calculated Points
			if (inheritedPoint.getCalculation() != null) {
			    existingPoint = this.calcPoints.get(id);
			    
			    if (existingPoint == null) {
			        this.calcPoints.put(id, inheritedPoint);
			        continue;
			    }
			} else {
			    // All Points
			    existingPoint = this.points.get(id);
			    
			    if (existingPoint == null) {
			        this.points.put(id, inheritedPoint);
			        continue;
			    }
			}
				
			// point was marked as disabled by child, leave it be
			if (!existingPoint.isEnabled()) {
				continue;
			} 
				
			// apply items from inherited point if they have not already been set by some child
			if (existingPoint.isInit() == null) {
				existingPoint.setInit(inheritedPoint.isInit());
			}
			if (existingPoint.getName() == null) {
				existingPoint.setName(inheritedPoint.getName());
			}
			if (existingPoint.getDescription() == null) {
				existingPoint.setDescription(inheritedPoint.getDescription());
			}
			
			//If the existing point lacks both stategroup and multiplier, then settings can be taken
			//from inhertited point. Existing point inherits either stategroup or multiplier/etc. from
			//inherited point.
			if(existingPoint.getStategroup() == null && existingPoint.getMultiplier() == null) { //&& inheritedPoint.getMultiplier() != null) {
				if(inheritedPoint.getMultiplier() != null) {
    			    existingPoint.setMultiplier(inheritedPoint.getMultiplier());
    				existingPoint.setUnitofmeasure(inheritedPoint.getUnitofmeasure());
    				existingPoint.setDecimalplaces(inheritedPoint.getDecimalplaces());
    				existingPoint.setAnalogstategroup(inheritedPoint.getAnalogstategroup());
				} else if(inheritedPoint.getStategroup() != null) {
				    existingPoint.setStategroup(inheritedPoint.getStategroup());
				}
			}
		}
	}
	
	private void applyCommands(List<Command> commands) {
		
		for (Command inheritedCommand : commands) {
			
			String id = inheritedCommand.getId();
			Command exisitngCommand = this.commands.get(id);
			if (exisitngCommand == null) {
				this.commands.put(id, inheritedCommand);
			}
		}
	}
	
	private void applyAttributes(List<Attribute> attributes) {
		
		for (Attribute inheritedAttribute : attributes) {
			
			String id = inheritedAttribute.getName();
			Attribute existingAttribute = this.attributes.get(id);
			if (existingAttribute == null) {
				this.attributes.put(id, inheritedAttribute);
			}
		}
	}
	
	private void applyTags(List<Tag> tags) {
		
		for (Tag inheritedTag : tags) {
			
			String id = inheritedTag.getName();
			Tag existingTag = this.tags.get(id);
			if (existingTag == null) {
				this.tags.put(id, inheritedTag);
			}
		}
	}
	
	
	// GETTERS
	public String getId() {
		return id;
	}
	public boolean isEnabled() {
		return enabled;
	}
	public boolean isCreatable() {
        return creatable;
    }
	public List<String> getInheritedIds() {
		return inheritedIds;
	}
	public boolean isAbstract() {
		return isAbstract;
	}
	public PaoType getPaoType() {
		return paoType;
	}
	

	public String getDisplayName() {
		return displayName;
	}
	public String getDisplayGroup() {
		return displayGroup;
	}
	public String getChangeGroup() {
		return changeGroup;
	}
	public List<Point> getEnabledPoints() {
		
		List<Point> enabledPoints = new ArrayList<Point>();
		for (PointIdentifier id : this.points.keySet()) {
			Point point = this.points.get(id);
			if (point.isEnabled()) {
				enabledPoints.add(point);
			}
		}
		return enabledPoints;
	}
	public List<Point> getEnabledCalcPoints() {
	    
	    List<Point> enabledPoints = new ArrayList<Point>();
	    for (PointIdentifier id : this.calcPoints.keySet()) {
	        Point point = this.calcPoints.get(id);
	        if (point.isEnabled()) {
	            enabledPoints.add(point);
	        }
	    }
	    return enabledPoints;
	}
	public List<Command> getEnabledCommands() {

		List<Command> enabledCommands = new ArrayList<Command>();
		for (String id : this.commands.keySet()) {
			Command command = this.commands.get(id);
			if (command.isEnabled()) {
				enabledCommands.add(command);
			}
		}
		return enabledCommands;
	}
	public List<Attribute> getEnabledAttributes() {

		List<Attribute> enabledAttributes = new ArrayList<Attribute>();
		for (String id : this.attributes.keySet()) {
			Attribute attribute = this.attributes.get(id);
			if (attribute.isEnabled()) {
				enabledAttributes.add(attribute);
			}
		}
		return enabledAttributes;
	}
	public List<Tag> getTags() {

	    List<Tag> filteredTags = new ArrayList<Tag>();
	    for (String id : this.tags.keySet()) {
	        Tag tag = this.tags.get(id);
	        filteredTags.add(tag);
	    }
	    return filteredTags;
	}
}
