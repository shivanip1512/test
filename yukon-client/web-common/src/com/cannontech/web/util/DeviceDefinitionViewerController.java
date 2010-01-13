package com.cannontech.web.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import com.cannontech.common.device.attribute.model.Attribute;
import com.cannontech.common.device.definition.attribute.lookup.AttributeDefinition;
import com.cannontech.common.device.definition.attribute.lookup.BasicAttributeDefinition;
import com.cannontech.common.device.definition.dao.DeviceDefinitionDao;
import com.cannontech.common.device.definition.model.CommandDefinition;
import com.cannontech.common.device.definition.model.DeviceDefinition;
import com.cannontech.common.device.definition.model.DeviceTag;
import com.cannontech.common.device.definition.model.PointIdentifier;
import com.cannontech.common.device.definition.model.PointTemplate;
import com.cannontech.common.pao.PaoType;
import com.cannontech.core.dao.StateDao;
import com.cannontech.core.dao.UnitMeasureDao;
import com.cannontech.database.data.lite.LiteStateGroup;
import com.cannontech.database.data.point.PointType;

public class DeviceDefinitionViewerController extends AbstractController {

	private DeviceDefinitionDao deviceDefinitionDao;
	private UnitMeasureDao unitMeasureDao;
	private StateDao stateDao;
	
	private static String[] DISPLAY_GROUP_ORDER = {"MCT", "Two Way LCR", "Signal Transmitters", "Electronic Meters", "RTU", "Virtual", "Grid Advisor", ""};
	
	@Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {

        ModelAndView mav = new ModelAndView("deviceDefinition/deviceDefinitionViewer.jsp");
        
        // init
        Set<DeviceDefinition> allDefinitions = deviceDefinitionDao.getAllDeviceDefinitions();
        Map<String, Set<DeviceDefinition>> allDeviceTypes = new LinkedHashMap<String, Set<DeviceDefinition>>();
        Set<DeviceDefinition> displayDefinitions = new LinkedHashSet<DeviceDefinition>();
        
        Set<String> allDisplayGroups = new LinkedHashSet<String>();
        Set<String> allChangeGroups = new HashSet<String>();
        Set<Attribute> allAttributes = new HashSet<Attribute>();
        Set<DeviceTag> allTags = new HashSet<DeviceTag>();
        
        // parameters
        String deviceTypeParam = ServletRequestUtils.getStringParameter(request, "deviceType");
        String displayGroupParam = ServletRequestUtils.getStringParameter(request, "displayGroup");
        String changeGroupParam = ServletRequestUtils.getStringParameter(request, "changeGroup");
        String attributeParam = ServletRequestUtils.getStringParameter(request, "attribute");
        String tagParam = ServletRequestUtils.getStringParameter(request, "tag");
        
        // all
        for (DeviceDefinition deviceDefiniton : allDefinitions) {
        	
        	// allDeviceTypes
        	String displayGroup = deviceDefiniton.getDisplayGroup();
        	if (displayGroup == null) {
        		displayGroup = "Untitled";
        	}
        	if (!allDeviceTypes.containsKey(displayGroup)) {
        		allDeviceTypes.put(displayGroup, new LinkedHashSet<DeviceDefinition>());
        	}
        	allDeviceTypes.get(displayGroup).add(deviceDefiniton);
        	
        	// allDisplayGroups
        	if (!allDisplayGroups.contains(displayGroup)) {
        		allDisplayGroups.add(displayGroup);
        	}
        	
        	// allChangeGroups
        	String changeGroup = deviceDefiniton.getChangeGroup();
        	if (!StringUtils.isBlank(changeGroup) && !allChangeGroups.contains(changeGroup)) {
        		allChangeGroups.add(changeGroup);
        	}
        	
        	// allAttributes
        	List<AttributeDefinition> definitionAttributes = new ArrayList<AttributeDefinition>(deviceDefinitionDao.getDefinedAttributes(deviceDefiniton.getType()));
        	for (AttributeDefinition attribute : definitionAttributes) {
        		if (!allAttributes.contains(attribute)) {
        			allAttributes.add(attribute.getAttribute());
        		}
        	}
        	
        	// allTags
        	List<DeviceTag> definitionTags = new ArrayList<DeviceTag>(deviceDefinitionDao.getSupportedTags(deviceDefiniton));
        	for (DeviceTag tag : definitionTags) {
        		if (!allTags.contains(tag)) {
        			allTags.add(tag);
        		}
        	}
        	
        	// add to displayDefinitions
        	if (!StringUtils.isBlank(deviceTypeParam)) {
        		if (PaoType.valueOf(deviceTypeParam) == deviceDefiniton.getType()) {
        			displayDefinitions.add(deviceDefiniton);
        		}
        	} else if (!StringUtils.isBlank(displayGroupParam)) {
    			if (displayGroupParam.equals(deviceDefiniton.getDisplayGroup() == null ? "Untitled" : deviceDefiniton.getDisplayGroup())) {
    				displayDefinitions.add(deviceDefiniton);
    			}
        	} else if (!StringUtils.isBlank(changeGroupParam)) {
    			if (changeGroupParam.equals(deviceDefiniton.getChangeGroup())) {
    				displayDefinitions.add(deviceDefiniton);
    			}
        	} else if (!StringUtils.isBlank(attributeParam)) {
        		for (AttributeDefinition attribute : definitionAttributes) {
        			if (attribute.getAttribute().getKey().equals(attributeParam)) {
        				displayDefinitions.add(deviceDefiniton);
        			}
        		}
        	} else if (!StringUtils.isBlank(tagParam)) {
        		for (DeviceTag tag : definitionTags) {
        			if (tagParam.equals(tag.name())) {
        				displayDefinitions.add(deviceDefiniton);
        			}
        		}
        	} else {
        		displayDefinitions.add(deviceDefiniton);
        	}
        }
        
        // display definitions info
        Map<String, Set<DeviceInfo>> displayDefinitionsMap = new HashMap<String, Set<DeviceInfo>>();
        for (DeviceDefinition deviceDefiniton : displayDefinitions) {
        	String displayGroup = deviceDefiniton.getDisplayGroup();
        	DeviceInfo deviceInfo = new DeviceInfo(deviceDefiniton);
        	if (!displayDefinitionsMap.containsKey(displayGroup)) {
        		displayDefinitionsMap.put(displayGroup, new LinkedHashSet<DeviceInfo>());
        	}
        	displayDefinitionsMap.get(displayGroup).add(deviceInfo);
        }
        
        // sort
        allDeviceTypes = sortDeviceTypesByGroupOrder(allDeviceTypes);
        displayDefinitionsMap = sortDisplayDefinitionsByGroupOrder(displayDefinitionsMap);
        allDisplayGroups = sortDisplayGroupsByDisplayGroupOrder(allDisplayGroups);
        
        mav.addObject("deviceTypeParam", deviceTypeParam);
        mav.addObject("displayGroupParam", displayGroupParam);
        mav.addObject("changeGroupParam", changeGroupParam);
        mav.addObject("attributeParam", attributeParam);
        mav.addObject("tagParam", tagParam);
        
        mav.addObject("allDeviceTypes", allDeviceTypes);
        mav.addObject("allDisplayGroups", allDisplayGroups);
        mav.addObject("allChangeGroups", allChangeGroups);
        mav.addObject("allAttributes", allAttributes);
        mav.addObject("allTags", allTags);
        
        mav.addObject("displayDefinitionsMap", displayDefinitionsMap);
        
        return mav;
    }
	
	private Map<String, Set<DeviceDefinition>> sortDeviceTypesByGroupOrder(Map<String, Set<DeviceDefinition>> allDeviceTypes) {
		Map<String, Set<DeviceDefinition>> sortedAllDeviceTypes = new LinkedHashMap<String, Set<DeviceDefinition>>();
		for (String displayGroup : DISPLAY_GROUP_ORDER) {
			Set<DeviceDefinition> deviceSet = allDeviceTypes.remove(displayGroup);
			if (deviceSet != null) {
				sortedAllDeviceTypes.put(displayGroup, deviceSet);
			}
		}
		sortedAllDeviceTypes.putAll(allDeviceTypes);
		return sortedAllDeviceTypes;
	}
	
	private Map<String, Set<DeviceInfo>> sortDisplayDefinitionsByGroupOrder(Map<String, Set<DeviceInfo>> displayDefinitionsMap) {
		Map<String, Set<DeviceInfo>> sortedDisplayDefinitionsMap = new LinkedHashMap<String, Set<DeviceInfo>>();
		for (String displayGroup : DISPLAY_GROUP_ORDER) {
			Set<DeviceInfo> deviceSet = displayDefinitionsMap.remove(displayGroup);
			if (deviceSet != null) {
				sortedDisplayDefinitionsMap.put(displayGroup, deviceSet);
			}
		}
		sortedDisplayDefinitionsMap.putAll(displayDefinitionsMap);
		return sortedDisplayDefinitionsMap;
	}
	
	private Set<String> sortDisplayGroupsByDisplayGroupOrder(Set<String> allDisplayGroups) {
		Set<String> sortedAllDisplayGroups = new LinkedHashSet<String>();
		for (String displayGroup : DISPLAY_GROUP_ORDER) {
			if (allDisplayGroups.contains(displayGroup)) {
				sortedAllDisplayGroups.add(displayGroup);
				allDisplayGroups.remove(displayGroup);
			}
		}
		sortedAllDisplayGroups.addAll(allDisplayGroups);
		return sortedAllDisplayGroups;
	}
	
	public class DeviceInfo {
		
		private DeviceDefinition definition;
		private List<PointTemplateWrapper> points;
		private List<AttributeWrapper> attributes;
		private List<CommandDefinitionWrapper> commands;
		private List<DeviceTag> tags;
		
		public DeviceInfo(DeviceDefinition deviceDefiniton) {
			
			this.definition = deviceDefiniton;
			
			// points
			List<PointTemplate> pointTemplates = new ArrayList<PointTemplate>(deviceDefinitionDao.getAllPointTemplates(deviceDefiniton));
			Collections.sort(pointTemplates);
			
			this.points = new ArrayList<PointTemplateWrapper>();
			for (PointTemplate pointTemplate : pointTemplates) {
				this.points.add(new PointTemplateWrapper(pointTemplate));
			}
			
			// attributes
			List<AttributeDefinition> attributes = new ArrayList<AttributeDefinition>(deviceDefinitionDao.getDefinedAttributes(deviceDefiniton.getType()));
			this.attributes = new ArrayList<AttributeWrapper>();
			for (AttributeDefinition attribute : attributes) {
				this.attributes.add(new AttributeWrapper(attribute));
			}
			
			// commands
			List<CommandDefinition> commands = new ArrayList<CommandDefinition>(deviceDefinitionDao.getAvailableCommands(deviceDefiniton));
			Collections.sort(commands);
			this.commands = new ArrayList<CommandDefinitionWrapper>();
			for (CommandDefinition commandDefinition : commands) {
				this.commands.add(new CommandDefinitionWrapper(commandDefinition, deviceDefiniton));
			}
			
			// tags
			this.tags = new ArrayList<DeviceTag>(deviceDefinitionDao.getSupportedTags(deviceDefiniton));
		}

		public DeviceDefinition getDefinition() {
			return definition;
		}
		public List<PointTemplateWrapper> getPoints() {
			return points;
		}
		public List<AttributeWrapper> getAttributes() {
			return attributes;
		}
		public List<CommandDefinitionWrapper> getCommands() {
			return commands;
		}
		public List<DeviceTag> getTags() {
			return tags;
		}
	}
	
	public class PointTemplateWrapper {
		
		private PointTemplate pointTemplate;
		private PointType pointType;
		private String uomString;
		private String stateGroup;
		
		public PointTemplateWrapper(PointTemplate pointTemplate) {
			
			this.pointTemplate = pointTemplate;
			this.pointType = pointTemplate.getPointIdentifier().getPointType();
			this.uomString = "";
			this.stateGroup = "";
			int uom = pointTemplate.getUnitOfMeasure();
			if (uom >= 0) {
				this.uomString = unitMeasureDao.getLiteUnitMeasure(pointTemplate.getUnitOfMeasure()).getUnitMeasureName();
			} else {
				int stateGroupId = pointTemplate.getStateGroupId();
				LiteStateGroup liteStateGroup = stateDao.getLiteStateGroup(stateGroupId);
				this.stateGroup = liteStateGroup.getStateGroupName();
			}
		}

		public PointTemplate getPointTemplate() {
			return pointTemplate;
		}
		public PointType getPointType() {
			return pointType;
		}
		public String getUomString() {
			return uomString;
		}
		public String getStateGroup() {
			return stateGroup;
		}
	}
	
	public class AttributeWrapper {
		
		private AttributeDefinition attribute;
		private PointTemplateWrapper pointTemplateWrapper;
		
		public AttributeWrapper(AttributeDefinition attribute) {
			
			this.attribute = attribute;
			if (attribute instanceof BasicAttributeDefinition) {
                BasicAttributeDefinition basicAttributeLookup = (BasicAttributeDefinition) attribute;
                
                this.pointTemplateWrapper = new PointTemplateWrapper(basicAttributeLookup.getPointTemplate());
            }
		}
		
		public AttributeDefinition getAttribute() {
			return attribute;
		}
		public PointTemplateWrapper getPointTemplateWrapper() {
			return pointTemplateWrapper;
		}
	}
	
	public class CommandDefinitionWrapper {
		
		private CommandDefinition commandDefinition;
		private List<String> pointNames = new ArrayList<String>();
		
		public CommandDefinitionWrapper(CommandDefinition commandDefinition, DeviceDefinition deviceDefiniton) {
			
			this.commandDefinition = commandDefinition;
			
			Set<PointIdentifier> affectedPointList = commandDefinition.getAffectedPointList();
			Set<PointTemplate> allPointTemplates = deviceDefinitionDao.getAllPointTemplates(deviceDefiniton);
			for (PointIdentifier affectedPoint : affectedPointList) {
				for (PointTemplate pointTemplate : allPointTemplates) {
					if (affectedPoint.getType() == pointTemplate.getType() && affectedPoint.getOffset() == pointTemplate.getOffset()) {
						pointNames.add(pointTemplate.getName());
						break;
					}
				}
			}
		}
		
		public CommandDefinition getCommandDefinition() {
			return commandDefinition;
		}
		public List<String> getPointNames() {
			return pointNames;
		}
	}
	
	
	@Autowired
	public void setDeviceDefinitionDao(DeviceDefinitionDao deviceDefinitionDao) {
		this.deviceDefinitionDao = deviceDefinitionDao;
	}
	@Autowired
	public void setUnitMeasureDao(UnitMeasureDao unitMeasureDao) {
		this.unitMeasureDao = unitMeasureDao;
	}
	@Autowired
	public void setStateDao(StateDao stateDao) {
		this.stateDao = stateDao;
	}
}
