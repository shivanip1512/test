package com.cannontech.common.pao.definition.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.cannontech.common.pao.definition.model.PointIdentifier;
import com.cannontech.common.pao.definition.model.castor.Attribute;
import com.cannontech.common.pao.definition.model.castor.Command;
import com.cannontech.common.pao.definition.model.castor.Pao;
import com.cannontech.common.pao.definition.model.castor.Point;
import com.cannontech.common.pao.definition.model.castor.Tag;
import com.cannontech.common.pao.PaoType;
import com.cannontech.database.data.point.PointTypes;

public class PaoStore {

	// unique per pao
	private String id;
	private boolean enabled = true;
	private List<String> inheritedIds = new ArrayList<String>();
	private boolean isAbstract = false;
	private PaoType paoType = null;
	
	
	// override-able if not already set by a child
	private String displayName;
	private String displayGroup;
	private String changeGroup;
	private Map<PointIdentifier, Point> points = new HashMap<PointIdentifier, Point>();
	private Map<String, Command> commands = new HashMap<String, Command>();
	private Map<String, Attribute> attributes = new HashMap<String, Attribute>();
	private Map<String, Tag> tags = new HashMap<String, Tag>();
	
	public PaoStore(Pao castor) {
		
		// set 
		this.setId(castor.getId());
		this.setEnabled(castor.getEnabled());
		this.setAbstract(castor.getAbstract());
		this.setInheritedIds(castor.getInherits());
		
		if (!isAbstract()) {
		    PaoType paoType = PaoType.valueOf(getId());
		    this.setPaoType(paoType);
		}
		
		// initial apply of castor object
		this.applyDisplayName(castor.getDisplayName());
		this.applyDisplayGroup(castor.getDisplayGroup());
		this.applyChangeGroup(castor.getChangeGroup());
		if (castor.getPoints() != null) {
			this.applyPoints(castor.getPoints().getPoint());
		}
		if (castor.getCommands() != null) {
			this.applyCommands(castor.getCommands().getCommand());
		}
		if (castor.getAttributes() != null) {
			this.applyAttributes(castor.getAttributes().getAttribute());
		}
		if (castor.getTags() != null) {
			this.applyTags(castor.getTags().getTag());
		}
	}
	
	public void mergePaoStore(PaoStore inheritedPao) {

		this.applyDisplayName(inheritedPao.getDisplayName());
		this.applyDisplayGroup(inheritedPao.getDisplayGroup());
		this.applyChangeGroup(inheritedPao.getChangeGroup());
		this.applyPoints(inheritedPao.getEnabledPoints());
		this.applyCommands(inheritedPao.getEnabledCommands());
		this.applyAttributes(inheritedPao.getEnabledAttributes());
		this.applyTags(inheritedPao.getEnabledTags());
	}
	
	
	// SETTERS
	// these items are defined at the pao level or are unique per pao and are not overridden by inherited abstract paos
	private void setId(String id) {
		this.id = id;
	}
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
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
	
	private void applyPoints(Point[] points) {
		
		if (points == null) {
			return;
		}
		
		for (Point inheritedPoint : points) {
			
			PointIdentifier id = new PointIdentifier(PointTypes.getType(inheritedPoint.getType()), inheritedPoint.getOffset());
			Point exisitngPoint = this.points.get(id);
			
			if (exisitngPoint == null) {
				this.points.put(id, inheritedPoint);
				continue;
			}
				
			// point was marked as disabled by child, leave it be
			if (!exisitngPoint.getEnabled()) {
				continue;
			} 
				
			// apply items from inherited point if they have not already been set by some child
			if (exisitngPoint.getInit() == null) {
				exisitngPoint.setInit(inheritedPoint.getInit());
			}
			if (exisitngPoint.getName() == null) {
				exisitngPoint.setName(inheritedPoint.getName());
			}
			if (exisitngPoint.getDescription() == null) {
				exisitngPoint.setDescription(inheritedPoint.getDescription());
			}
			
			// existing point does not have state group set, and the inherited point has a multipier/uom to offer us...
			// if we already have a state group then we are not interested in the inherited point's multiplier/uom.
			if(exisitngPoint.getPointChoice().getStategroup() == null && inheritedPoint.getPointChoice().getPointChoiceSequence() != null) {
				
				// existing point doesn't have any multiplier/uom, take all that the inherited point has to give us
				if (exisitngPoint.getPointChoice().getPointChoiceSequence() == null) {
					exisitngPoint.getPointChoice().setPointChoiceSequence(inheritedPoint.getPointChoice().getPointChoiceSequence());
				}
			}

			// existing point does not have multiplier/uom set, and the inherited point has state group to offer us...
			// if we already have a multiplier/uom then we are not interested in the state group that the inherited point has to offer.
			if (exisitngPoint.getPointChoice().getPointChoiceSequence() == null && inheritedPoint.getPointChoice().getStategroup() != null) {
				
				if (exisitngPoint.getPointChoice().getStategroup() == null) {
					exisitngPoint.getPointChoice().setStategroup(inheritedPoint.getPointChoice().getStategroup());
				}
			}
		}
	}
	
	private void applyCommands(Command[] commands) {
		
		if (commands == null) {
			return;
		}
		
		for (Command inheritedCommand : commands) {
			
			String id = inheritedCommand.getId();
			Command exisitngCommand = this.commands.get(id);
			if (exisitngCommand == null) {
				this.commands.put(id, inheritedCommand);
			}
		}
	}
	
	private void applyAttributes(Attribute[] attributes) {
		
		if (attributes == null) {
			return;
		}
		
		for (Attribute inheritedAttribute : attributes) {
			
			String id = inheritedAttribute.getName();
			Attribute existingAttribute = this.attributes.get(id);
			if (existingAttribute == null) {
				this.attributes.put(id, inheritedAttribute);
			}
		}
	}
	
	private void applyTags(Tag[] tags) {
		
		if (tags == null) {
			return;
		}
		
		for (Tag inheritedTag : tags) {
			
			String id = inheritedTag.getName();
			Tag existingFeature = this.tags.get(id);
			if (existingFeature == null) {
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
	public Point[] getEnabledPoints() {
		
		List<Point> enabledPoints = new ArrayList<Point>();
		for (PointIdentifier id : this.points.keySet()) {
			Point point = this.points.get(id);
			if (point.getEnabled()) {
				enabledPoints.add(point);
			}
		}
		return enabledPoints.toArray(new Point[]{});
	}
	public Command[] getEnabledCommands() {

		List<Command> enabledCommands = new ArrayList<Command>();
		for (String id : this.commands.keySet()) {
			Command command = this.commands.get(id);
			if (command.getEnabled()) {
				enabledCommands.add(command);
			}
		}
		return enabledCommands.toArray(new Command[]{});
	}
	public Attribute[] getEnabledAttributes() {

		List<Attribute> enabledAttributes = new ArrayList<Attribute>();
		for (String id : this.attributes.keySet()) {
			Attribute attribute = this.attributes.get(id);
			if (attribute.getEnabled()) {
				enabledAttributes.add(attribute);
			}
		}
		return enabledAttributes.toArray(new Attribute[]{});
	}
	public Tag[] getEnabledTags() {

		List<Tag> enabledTags = new ArrayList<Tag>();
		for (String id : this.tags.keySet()) {
			Tag tag = this.tags.get(id);
			if (tag.getEnabled()) {
				enabledTags.add(tag);
			}
		}
		return enabledTags.toArray(new Tag[]{});
	}
}
