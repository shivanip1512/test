package com.cannontech.web.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.pao.definition.attribute.lookup.AttributeDefinition;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.CommandDefinition;
import com.cannontech.common.pao.definition.model.PaoDefinition;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.common.pao.definition.model.PaoTagDefinition;
import com.cannontech.common.pao.definition.model.PointIdentifier;
import com.cannontech.common.pao.definition.model.PointTemplate;
import com.cannontech.core.dao.StateGroupDao;
import com.cannontech.core.dao.UnitMeasureDao;
import com.cannontech.database.data.lite.LiteStateGroup;
import com.cannontech.database.data.point.PointType;
import com.cannontech.user.YukonUserContext;
import com.google.common.collect.Sets;

@Controller
public class DeviceDefinitionViewerController {

    @Autowired private PaoDefinitionDao paoDefinitionDao;
	@Autowired private UnitMeasureDao unitMeasureDao;
	@Autowired private StateGroupDao stateGroupDao;
    @Autowired private AttributeService attributeService;
    private static final String NO_FILTER = "ALL";

	private static String[] DISPLAY_GROUP_ORDER = {"MCT", "RFMESH", "IPC", "Two Way LCR", "Demand Response", "Signal Transmitters", "Electronic Meters", "RTU", "Virtual", "Grid Advisor", "Volt/Var", ""};
	
	@RequestMapping(value = "/deviceDefinition.xml", method = RequestMethod.GET)
    public String view(DeviceDefinitionsFilter definitionsFilter, ModelMap model, YukonUserContext context) throws Exception {

        // init
        Set<PaoDefinition> allDefinitions = paoDefinitionDao.getAllPaoDefinitions();
        Map<String, Set<PaoDefinition>> allDeviceTypes = new LinkedHashMap<String, Set<PaoDefinition>>();
        Set<PaoDefinition> displayDefinitions = new LinkedHashSet<PaoDefinition>();
        
        Set<String> allDisplayGroups = new LinkedHashSet<String>();
        Set<String> allChangeGroups = new HashSet<String>();
        SortedSet<Attribute> allAttributes = Sets.newTreeSet(attributeService.getNameComparator(context));
        Set<PaoTag> allTags = new HashSet<PaoTag>();
        
        // parameters
        String deviceTypeParam = definitionsFilter.getDeviceType();
        String displayGroupParam = definitionsFilter.getDisplayGroup();
        String changeGroupParam = definitionsFilter.getChangeGroup();
        String attributeParam = definitionsFilter.getAttribute();
        String tagParam = definitionsFilter.getTag();
        
        // all
        for (PaoDefinition deviceDefiniton : allDefinitions) {
        	
        	// allDeviceTypes
        	String displayGroup = deviceDefiniton.getDisplayGroup();
        	if (displayGroup == null) {
        		displayGroup = "Untitled";
        	}
        	if (!allDeviceTypes.containsKey(displayGroup)) {
        		allDeviceTypes.put(displayGroup, new LinkedHashSet<PaoDefinition>());
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
        	List<AttributeDefinition> definitionAttributes = new ArrayList<AttributeDefinition>(paoDefinitionDao.getDefinedAttributes(deviceDefiniton.getType()));
        	for (AttributeDefinition attribute : definitionAttributes) {
        	    allAttributes.add(attribute.getAttribute());
        	}
        	
        	// allTags
        	List<PaoTag> definitionTags = new ArrayList<PaoTag>(paoDefinitionDao.getSupportedTags(deviceDefiniton));
        	for (PaoTag tag : definitionTags) {
        		if (!allTags.contains(tag)) {
        			allTags.add(tag);
        		}
        	}
        	
            // add to displayDefinitions
            if (isNotFiltered(deviceTypeParam) || isNotFiltered(displayGroupParam) || isNotFiltered(changeGroupParam)
                || isNotFiltered(attributeParam) || isNotFiltered(tagParam)) {
                displayDefinitions.add(deviceDefiniton);
            } else if (!StringUtils.isBlank(deviceTypeParam) && PaoType.valueOf(deviceTypeParam) == deviceDefiniton.getType()) {
                displayDefinitions.add(deviceDefiniton);
            } else if (!StringUtils.isBlank(displayGroupParam) && displayGroupParam.equals(
                deviceDefiniton.getDisplayGroup() == null ? "Untitled" : deviceDefiniton.getDisplayGroup())) {
                displayDefinitions.add(deviceDefiniton);
            } else if (!StringUtils.isBlank(changeGroupParam) && changeGroupParam.equals(deviceDefiniton.getChangeGroup())) {
                displayDefinitions.add(deviceDefiniton);
            } else if (!StringUtils.isBlank(attributeParam)) {
                for (AttributeDefinition attribute : definitionAttributes) {
                    if (StringUtils.isBlank(attributeParam)
                        || attribute.getAttribute().getKey().equals(attributeParam)) {
                        displayDefinitions.add(deviceDefiniton);
                    }
                }
            } else if (!StringUtils.isBlank(tagParam)) {
                for (PaoTag tag : definitionTags) {
                    if (StringUtils.isBlank(tagParam) || tagParam.equals(tag.name())) {
                        displayDefinitions.add(deviceDefiniton);
                    }
                }
            } 
        }
        
         // display definitions info
        Map<String, Set<DeviceInfo>> displayDefinitionsMap = new HashMap<String, Set<DeviceInfo>>();
        for (PaoDefinition deviceDefiniton : displayDefinitions) {
        	String displayGroup = deviceDefiniton.getDisplayGroup();
        	DeviceInfo deviceInfo = new DeviceInfo(deviceDefiniton);
        	if (!displayDefinitionsMap.containsKey(displayGroup)) {
        		displayDefinitionsMap.put(displayGroup, new LinkedHashSet<DeviceInfo>());
        	}
        	displayDefinitionsMap.get(displayGroup).add(deviceInfo);
        }
        
        model.addAttribute("definitionsFilter", definitionsFilter);
        // sort
        allDeviceTypes = sortDeviceTypesByGroupOrder(allDeviceTypes);
        displayDefinitionsMap = sortDisplayDefinitionsByGroupOrder(displayDefinitionsMap);
        allDisplayGroups = sortDisplayGroupsByDisplayGroupOrder(allDisplayGroups);
        
        model.addAttribute("deviceTypeParam", deviceTypeParam);
        model.addAttribute("displayGroupParam", displayGroupParam);
        model.addAttribute("changeGroupParam", changeGroupParam);
        model.addAttribute("attributeParam", attributeParam);
        model.addAttribute("tagParam", tagParam);
        
        model.addAttribute("allDeviceTypes", allDeviceTypes);
        model.addAttribute("allDisplayGroups", allDisplayGroups);
        model.addAttribute("allChangeGroups", allChangeGroups);
        model.addAttribute("allAttributes", allAttributes);
        model.addAttribute("allTags", allTags);
        
        model.addAttribute("displayDefinitionsMap", displayDefinitionsMap);
        
        return "deviceDefinition/deviceDefinitionViewer.jsp";
    }

    private boolean isNotFiltered(String field) {
        return field != null && field.equals(NO_FILTER);
    }

	private Map<String, Set<PaoDefinition>> sortDeviceTypesByGroupOrder(Map<String, Set<PaoDefinition>> allDeviceTypes) {
		Map<String, Set<PaoDefinition>> sortedAllDeviceTypes = new LinkedHashMap<String, Set<PaoDefinition>>();
		for (String displayGroup : DISPLAY_GROUP_ORDER) {
			Set<PaoDefinition> deviceSet = allDeviceTypes.remove(displayGroup);
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
		
		private final PaoDefinition definition;
		private final List<PointTemplateWrapper> points;
		private final List<AttributeWrapper> attributes;
		private final List<CommandDefinitionWrapper> commands;
		private final Collection<PaoTagDefinition> tagDefinitions;
		
		public DeviceInfo(PaoDefinition deviceDefiniton) {
			
			this.definition = deviceDefiniton;
			
			// points
			List<PointTemplate> pointTemplates = new ArrayList<PointTemplate>(paoDefinitionDao.getAllPointTemplates(deviceDefiniton));
			Collections.sort(pointTemplates);
			
			this.points = new ArrayList<PointTemplateWrapper>();
			for (PointTemplate pointTemplate : pointTemplates) {
				this.points.add(new PointTemplateWrapper(deviceDefiniton, pointTemplate));
			}
			
			// attributes
			List<AttributeDefinition> attributes = new ArrayList<AttributeDefinition>(paoDefinitionDao.getDefinedAttributes(deviceDefiniton.getType()));
			Collections.sort(attributes);
			this.attributes = new ArrayList<AttributeWrapper>();
			for (AttributeDefinition attribute : attributes) {
				this.attributes.add(new AttributeWrapper(deviceDefiniton, attribute));
			}
			
			// commands
			List<CommandDefinition> commands = new ArrayList<CommandDefinition>(paoDefinitionDao.getAvailableCommands(deviceDefiniton));
			Collections.sort(commands);
			this.commands = new ArrayList<CommandDefinitionWrapper>();
			for (CommandDefinition commandDefinition : commands) {
				this.commands.add(new CommandDefinitionWrapper(commandDefinition, deviceDefiniton));
			}
			
			// tags
			Map<PaoTag, PaoTagDefinition> tagMap = 
			    paoDefinitionDao.getSupportedTagsForPaoType(definition.getType());
			this.tagDefinitions = tagMap.values();
		}

		public PaoDefinition getDefinition() {
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
		public Collection<PaoTagDefinition> getTagDefinitions() {
			return tagDefinitions;
		}
	}
	
	public class PointTemplateWrapper {
		
		private final PointTemplate pointTemplate;
		private final PointType pointType;
		private String uomString;
		private String stateGroup;
		private final boolean init;
		
		public PointTemplateWrapper(PaoDefinition deviceDefiniton, PointTemplate pointTemplate) {
			
			this.pointTemplate = pointTemplate;
			this.pointType = pointTemplate.getPointIdentifier().getPointType();
			this.uomString = "";
			this.stateGroup = "";
			int uom = pointTemplate.getUnitOfMeasure();
			if (uom >= 0) {
				this.uomString = unitMeasureDao.getLiteUnitMeasure(pointTemplate.getUnitOfMeasure()).getUnitMeasureName();
			} else {
				int stateGroupId = pointTemplate.getStateGroupId();
				LiteStateGroup liteStateGroup = stateGroupDao.getStateGroup(stateGroupId);
				this.stateGroup = liteStateGroup.getStateGroupName();
			}
			this.init = paoDefinitionDao.getInitPointTemplates(deviceDefiniton).contains(pointTemplate);
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
		public boolean isInit() {
			return init;
		}
	}
	
	public class AttributeWrapper {
		
		private final AttributeDefinition attribute;
		private PointTemplateWrapper pointTemplateWrapper;
		
		public AttributeWrapper(PaoDefinition deviceDefiniton, AttributeDefinition attribute) {
			
			this.attribute = attribute;
			if (attribute instanceof AttributeDefinition) {
                AttributeDefinition basicAttributeLookup = attribute;
                
                this.pointTemplateWrapper = new PointTemplateWrapper(deviceDefiniton, basicAttributeLookup.getPointTemplate());
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
		
		private final CommandDefinition commandDefinition;
		private final List<String> pointNames = new ArrayList<String>();
		
		public CommandDefinitionWrapper(CommandDefinition commandDefinition, PaoDefinition deviceDefiniton) {
			
			this.commandDefinition = commandDefinition;
			
			Set<PointIdentifier> affectedPointList = commandDefinition.getAffectedPointList();
			Set<PointTemplate> allPointTemplates = paoDefinitionDao.getAllPointTemplates(deviceDefiniton);
			for (PointIdentifier affectedPoint : affectedPointList) {
				for (PointTemplate pointTemplate : allPointTemplates) {
					if (affectedPoint.getPointType() == pointTemplate.getPointType() && affectedPoint.getOffset() == pointTemplate.getOffset()) {
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
}
