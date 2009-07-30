package com.cannontech.common.device.definition.dao;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;
import org.exolab.castor.xml.Unmarshaller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.dao.DataAccessException;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.attribute.model.Attribute;
import com.cannontech.common.device.attribute.model.BuiltInAttribute;
import com.cannontech.common.device.definition.attribute.lookup.AttributeDefinition;
import com.cannontech.common.device.definition.attribute.lookup.BasicAttributeDefinition;
import com.cannontech.common.device.definition.model.CommandDefinition;
import com.cannontech.common.device.definition.model.DeviceDefinition;
import com.cannontech.common.device.definition.model.DeviceDefinitionImpl;
import com.cannontech.common.device.definition.model.DeviceTag;
import com.cannontech.common.device.definition.model.DeviceTagDefinition;
import com.cannontech.common.device.definition.model.PointIdentifier;
import com.cannontech.common.device.definition.model.PointTemplate;
import com.cannontech.common.device.definition.model.castor.BasicLookup;
import com.cannontech.common.device.definition.model.castor.Cmd;
import com.cannontech.common.device.definition.model.castor.Command;
import com.cannontech.common.device.definition.model.castor.Device;
import com.cannontech.common.device.definition.model.castor.DeviceDefinitions;
import com.cannontech.common.device.definition.model.castor.Point;
import com.cannontech.common.device.definition.model.castor.PointRef;
import com.cannontech.common.device.definition.model.castor.Tag;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.StateDao;
import com.cannontech.core.dao.UnitMeasureDao;
import com.cannontech.database.data.lite.LiteStateGroup;
import com.cannontech.database.data.lite.LiteUnitMeasure;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.database.data.point.PointUnits;
import com.cannontech.database.db.state.StateGroupUtils;
import com.google.common.collect.ImmutableSet;

/**
 * Implementation class for DeviceDefinitionDao
 */
public class DeviceDefinitionDaoImpl implements DeviceDefinitionDao {

    private Resource inputFile = null;
    private Resource schemaFile = null;
    private Resource customInputFile = new FileSystemResource(CtiUtilities.getYukonBase() + "\\Server\\Config\\deviceDefinition.xml");
    private Resource pointLegendFile = null;
    private UnitMeasureDao unitMeasureDao = null;
    private StateDao stateDao = null;
    private Logger log = YukonLogManager.getLogger(DeviceDefinitionDaoImpl.class);

    // Maps containing all of the data in the deviceDefinition.xml file
    private Map<PaoType, Map<Attribute, AttributeDefinition>> deviceAttributeAttrDefinitionMap = null;
    private Map<PaoType, Set<PointTemplate>> deviceAllPointTemplateMap = null;
    private Map<PaoType, Set<PointTemplate>> deviceInitPointTemplateMap = null;
    private Map<PaoType, DeviceDefinition> deviceTypeMap = null;
    private Map<String, List<DeviceDefinition>> deviceDisplayGroupMap = null;
    private Map<String, Set<DeviceDefinition>> changeGroupDevicesMap = null;
    private Map<PaoType, Set<CommandDefinition>> deviceCommandMap = null;
    private Map<PaoType, Set<DeviceTagDefinition>> deviceFeatureMap = null;
    private List<String> fileIdOrder = null;
    
    public void setInputFile(Resource inputFile) {
        this.inputFile = inputFile;
    }
    
    public void setSchemaFile(Resource schemaFile) {
        this.schemaFile = schemaFile;
    }
    
    public void setCustomInputFile(Resource customInputResource) {
        this.customInputFile = customInputResource;
    }

    @Autowired
    public void setStateDao(StateDao stateDao) {
        this.stateDao = stateDao;
    }
    
    @Autowired
    public void setUnitMeasureDao(UnitMeasureDao unitMeasureDao) {
        this.unitMeasureDao = unitMeasureDao;
    }
    
    public void setPointLegendFile(Resource pointLegendFile) {
        this.pointLegendFile = pointLegendFile;
    }
    
    // ATTRIBUTES
    //============================================
    @Override
    public AttributeDefinition getAttributeLookup(PaoType deviceType,
            BuiltInAttribute attribute) {
        Map<Attribute, AttributeDefinition> attributeLookupsForDevice = deviceAttributeAttrDefinitionMap.get(deviceType);
        Validate.notNull(attributeLookupsForDevice, "No AttributeLookups exist for " + deviceType);
        AttributeDefinition attributeDefinition = attributeLookupsForDevice.get(attribute);
        Validate.notNull(attributeDefinition, "No AttributeLookup exists for " + attribute + " on " + deviceType);
        return attributeDefinition;
    }
    
    @Override
    public Set<AttributeDefinition> getDefinedAttributes(PaoType deviceType) {
        Map<Attribute, AttributeDefinition> attributeLookupsForDevice = deviceAttributeAttrDefinitionMap.get(deviceType);
        if (attributeLookupsForDevice == null) {
            return ImmutableSet.of();
        } else {
            return ImmutableSet.copyOf(attributeLookupsForDevice.values());
        }
    }
    
    public Set<PointTemplate> getAllPointTemplates(DeviceDefinition deviceDefinition) {
        return this.getAllPointTemplates(deviceDefinition.getType());
    }

    public Set<PointTemplate> getInitPointTemplates(DeviceDefinition deviceDefinition) {
        return this.getInitPointTemplates(deviceDefinition.getType());
    }
    
    public PointTemplate getPointTemplateByTypeAndOffset(PaoType deviceType, PointIdentifier pointIdentifier) {

    	int pointType = pointIdentifier.getType();
    	int offset = pointIdentifier.getOffset();

        Set<PointTemplate> allPointTemplates = this.getAllPointTemplates(deviceType);

        for (PointTemplate template : allPointTemplates) {

            if (template.getType() == pointType && template.getOffset() == offset) {
                return template;
            }
        }

        String pointTypeString = PointTypes.getType(pointType);

        throw new NotFoundException("Point template not found for device type: " + deviceType + ", point type: " + pointTypeString + ", offset: " + offset);
    }
    
    private PointTemplate getPointTemplate(PaoType deviceType, String pointName) {
    	if (this.deviceAllPointTemplateMap.containsKey(deviceType)) {
        	Set<PointTemplate> templates = this.deviceAllPointTemplateMap.get(deviceType);

        	for (PointTemplate template : templates) {
            	if( template.getName().equals(pointName))
            		return template;
            }
            throw new NotFoundException("Device type " + deviceType + " does not support point " + pointName + ".");
        } else {
            throw new IllegalArgumentException("Device type " + deviceType + " is not supported.");
        }
    }
    
    public Set<PointTemplate> getAllPointTemplates(PaoType deviceType) {
        if (this.deviceAllPointTemplateMap.containsKey(deviceType)) {
            Set<PointTemplate> templates = this.deviceAllPointTemplateMap.get(deviceType);
            Set<PointTemplate> returnSet = new HashSet<PointTemplate>();
            for (PointTemplate template : templates) {
                returnSet.add(template);
            }
            return returnSet;
        } else {
            throw new IllegalArgumentException("Device type "
                    + deviceType + " is not supported.");
        }
    }

    public Set<PointTemplate> getInitPointTemplates(PaoType deviceType) {

        if (this.deviceInitPointTemplateMap.containsKey(deviceType)) {
        	Set<PointTemplate> templates = this.deviceInitPointTemplateMap.get(deviceType);
        	Set<PointTemplate> returnSet = new HashSet<PointTemplate>();
        	for (PointTemplate template : templates) {
                returnSet.add(template);
            }
        	return returnSet;
        } else {
            throw new IllegalArgumentException("Device type " + deviceType + " is not supported.");
        }
    }
    
    // COMMANDS
    //============================================
    public Set<CommandDefinition> getCommandsThatAffectPoints(PaoType deviceType, Set<? extends PointIdentifier> pointSet) {

        Set<CommandDefinition> commandSet = new HashSet<CommandDefinition>();

        Set<CommandDefinition> allCommandSet = this.deviceCommandMap.get(deviceType);

        for (CommandDefinition command : allCommandSet) {
            for (PointIdentifier point : pointSet) {
                if (command.affectsPoint(point)) {
                    commandSet.add(command);
                    break;
                }
            }
        }

        return commandSet;
    }
    
    public Set<CommandDefinition> getAvailableCommands(DeviceDefinition newDefinition) {
    	
    	PaoType deviceType = newDefinition.getType();
        Set<CommandDefinition> allCommandSet = this.deviceCommandMap.get(deviceType);
        return allCommandSet;
    }
    
    // TAGS
    //============================================
    public Set<DeviceTag> getSupportedTags(PaoType deviceType) {
    	
    	return getSupportedTagsForDeviceType(deviceType);
    }
    public Set<DeviceTag> getSupportedTags(DeviceDefinition deviceDefiniton) {
    	
    	return getSupportedTagsForDeviceType(deviceDefiniton.getType());
    }
    
    public Set<DeviceDefinition> getDevicesThatSupportTag(DeviceTag feature) {
    	
    	Set<DeviceDefinition> definitions = new HashSet<DeviceDefinition>();
    	for (PaoType deviceType : this.deviceFeatureMap.keySet()) {
    		
    		Set<DeviceTagDefinition> allDeviceFeatureDefinitionsSet = this.deviceFeatureMap.get(deviceType);
    		for (DeviceTagDefinition featureDefinition : allDeviceFeatureDefinitionsSet) {
    			
    			if (featureDefinition.getTag().equals(feature) && featureDefinition.isTagTrue()) {
    				definitions.add(getDeviceDefinition(deviceType));
    				break;
    			}
    		}
    	}
    	return definitions;
    }
    
    public boolean isTagSupported(DeviceDefinition deviceDefiniton, DeviceTag feature) {
    	return isTagSupported(deviceDefiniton.getType(), feature);
    }
    
    public boolean isTagSupported(PaoType deviceType, DeviceTag feature) {
    	
    	Set<DeviceTagDefinition> allDeviceFeatureDefinitionsSet = this.deviceFeatureMap.get(deviceType);
    	if (allDeviceFeatureDefinitionsSet != null) {
    		for (DeviceTagDefinition featureDefinition : allDeviceFeatureDefinitionsSet) {
    			if (featureDefinition.getTag().equals(feature) && featureDefinition.isTagTrue()) {
    				return true;
    			}
    		}
    	}
    	return false;
    }
    
    private Set<DeviceTag> getSupportedTagsForDeviceType(PaoType deviceType) {
    	
    	Set<DeviceTag> supportedDeviceFeaturesSet = new HashSet<DeviceTag>();
    	Set<DeviceTagDefinition> allDeviceFeatureDefinitionsSet = this.deviceFeatureMap.get(deviceType);
    	
    	if (allDeviceFeatureDefinitionsSet != null) {
	    	for (DeviceTagDefinition featureDefinition : allDeviceFeatureDefinitionsSet) {
	    		if (featureDefinition.isTagTrue()) {
	    			supportedDeviceFeaturesSet.add(featureDefinition.getTag());
	    		}
	    	}
    	}
    	return supportedDeviceFeaturesSet;
    }
    
    // DEFINITIONS
    //============================================
    public Set<DeviceDefinition> getAllDeviceDefinitions() {
    	
    	Set<DeviceDefinition> allDefinitions = Collections.unmodifiableSet(new LinkedHashSet<DeviceDefinition>(this.deviceTypeMap.values()));
    	return allDefinitions;
    }
    
    public Map<String, List<DeviceDefinition>> getDeviceDisplayGroupMap() {
        return Collections.unmodifiableMap(this.deviceDisplayGroupMap);
    }
    
    public DeviceDefinition getDeviceDefinition(PaoType deviceType) {
        
        if (this.deviceTypeMap.containsKey(deviceType)) {
            return this.deviceTypeMap.get(deviceType);
        } else {
            throw new IllegalArgumentException("Device type " + deviceType
                    + " is not supported.");
        }
    }
    
    public Set<DeviceDefinition> getDevicesThatDeviceCanChangeTo(DeviceDefinition deviceDefinition) {

        String changeGroup = deviceDefinition.getChangeGroup();
        if (changeGroup != null && this.changeGroupDevicesMap.containsKey(changeGroup)) {

            Set<DeviceDefinition> definitions = this.changeGroupDevicesMap.get(changeGroup);
            Set<DeviceDefinition> returnDefinitions = new HashSet<DeviceDefinition>();
            for (DeviceDefinition definition : definitions) {
                if (!definition.equals(deviceDefinition)) {
                    returnDefinitions.add(definition);
                }
            }
            return returnDefinitions;
        } else {
            throw new IllegalArgumentException("No device types found for change group: "
                    + changeGroup);
        }
    }
    
    // MISC
    //============================================
    public String getPointLegendHtml(String displayGroup) {
        try {
            log.debug("Transforming notification");
            log.debug("javax.xml: " + System.getProperty("javax.xml.transform.TransformerFactory"));
            Writer resultWriter = new StringWriter();
            Result result = new StreamResult(resultWriter);
            Source inputSource = new StreamSource(inputFile.getInputStream());
            Source xslSource = new StreamSource(pointLegendFile.getInputStream());
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer(xslSource);
            transformer.setParameter("group", displayGroup);
            transformer.transform(inputSource, result);
            String resultString = resultWriter.toString();
            return resultString;
        } catch (Exception e) {
            log.warn("Unable to translate deviceDefinition.xml file for displayGroup=" + displayGroup, e);
            return "Error rendering point legends: " + e.toString();
        }
    }
    
    // INITALIZATION
    //============================================
    public void initialize() throws Exception {

        this.deviceTypeMap = new LinkedHashMap<PaoType, DeviceDefinition>();
        this.deviceAllPointTemplateMap = new HashMap<PaoType, Set<PointTemplate>>();
        this.deviceInitPointTemplateMap = new HashMap<PaoType, Set<PointTemplate>>();
        this.deviceDisplayGroupMap = new LinkedHashMap<String, List<DeviceDefinition>>();
        this.changeGroupDevicesMap = new HashMap<String, Set<DeviceDefinition>>();
        this.deviceAttributeAttrDefinitionMap = new HashMap<PaoType, Map<Attribute, AttributeDefinition>>();
        this.deviceCommandMap = new HashMap<PaoType, Set<CommandDefinition>>();
        this.deviceFeatureMap = new HashMap<PaoType, Set<DeviceTagDefinition>>();
        this.fileIdOrder = new ArrayList<String>();
        
        // definition resources (in order from lowest level overrides to highest)
        List<Resource> definitionResources = new ArrayList<Resource>();
        if (customInputFile != null && customInputFile.exists()) {
        	definitionResources.add(customInputFile);
        }
        definitionResources.add(inputFile);
        
        // merge resources, sort
        Map<String, DeviceStore> deviceStores = mergeDefinitionResourcesIntoDeviceStoreMap(definitionResources);
        Map<String, DeviceStore> sortedDeviceStores = new LinkedHashMap<String, DeviceStore>();
        for (String id : this.fileIdOrder) {
    		sortedDeviceStores.put(id, deviceStores.remove(id));
        }
        sortedDeviceStores.putAll(deviceStores);
        
        
        // inherit definitions
        List<DeviceStore> finalDeviceStores = new ArrayList<DeviceStore>();
        for (DeviceStore deviceStore : sortedDeviceStores.values()) {
        	
        	mergeInheritedDeviceStoresOntoDeviceStore(deviceStore, sortedDeviceStores);
        	
        	if (!deviceStore.isAbstract()) {
        		finalDeviceStores.add(deviceStore);
        	}
    	}
        
        // final devices are ready to have data added to the dao data maps 
        for (DeviceStore deviceStore : finalDeviceStores) {
            this.addDevice(deviceStore);
        }
        
// this junk should probably stick around for a while during 4.3 development, makes it easy to compare dao output to earlier versions
//      //TEST FILE
//        BufferedWriter out = new BufferedWriter(new FileWriter("c:\\deviceDefinition_NEW-3.txt"));
//        
//        List<DeviceDefinition> definitionList = new ArrayList<DeviceDefinition>(this.deviceTypeMap.values());
//        Collections.sort(definitionList);
//        for (DeviceDefinition definition : definitionList) {
//        	
//        	int deviceType = definition.getType();
//        	Set<PointTemplate> pointTemplates = this.deviceAllPointTemplateMap.get(deviceType);
//        	Set<PointTemplate> initPointTemplates = this.deviceInitPointTemplateMap.get(deviceType);
//        	Set<CommandDefinition> commandDefinitions = this.deviceCommandMap.get(deviceType);
//        	Map<Attribute, AttributeLookup> attributesMap = this.deviceAttributeAttrDefinitionMap.get(deviceType);
//        	
//        	// definition
//            out.write("definition:type" + "\t\t" + definition.getType() + "\n");
//            out.write("definition:changeGroup" + "\t\t" + definition.getChangeGroup() + "\n");
//            out.write("definition:displayGroup" + "\t\t" + definition.getDisplayGroup() + "\n");
//            out.write("definition:displayName" + "\t\t" + definition.getDisplayName() + "\n");
//            out.write("definition:javaConstant" + "\t\t" + definition.getJavaConstant() + "\n");
//            
//            // points
//            List<PointTemplate> pointTemplatesList = new ArrayList<PointTemplate>(pointTemplates);
//            Collections.sort(pointTemplatesList);
//            for (PointTemplate pointTemplate : pointTemplatesList) {
//            	
//            	out.write("pointTemplate:name" + "\t\t" + pointTemplate.getName() + "\n");
//                out.write("pointTemplate:type" + "\t\t" + pointTemplate.getType() + "\n");
//                out.write("pointTemplate:offset" + "\t\t" + pointTemplate.getOffset() + "\n");
//                out.write("pointTemplate:multiplier" + "\t\t" + pointTemplate.getMultiplier() + "\n");
//                out.write("pointTemplate:stateGroup" + "\t\t" + pointTemplate.getStateGroupId() + "\n");
//                out.write("pointTemplate:uom" + "\t\t" + pointTemplate.getUnitOfMeasure() + "\n");
//                
//                if (initPointTemplates.contains(pointTemplate)) {
//                	out.write("pointTemplate:init" + "\t\t" + true + "\n");
//                } else {
//                	out.write("pointTemplate:init" + "\t\t" + false + "\n");
//                }
//            }
//            
//            // commands
//            List<CommandDefinition> commandDefinitionsList = new ArrayList<CommandDefinition>(commandDefinitions);
//            Collections.sort(commandDefinitionsList);
//            for (CommandDefinition commandDefinition : commandDefinitionsList) {
//            	
//            	out.write("commandDefinition:name" + "\t\t" + commandDefinition.getName() + "\n");
//            	
//            	Set<DevicePointIdentifier> affectedPointSet = commandDefinition.getAffectedPointList();
//            	List<DevicePointIdentifier> affectedPointList = new ArrayList<DevicePointIdentifier>(affectedPointSet);
//            	Collections.sort(affectedPointList);
//            	for (DevicePointIdentifier affectedPoint : affectedPointList) {
//            		
//            		out.write("commandDefinition:affectedPoint:type" + "\t\t" + affectedPoint.getType() + "\n");
//            		out.write("commandDefinition:affectedPoint:offset" + "\t\t" + affectedPoint.getOffset() + "\n");
//            	}
//            	
//            	List<String> commandStringList = commandDefinition.getCommandStringList();
//            	Collections.sort(commandStringList);
//            	for (String commandstring : commandStringList) {
//            		
//            		out.write("commandDefinition:commandstring" + "\t\t" + commandstring + "\n");
//            	}
//            }
//            
//            // attributes
//            List<AttributeLookup> attributeLookupList = new ArrayList<AttributeLookup>(attributesMap.values());
//            Collections.sort(attributeLookupList);
//            for (AttributeLookup attributeLookup : attributeLookupList) {
//
//        		out.write("attribute:key" + "\t\t" + attributeLookup.getAttribute().getKey() + "\n");
//        		out.write("attribute:description" + "\t\t" + attributeLookup.getAttribute().getDescription() + "\n");
//        		out.write("attribute:attributeLookup" + "\t\t" + attributeLookup.getPointRefName(null) + "\n");
//        	}
//            
//            out.write("\n\n");
//        }
//        out.flush();
//        out.close();
    }
    
    private void mergeInheritedDeviceStoresOntoDeviceStore(DeviceStore deviceStore, Map<String, DeviceStore> deviceStores) {
    	
    	List<String> inheritedIds = deviceStore.getInheritedIds();
    	for (String inheritedId : inheritedIds) {
    		
    		DeviceStore inheritedDeviceStore = deviceStores.get(inheritedId);
    		mergeInheritedDeviceStoresOntoDeviceStore(inheritedDeviceStore, deviceStores);
    		deviceStore.mergeDeviceStore(inheritedDeviceStore);
    	}
    }
    
    private Map<String, DeviceStore> mergeDefinitionResourcesIntoDeviceStoreMap(List<Resource> definitionResources) throws Exception {
		
    	Map<String, DeviceStore> deviceStores = new LinkedHashMap<String, DeviceStore>();
    	
    	int resourceCount = 0;
    	boolean isLastResource = false;
    	for (Resource definitionResource : definitionResources) {
    	
    		resourceCount++;
    		if (resourceCount == definitionResources.size()) {
    			isLastResource = true;
    		}
    		
			validateXmlSchema(definitionResource);
			InputStreamReader reader = new InputStreamReader(definitionResource.getInputStream());
			
			try {
	            
	            DeviceDefinitions definition = (DeviceDefinitions) Unmarshaller.unmarshal(DeviceDefinitions.class, reader);
	            
	            for (Device device : definition.getDevice()) {
	        		if (device.getEnabled()) {
	        			
	        			DeviceStore deviceStore = new DeviceStore(device);
	        			String id = deviceStore.getId();
	        			
	        			if (deviceStores.get(id) == null) {
	        				deviceStores.put(id, deviceStore);
	        			} else {
	        				DeviceStore existingDeviceStore = deviceStores.get(id);
	        				existingDeviceStore.mergeDeviceStore(deviceStore);
	        			}
	        			
	        			if (isLastResource) {
		        			this.fileIdOrder.add(id);
		        		}
	        		}
	            }
			} finally {
				try {
	                if (reader != null) {
	                    reader.close();
	                }
	            } catch (IOException e) {}
			}
    	}
    	
    	return deviceStores;
    }
    
    public void validateXmlSchema(Resource currentDefinitionResource) throws IOException, SAXException, ParserConfigurationException {
        InputStream is = currentDefinitionResource.getInputStream();
        URL schemaUrl = schemaFile.getURL();
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setValidating(false); // this is for DTD, so we want it off
        factory.setNamespaceAware(true);
        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema = schemaFactory.newSchema(schemaUrl);
        factory.setSchema(schema);

        SAXParser saxParser = factory.newSAXParser();

        XMLReader xmlReader = saxParser.getXMLReader();
        xmlReader.setErrorHandler(new ErrorHandler() {

            @Override
            public void error(SAXParseException exception) throws SAXException {
                throw exception;
            }

            @Override
            public void fatalError(SAXParseException exception) throws SAXException {
                throw exception;
            }

            @Override
            public void warning(SAXParseException exception) throws SAXException {
                throw exception;
            }
        });
        
        
        xmlReader.parse(new InputSource(is));
        is.close();
    }
    
    // ADD DEVICE
    //============================================
    /**
     * Helper method to add the device and its point templates to the
     * appropriate maps
     * @param device - Device to add
     */
    private void addDevice(DeviceStore device) {

        PaoType deviceType = device.getDeviceType();
        String javaConstant = device.getId();

        String displayName = null;
        if (device.getDisplayName() != null) {
            displayName = device.getDisplayName();
        }
        String group = null;
        if (device.getDisplayGroup() != null) {
            group = device.getDisplayGroup();
        }

        DeviceDefinition deviceDefinition = new DeviceDefinitionImpl(deviceType,
                                                                     displayName,
                                                                     group,
                                                                     javaConstant,
                                                                     device.getChangeGroup());

        // Add deviceDefinition to type map
        this.deviceTypeMap.put(deviceType, deviceDefinition);

        // Add deviceDefinition to change group map
        if (device.getChangeGroup() != null) {
            String changeGroup = device.getChangeGroup();

            // Add the paoClass to the hashmap if not there
            if (!this.changeGroupDevicesMap.containsKey(changeGroup)) {
                this.changeGroupDevicesMap.put(changeGroup, new HashSet<DeviceDefinition>());
            }

            this.changeGroupDevicesMap.get(changeGroup).add(deviceDefinition);

        }

        // Add deviceDefinition to group map
        if (group != null) {
            List<DeviceDefinition> typeList = this.deviceDisplayGroupMap.get(group);
            if (typeList == null) {
                typeList = new ArrayList<DeviceDefinition>();
                this.deviceDisplayGroupMap.put(group, typeList);
            }

            typeList.add(deviceDefinition);
        }

        // Add device points
        Set<PointTemplate> pointSet = new HashSet<PointTemplate>();
        Set<PointTemplate> initPointSet = new HashSet<PointTemplate>();

        Map<String, PointTemplate> pointNameTemplateMap = new HashMap<String, PointTemplate>();

        Point[] points = device.getEnabledPoints();
        for (Point point : points) {
            PointTemplate template = this.createPointTemplate(point);
            pointSet.add(template);

            if (point.getInit()) {
            	initPointSet.add(template);
            }
            
            if (pointNameTemplateMap.containsKey(template.getName())) {
                throw new RuntimeException("Point name: " + template.getName() + " is used twice for device type: " + javaConstant + " in the deviceDefinition.xml file - point names must be unique within a device type");
            }
            pointNameTemplateMap.put(template.getName(), template);
        }
        this.deviceAllPointTemplateMap.put(deviceType, pointSet);
        this.deviceInitPointTemplateMap.put(deviceType, initPointSet);
        
        // Add device attributes
        Map<Attribute, AttributeDefinition> attributeMap = new HashMap<Attribute, AttributeDefinition>();
        com.cannontech.common.device.definition.model.castor.Attribute[] attributes = device.getEnabledAttributes();
        for (com.cannontech.common.device.definition.model.castor.Attribute attribute : attributes) {
        	
        	AttributeDefinition attributeDefinition = 
        		createAttributeDefinition(attribute.getName(), attribute.getChoiceValue(), pointNameTemplateMap);
        	Attribute pointAttribute = BuiltInAttribute.valueOf(attribute.getName());
        	
            if (attributeMap.containsKey(pointAttribute)) {
                throw new RuntimeException("Attribute: " + attribute.getName() + " is used twice for device type: " + javaConstant + " in the deviceDefinition.xml file - attribute names must be unique within a device type");
            }
        	attributeMap.put(pointAttribute, attributeDefinition);
        }   
        this.deviceAttributeAttrDefinitionMap.put(deviceType, attributeMap);

        // Add device commands
        Set<CommandDefinition> commandSet = new HashSet<CommandDefinition>();
        Command[] commands = device.getEnabledCommands();
        for (Command command : commands) {
            CommandDefinition definition = this.createCommandDefinition(device.getDisplayName(), 
                                                                        command,
                                                                        pointNameTemplateMap);
            commandSet.add(definition);
        }
        this.deviceCommandMap.put(deviceType, commandSet);
        
        // Add device features
        Set<DeviceTagDefinition> featureSet = new HashSet<DeviceTagDefinition>();
        Tag[] tags = device.getEnabledTags();
        for (Tag tag : tags) {
            DeviceTagDefinition definition = this.createDeviceTagDefinition(tag.getName(), tag.getValue());
            
            featureSet.add(definition);
        }
        this.deviceFeatureMap.put(deviceType, featureSet);
    }
    
    /**
     * Helper method to convert a castor AttributeLookup CHOICE object to a AttributeLookup definition
     * @param choiceLookup - The choice object from the attribute lookup
     * @param pointNameTemplateMap 
     * @return The AttributeLookup definition representing the castor CHOICE lookup
     */
	private AttributeDefinition createAttributeDefinition(String attributeName,
            Object choiceLookup, Map<String, PointTemplate> pointNameTemplateMap) {

        AttributeDefinition attributeDefinition = null;
        Attribute attribute = BuiltInAttribute.valueOf(attributeName);

        if (choiceLookup instanceof BasicLookup) {
            BasicLookup lookup = (BasicLookup) choiceLookup;
            PointTemplate pointTemplate = pointNameTemplateMap.get(lookup.getPoint());
            if (pointTemplate == null) {
                throw new IllegalArgumentException("Can't resolve point name '" + lookup.getPoint() + ":");
            }
            
            attributeDefinition = new BasicAttributeDefinition(attribute, pointTemplate);
        }

        if (attributeDefinition != null) {
            return attributeDefinition;
        }

        throw new IllegalArgumentException("Attribute Choice '" + choiceLookup.toString() + "' is not supported.");
    }
	
    /**
     * Helper method to convert a castor command to a command definition
     * @param deviceName - Name of device for commands
     * @param command - Command to convert
     * @param pointNameTemplateMap
     * @return The command definition representing the castor command
     */
    private CommandDefinition createCommandDefinition(String deviceName, Command command,
            Map<String, PointTemplate> pointNameTemplateMap) {

        CommandDefinition definition = new CommandDefinition(command.getName());

        // Add command text
        Cmd[] cmds = command.getCmd();
        for (Cmd cmd : cmds) {
            definition.addCommandString(cmd.getText());
        }

        // Add point reference
        PointRef[] pointRefs = command.getPointRef();
        for (PointRef pointRef : pointRefs) {
            if (!pointNameTemplateMap.containsKey(pointRef.getName())) {
                throw new RuntimeException("Point name: " + pointRef.getName() + " not found for device: " + deviceName + ".  command pointRefs must reference a point name from the same device in the deviceDefinition.xml file. Point is not on device, or point has been named incorrectly in a custom file.");
            }
            PointTemplate template = pointNameTemplateMap.get(pointRef.getName());
            PointIdentifier pi = new PointIdentifier(template.getType(), template.getOffset());
            definition.addAffectedPoint(pi);
        }

        return definition;
    }

    /**
     * Helper method to convert a castor feature to feature definition.
     * @param featureName
     * @param value
     * @return
     */
    private DeviceTagDefinition createDeviceTagDefinition(String tagName, boolean value) {
    	
    	DeviceTag feature = DeviceTag.valueOf(tagName);
    	DeviceTagDefinition definition = new DeviceTagDefinition(feature, value);
    	return definition;
    }

    /**
     * Helper method to convert a castor point to a point template
     * @param point - Point to convert
     * @return The point template representing the castor point
     */
    private PointTemplate createPointTemplate(Point point) {

        PointTemplate template = new PointTemplate(PointTypes.getType(point.getType()), point.getOffset());

        template.setName(point.getName());

        double multiplier = 1.0;
        int unitOfMeasure = PointUnits.UOMID_INVALID;
        if (point.getPointChoice().getPointChoiceSequence() != null) {
            if (point.getPointChoice().getPointChoiceSequence().getMultiplier() != null) {
                multiplier = point.getPointChoice()
                                  .getPointChoiceSequence()
                                  .getMultiplier()
                                  .getValue()
                                  .doubleValue();
            }
            if (point.getPointChoice().getPointChoiceSequence().getUnitofmeasure() != null) {
                String unitOfMeasureName = point.getPointChoice()
                                                .getPointChoiceSequence()
                                                .getUnitofmeasure()
                                                .getValue();
                LiteUnitMeasure unitMeasure = null;
                try {
                    unitMeasure = unitMeasureDao.getLiteUnitMeasure(unitOfMeasureName);
                } catch (DataAccessException e) {
                    throw new NotFoundException("Unit of measure does not exist: "
                            + unitOfMeasureName + ". Check the deviceDefinition.xml file ", e);
                }
                unitOfMeasure = unitMeasure.getUomID();
            }
        }
        template.setMultiplier(multiplier);
        template.setUnitOfMeasure(unitOfMeasure);

        int stateGroupId = StateGroupUtils.SYSTEM_STATEGROUPID;
        if (point.getPointChoice().getStategroup() != null) {

            String stateGroupName = point.getPointChoice().getStategroup().getValue();
            LiteStateGroup stateGroup = null;
            try {
                stateGroup = stateDao.getLiteStateGroup(stateGroupName);
            } catch (NotFoundException e) {
                throw new NotFoundException("State group does not exist: " + stateGroupName
                        + ". Check the deviceDefinition.xml file ", e);
            }
            stateGroupId = stateGroup.getStateGroupID();
        }
        template.setStateGroupId(stateGroupId);

        return template;
    }
}
