package com.cannontech.common.device.definition.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.cannontech.common.device.definition.model.DevicePointIdentifier;
import com.cannontech.common.device.definition.model.castor.Attribute;
import com.cannontech.common.device.definition.model.castor.Command;
import com.cannontech.common.device.definition.model.castor.Device;
import com.cannontech.common.device.definition.model.castor.Feature;
import com.cannontech.common.device.definition.model.castor.Point;
import com.cannontech.database.data.point.PointTypes;

public class DeviceStore {

	// unique per device
	private String id;
	private boolean enabled = true;
	private List<String> inheritedIds = new ArrayList<String>();
	private boolean isAbstract;
	private Integer deviceType;
	
	
	// override-able if not already set by a child
	private String displayName;
	private String displayGroup;
	private String changeGroup;
	private Map<DevicePointIdentifier, Point> points = new HashMap<DevicePointIdentifier, Point>();
	private Map<String, Command> commands = new HashMap<String, Command>();
	private Map<String, Attribute> attributes = new HashMap<String, Attribute>();
	private Map<String, Feature> features = new HashMap<String, Feature>();
	
	public DeviceStore(Device castor) {
		
		// set 
		this.setId(castor.getId());
		this.setEnabled(castor.getEnabled());
		this.setAbstract(castor.getAbstract());
		this.setInheritedIds(castor.getInherits());
		this.setDeviceType(castor.getType());
		
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
		if (castor.getFeatures() != null) {
			this.applyFeatures(castor.getFeatures().getFeature());
		}
	}
	
	public void mergeDeviceStore(DeviceStore inheritedDevice) {

		this.applyDisplayName(inheritedDevice.getDisplayName());
		this.applyDisplayGroup(inheritedDevice.getDisplayGroup());
		this.applyChangeGroup(inheritedDevice.getChangeGroup());
		this.applyPoints(inheritedDevice.getEnabledPoints());
		this.applyCommands(inheritedDevice.getEnabledCommands());
		this.applyAttributes(inheritedDevice.getEnabledAttributes());
		this.applyFeatures(inheritedDevice.getEnabledFeatures());
	}
	
	
	// SETTERS
	// these items are defined at the device level or are unique per device and are not overridden by inherited abstract devices
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
	private void setDeviceType(Integer deviceType) {
		this.deviceType = deviceType;
	}
	
	// APPLIERS
	// these items can be set by an inherited device only if they have not already been set by some child
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
			
			DevicePointIdentifier id = new DevicePointIdentifier(inheritedPoint.getOffset(), PointTypes.getType(inheritedPoint.getType()));
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
	
	private void applyFeatures(Feature[] features) {
		
		if (features == null) {
			return;
		}
		
		for (Feature inheritedFeature : features) {
			
			String id = inheritedFeature.getName();
			Feature existingFeature = this.features.get(id);
			if (existingFeature == null) {
				this.features.put(id, inheritedFeature);
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
	public int getDeviceType() {
		return deviceType;
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
		for (DevicePointIdentifier id : this.points.keySet()) {
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
	public Feature[] getEnabledFeatures() {

		List<Feature> enabledFeatures = new ArrayList<Feature>();
		for (String id : this.features.keySet()) {
			Feature feature = this.features.get(id);
			if (feature.getEnabled()) {
				enabledFeatures.add(feature);
			}
		}
		return enabledFeatures.toArray(new Feature[]{});
	}
}
