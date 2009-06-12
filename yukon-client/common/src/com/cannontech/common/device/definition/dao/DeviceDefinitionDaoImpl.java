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
import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.device.attribute.model.Attribute;
import com.cannontech.common.device.attribute.model.BuiltInAttribute;
import com.cannontech.common.device.definition.attribute.lookup.AttributeLookup;
import com.cannontech.common.device.definition.attribute.lookup.BasicLookupAttrDef;
import com.cannontech.common.device.definition.model.CommandDefinition;
import com.cannontech.common.device.definition.model.DeviceDefinition;
import com.cannontech.common.device.definition.model.DeviceDefinitionImpl;
import com.cannontech.common.device.definition.model.DeviceFeature;
import com.cannontech.common.device.definition.model.DevicePointIdentifier;
import com.cannontech.common.device.definition.model.FeatureDefinition;
import com.cannontech.common.device.definition.model.PointTemplate;
import com.cannontech.common.device.definition.model.castor.BasicLookup;
import com.cannontech.common.device.definition.model.castor.Cmd;
import com.cannontech.common.device.definition.model.castor.Command;
import com.cannontech.common.device.definition.model.castor.Device;
import com.cannontech.common.device.definition.model.castor.DeviceDefinitions;
import com.cannontech.common.device.definition.model.castor.Feature;
import com.cannontech.common.device.definition.model.castor.Point;
import com.cannontech.common.device.definition.model.castor.PointRef;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.StateDao;
import com.cannontech.core.dao.UnitMeasureDao;
import com.cannontech.database.data.lite.LiteStateGroup;
import com.cannontech.database.data.lite.LiteUnitMeasure;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.pao.PaoGroupsWrapper;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.database.data.point.PointUnits;
import com.cannontech.database.db.state.StateGroupUtils;
import com.cannontech.util.ReflectivePropertySearcher;

/**
 * Implementation class for DeviceDefinitionDao
 */
public class DeviceDefinitionDaoImpl implements DeviceDefinitionDao {

    private Resource inputFile = null;
    private Resource schemaFile = null;
    private Resource customInputFile = new FileSystemResource(CtiUtilities.getYukonBase() + "\\Server\\Config\\deviceDefinition.xml");
    private Resource pointLegendFile = null;
    private PaoGroupsWrapper paoGroupsWrapper = null;
    private String javaConstantClassName = null;
    private UnitMeasureDao unitMeasureDao = null;
    private StateDao stateDao = null;
    private Logger log = YukonLogManager.getLogger(DeviceDefinitionDaoImpl.class);

    // Maps containing all of the data in the deviceDefinition.xml file
    private Map<Integer, Map<Attribute, AttributeLookup>> deviceAttributeAttrDefinitionMap = null;
    private Map<Integer, Set<PointTemplate>> deviceAllPointTemplateMap = null;
    private Map<Integer, Set<PointTemplate>> deviceInitPointTemplateMap = null;
    private Map<Integer, DeviceDefinition> deviceTypeMap = null;
    private Map<String, List<DeviceDefinition>> deviceDisplayGroupMap = null;
    private Map<String, Set<DeviceDefinition>> changeGroupDevicesMap = null;
    private Map<Integer, Set<CommandDefinition>> deviceCommandMap = null;
    private Map<Integer, Set<FeatureDefinition>> deviceFeatureMap = null;
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
    public void setPaoGroupsWrapper(PaoGroupsWrapper paoGroupsWrapper) {
        this.paoGroupsWrapper = paoGroupsWrapper;
    }
    public void setJavaConstantClassName(String javaConstantClassName) {
        this.javaConstantClassName = javaConstantClassName;
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
    public Set<Attribute> getAvailableAttributes(DeviceDefinition deviceDefinition) {
    	
    	int deviceType = deviceDefinition.getType();
    	return getAvailableAttributes(deviceType);
    }
    
    public Set<Attribute> getAvailableAttributes(YukonDevice device) {
    	
        int deviceType = device.getType();
        return getAvailableAttributes(deviceType);
    }

	private Set<Attribute> getAvailableAttributes(int deviceType) {
		
		if (this.deviceAttributeAttrDefinitionMap.containsKey(deviceType)) {
            Map<Attribute, AttributeLookup> attributeMap = this.deviceAttributeAttrDefinitionMap.get(deviceType);
            return Collections.unmodifiableSet(attributeMap.keySet());
        } else {
            throw new IllegalArgumentException("Device type '" + deviceType
                    + "' is not supported.");
        }
	}

    // POINTS
    //============================================
    public Set<DevicePointIdentifier> getDevicePointIdentifierForAttributes(YukonDevice device, Set<? extends Attribute> attributes) {
    	
        Set<DevicePointIdentifier> pointSet = new HashSet<DevicePointIdentifier>(attributes.size());
        for (Attribute attribute : attributes) {
            PointTemplate pointTemplateForAttribute = getPointTemplateForAttribute(device, attribute);
            pointSet.add(pointTemplateForAttribute.getDevicePointIdentifier());
        }
        return pointSet;
    }
    
    public PointTemplate getPointTemplateForAttribute(YukonDevice device, Attribute attribute) {
        int deviceType = device.getType();
        if (this.deviceAttributeAttrDefinitionMap.containsKey(deviceType)) {
            Map<Attribute, AttributeLookup> attributeMap = this.deviceAttributeAttrDefinitionMap.get(deviceType);
            if (attributeMap.containsKey(attribute)) {
            	AttributeLookup attributeDefinition = attributeMap.get(attribute);
            	String pointName = attributeDefinition.getPointRefName(device);
            	return getPointTemplate(deviceType, pointName);
            } else {
                throw new IllegalArgumentException("Attribute " + attribute.getKey()
                        + " is not supported for Device type '" + deviceType + "'");
            }
        } else {
            throw new IllegalArgumentException("Device type '" + deviceType
                    + "' is not supported.");
        }
    }
    
    public Set<PointTemplate> getAllPointTemplates(YukonDevice device) {
        int deviceType = device.getType();
        return this.getAllPointTemplates(deviceType);
    }

    public Set<PointTemplate> getAllPointTemplates(DeviceDefinition deviceDefinition) {
        return this.getAllPointTemplates(deviceDefinition.getType());
    }

    public Set<PointTemplate> getInitPointTemplates(YukonDevice device) {
        int deviceType = device.getType();
        return this.getInitPointTemplates(deviceType);
    }

    public Set<PointTemplate> getInitPointTemplates(DeviceDefinition deviceDefinition) {
        return this.getInitPointTemplates(deviceDefinition.getType());
    }
    
    public PointTemplate getPointTemplateByTypeAndOffset(YukonDevice device,
            Integer offset, Integer pointType) {


        Set<PointTemplate> allPointTemplates = this.getAllPointTemplates(device.getType());

        for (PointTemplate template : allPointTemplates) {

            if (template.getType() == pointType && template.getOffset() == offset) {
                return template;
            }
        }

        String deviceType = paoGroupsWrapper.getPAOTypeString(device.getType());
        String pointTypeString = PointTypes.getType(pointType);

        throw new NotFoundException("Point template not found for device type: " + deviceType + ", point type: " + pointTypeString + ", offset: " + offset);
    }
    
    private PointTemplate getPointTemplate(Integer deviceType, String pointName) {
    	if (this.deviceAllPointTemplateMap.containsKey(deviceType)) {
        	Set<PointTemplate> templates = this.deviceAllPointTemplateMap.get(deviceType);

        	for (PointTemplate template : templates) {
            	if( template.getName().equals(pointName))
            		return template;
            }
            throw new NotFoundException("Device type '"
                    + paoGroupsWrapper.getPAOTypeString(deviceType) + "' does not support point " + pointName + ".");
        } else {
            throw new IllegalArgumentException("Device type '"
                    + paoGroupsWrapper.getPAOTypeString(deviceType) + "' is not supported.");
        }
    }
    
    private Set<PointTemplate> getAllPointTemplates(Integer deviceType) {
        if (this.deviceAllPointTemplateMap.containsKey(deviceType)) {
            Set<PointTemplate> templates = this.deviceAllPointTemplateMap.get(deviceType);
            Set<PointTemplate> returnSet = new HashSet<PointTemplate>();
            for (PointTemplate template : templates) {
                returnSet.add(template);
            }
            return returnSet;
        } else {
            throw new IllegalArgumentException("Device type '"
                    + paoGroupsWrapper.getPAOTypeString(deviceType) + "' is not supported.");
        }
    }

    private Set<PointTemplate> getInitPointTemplates(Integer deviceType) {

        if (this.deviceInitPointTemplateMap.containsKey(deviceType)) {
        	Set<PointTemplate> templates = this.deviceInitPointTemplateMap.get(deviceType);
        	Set<PointTemplate> returnSet = new HashSet<PointTemplate>();
        	for (PointTemplate template : templates) {
                returnSet.add(template);
            }
        	return returnSet;
        } else {
            throw new IllegalArgumentException("Device type '"
                    + paoGroupsWrapper.getPAOTypeString(deviceType) + "' is not supported.");
        }
    }
    
    // COMMANDS
    //============================================
    public Set<CommandDefinition> getCommandsThatAffectPoints(YukonDevice device, Set<? extends DevicePointIdentifier> pointSet) {

        Set<CommandDefinition> commandSet = new HashSet<CommandDefinition>();

        int deviceType = device.getType();
        Set<CommandDefinition> allCommandSet = this.deviceCommandMap.get(deviceType);

        for (CommandDefinition command : allCommandSet) {
            for (DevicePointIdentifier point : pointSet) {
                if (command.affectsPoint(point)) {
                    commandSet.add(command);
                    break;
                }
            }
        }

        return commandSet;
    }
    
    public Set<CommandDefinition> getAvailableCommands(DeviceDefinition newDefinition) {
    	
    	int deviceType = newDefinition.getType();
        Set<CommandDefinition> allCommandSet = this.deviceCommandMap.get(deviceType);
        return allCommandSet;
    }
    
    // FEATURES
    //============================================
    public Set<DeviceFeature> getSupportedFeatures(YukonDevice device) {
    	
    	int deviceType = device.getType();
    	return getSupportedFeaturesForDeviceType(deviceType);
    }
    public Set<DeviceFeature> getSupportedFeatures(DeviceDefinition deviceDefiniton) {
    	
    	int deviceType = deviceDefiniton.getType();
    	return getSupportedFeaturesForDeviceType(deviceType);
    }
    
    public Set<DeviceDefinition> getDevicesThatSupportFeature(DeviceFeature feature) {
    	
    	Set<DeviceDefinition> definitions = new HashSet<DeviceDefinition>();
    	for (int deviceType : this.deviceFeatureMap.keySet()) {
    		
    		Set<FeatureDefinition> allDeviceFeatureDefinitionsSet = this.deviceFeatureMap.get(deviceType);
    		for (FeatureDefinition featureDefinition : allDeviceFeatureDefinitionsSet) {
    			
    			if (featureDefinition.getFeature().equals(feature) && featureDefinition.isSupported()) {
    				definitions.add(getDeviceDefinition(deviceType));
    				break;
    			}
    		}
    	}
    	return definitions;
    }
    
    public boolean isFeatureSupported(YukonDevice device, DeviceFeature feature) {
    	int deviceType = device.getType();
    	return isFeatureSupported(deviceType, feature);
    }
    public boolean isFeatureSupported(DeviceDefinition deviceDefiniton, DeviceFeature feature) {
    	int deviceType = deviceDefiniton.getType();
    	return isFeatureSupported(deviceType, feature);
    }
    
    public boolean isFeatureSupported(LiteYukonPAObject litePao, DeviceFeature feature) {
    	int deviceType = litePao.getType();
    	return isFeatureSupported(deviceType, feature);
    }
    
    private boolean isFeatureSupported(int deviceType, DeviceFeature feature) {
    	
    	Set<FeatureDefinition> allDeviceFeatureDefinitionsSet = this.deviceFeatureMap.get(deviceType);
    	if (allDeviceFeatureDefinitionsSet != null) {
    		for (FeatureDefinition featureDefinition : allDeviceFeatureDefinitionsSet) {
    			if (featureDefinition.getFeature().equals(feature) && featureDefinition.isSupported()) {
    				return true;
    			}
    		}
    	}
    	return false;
    }
    
    private Set<DeviceFeature> getSupportedFeaturesForDeviceType(int deviceType) {
    	
    	Set<DeviceFeature> supportedDeviceFeaturesSet = new HashSet<DeviceFeature>();
    	Set<FeatureDefinition> allDeviceFeatureDefinitionsSet = this.deviceFeatureMap.get(deviceType);
    	
    	if (allDeviceFeatureDefinitionsSet != null) {
	    	for (FeatureDefinition featureDefinition : allDeviceFeatureDefinitionsSet) {
	    		if (featureDefinition.isSupported()) {
	    			supportedDeviceFeaturesSet.add(featureDefinition.getFeature());
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
    
    public DeviceDefinition getDeviceDefinition(int deviceType) {
        
        if (this.deviceTypeMap.containsKey(deviceType)) {
            return this.deviceTypeMap.get(deviceType);
        } else {
            throw new IllegalArgumentException("Device type '" + deviceType
                    + "' is not supported.");
        }
    }
    
    public DeviceDefinition getDeviceDefinition(YukonDevice device) {

        int deviceType = device.getType();
        return this.getDeviceDefinition(deviceType);

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

        this.deviceTypeMap = new LinkedHashMap<Integer, DeviceDefinition>();
        this.deviceAllPointTemplateMap = new HashMap<Integer, Set<PointTemplate>>();
        this.deviceInitPointTemplateMap = new HashMap<Integer, Set<PointTemplate>>();
        this.deviceDisplayGroupMap = new LinkedHashMap<String, List<DeviceDefinition>>();
        this.changeGroupDevicesMap = new HashMap<String, Set<DeviceDefinition>>();
        this.deviceAttributeAttrDefinitionMap = new HashMap<Integer, Map<Attribute, AttributeLookup>>();
        this.deviceCommandMap = new HashMap<Integer, Set<CommandDefinition>>();
        this.deviceFeatureMap = new HashMap<Integer, Set<FeatureDefinition>>();
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
        
        	this.validateDeviceConstant(deviceStore);
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
    
    private void validateDeviceConstant(DeviceStore device) throws Exception {

        // Validate xml device type int / java constant
        String javaConstant = device.getId();
        int deviceType = device.getDeviceType();
        int constantValue = ReflectivePropertySearcher.getIntForFQN(javaConstantClassName,
                                                                    javaConstant);
        if (deviceType != constantValue) {
            throw new Exception("Java constant value for " + javaConstant + ": " + constantValue
                    + " does not match value in deviceDefinition.xml file " + deviceType);
        }

    }
    
    // ADD DEVICE
    //============================================
    /**
     * Helper method to add the device and its point templates to the
     * appropriate maps
     * @param device - Device to add
     */
    private void addDevice(DeviceStore device) {

        int deviceType = device.getDeviceType();
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
        Map<Attribute, AttributeLookup> attributeMap = new HashMap<Attribute, AttributeLookup>();
        com.cannontech.common.device.definition.model.castor.Attribute[] attributes = device.getEnabledAttributes();
        for (com.cannontech.common.device.definition.model.castor.Attribute attribute : attributes) {
        	
        	AttributeLookup attributeDefinition = 
        		createAttributeDefinition(attribute.getName(), attribute.getChoiceValue());
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
        Set<FeatureDefinition> featureSet = new HashSet<FeatureDefinition>();
        Feature[] features = device.getEnabledFeatures();
        for (Feature feature : features) {
            FeatureDefinition definition = this.createFeatureDefinition(feature.getName(), feature.getValue());
            
            featureSet.add(definition);
        }
        this.deviceFeatureMap.put(deviceType, featureSet);
    }
    
    /**
     * Helper method to convert a castor AttributeLookup CHOICE object to a AttributeLookup definition
     * @param choiceLookup - The choice object from the attribute lookup
     * @return The AttributeLookup definition representing the castor CHOICE lookup
     */
	private AttributeLookup createAttributeDefinition(String attributeName,
            Object choiceLookup) {

        AttributeLookup attributeDefinition = null;
        Attribute attribute = BuiltInAttribute.valueOf(attributeName);

        if (choiceLookup instanceof BasicLookup) {
            BasicLookup lookup = (BasicLookup) choiceLookup;
            attributeDefinition = new BasicLookupAttrDef(attribute);
            ((BasicLookupAttrDef) attributeDefinition).setPointName(lookup.getPoint());
        }

        if (attributeDefinition != null)
            return attributeDefinition;

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
            DevicePointIdentifier dpi = new DevicePointIdentifier(template.getType(), template.getOffset());
            definition.addAffectedPoint(dpi);
        }

        return definition;
    }

    /**
     * Helper method to convert a castor feature to feature definition.
     * @param featureName
     * @param value
     * @return
     */
    private FeatureDefinition createFeatureDefinition(String featureName, boolean value) {
    	
    	DeviceFeature feature = DeviceFeature.valueOf(featureName);
    	FeatureDefinition definition = new FeatureDefinition(feature, value);
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
