package com.cannontech.web.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.definition.attribute.lookup.AttributeDefinition;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.CommandDefinition;
import com.cannontech.common.pao.definition.model.PaoDefinition;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.common.pao.definition.model.PaoTagDefinition;
import com.cannontech.common.pao.definition.model.PointIdentifier;
import com.cannontech.common.pao.definition.model.PointTemplate;
import com.cannontech.core.dao.StateDao;
import com.cannontech.core.dao.UnitMeasureDao;
import com.cannontech.database.data.lite.LiteStateGroup;
import com.cannontech.database.data.point.PointType;
import com.google.common.base.Function;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;

public class DeviceDefinitionViewerController extends AbstractController {

	private PaoDefinitionDao paoDefinitionDao;
	private UnitMeasureDao unitMeasureDao;
	private StateDao stateDao;
	
	private static String[] DISPLAY_GROUP_ORDER = {"MCT", "Two Way LCR", "Signal Transmitters", "Electronic Meters", "RTU", "Virtual", "Grid Advisor", ""};
	
    private static Comparator<Attribute> attributeComparitor() {
        Ordering<Attribute> descriptionOrdering = Ordering.natural()
            .onResultOf(new Function<Attribute, String>() {
                public String apply(Attribute from) {
                    return from.getDescription();
                }
            });
        return descriptionOrdering;
    }
	
	@Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {

        ModelAndView mav = new ModelAndView("deviceDefinition/deviceDefinitionViewer.jsp");
        
        // init
        Set<PaoDefinition> allDefinitions = paoDefinitionDao.getAllPaoDefinitions();
        Map<String, Set<PaoDefinition>> allDeviceTypes = new LinkedHashMap<String, Set<PaoDefinition>>();
        Set<PaoDefinition> displayDefinitions = new LinkedHashSet<PaoDefinition>();
        
        Set<String> allDisplayGroups = new LinkedHashSet<String>();
        Set<String> allChangeGroups = new HashSet<String>();
        SortedSet<Attribute> allAttributes = Sets.newTreeSet(attributeComparitor());
        Set<PaoTag> allTags = new HashSet<PaoTag>();
        
        // parameters
        String deviceTypeParam = ServletRequestUtils.getStringParameter(request, "deviceType");
        String displayGroupParam = ServletRequestUtils.getStringParameter(request, "displayGroup");
        String changeGroupParam = ServletRequestUtils.getStringParameter(request, "changeGroup");
        String attributeParam = ServletRequestUtils.getStringParameter(request, "attribute");
        String tagParam = ServletRequestUtils.getStringParameter(request, "tag");
        
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
        		for (PaoTag tag : definitionTags) {
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
        for (PaoDefinition deviceDefiniton : displayDefinitions) {
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
		
		private PaoDefinition definition;
		private List<PointTemplateWrapper> points;
		private List<AttributeWrapper> attributes;
		private List<CommandDefinitionWrapper> commands;
		private Collection<PaoTagDefinition> tagDefinitions;
		
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
		
		private PointTemplate pointTemplate;
		private PointType pointType;
		private String uomString;
		private String stateGroup;
		private boolean init;
		
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
				LiteStateGroup liteStateGroup = stateDao.getLiteStateGroup(stateGroupId);
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
		
		private AttributeDefinition attribute;
		private PointTemplateWrapper pointTemplateWrapper;
		
		public AttributeWrapper(PaoDefinition deviceDefiniton, AttributeDefinition attribute) {
			
			this.attribute = attribute;
			if (attribute instanceof AttributeDefinition) {
                AttributeDefinition basicAttributeLookup = (AttributeDefinition) attribute;
                
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
		
		private CommandDefinition commandDefinition;
		private List<String> pointNames = new ArrayList<String>();
		
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
	
	
	@Autowired
	public void setPaoDefinitionDao(PaoDefinitionDao paoDefinitionDao) {
		this.paoDefinitionDao = paoDefinitionDao;
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
