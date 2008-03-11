package com.cannontech.common.device.definition.dao;

import java.io.File;
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
import com.cannontech.common.device.definition.model.DevicePointIdentifier;
import com.cannontech.common.device.definition.model.PointTemplate;
import com.cannontech.common.device.definition.model.castor.BasicLookup;
import com.cannontech.common.device.definition.model.castor.Cmd;
import com.cannontech.common.device.definition.model.castor.Command;
import com.cannontech.common.device.definition.model.castor.Device;
import com.cannontech.common.device.definition.model.castor.DeviceDefinitions;
import com.cannontech.common.device.definition.model.castor.Point;
import com.cannontech.common.device.definition.model.castor.PointRef;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.StateDao;
import com.cannontech.core.dao.UnitMeasureDao;
import com.cannontech.database.data.lite.LiteStateGroup;
import com.cannontech.database.data.lite.LiteUnitMeasure;
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
    private File customInputFile = new File(CtiUtilities.getYukonBase() + "\\Server\\Config\\deviceDefinition.xml");
    private Resource pointLegendFile = null;
    private PaoGroupsWrapper paoGroupsWrapper = null;
    private String javaConstantClassName = null;
    private UnitMeasureDao unitMeasureDao = null;
    private StateDao stateDao = null;
    private Logger log = YukonLogManager.getLogger(DeviceDefinitionDaoImpl.class);

    // Maps containing all of the data in the deviceDefinition.xml file

    // Map<DeviceType, Map<Attribute, AttributeDefinition>>
    private Map<Integer, Map<Attribute, AttributeLookup>> deviceAttributeAttrDefinitionMap = null;

    // Map<DeviceType, List<PointTemplate>>
    private Map<Integer, Set<PointTemplate>> deviceAllPointTemplateMap = null;

    // Map<DeviceType, DeviceDefinition>
    private Map<Integer, DeviceDefinition> deviceTypeMap = null;

    // Map<DeviceDisplayGroup, List<DeviceDefinition>
    private Map<String, List<DeviceDefinition>> deviceDisplayGroupMap = null;

    // Map<ChangeGroup, Set<DeviceDefinition>
    private Map<String, Set<DeviceDefinition>> changeGroupDevicesMap = null;

    // Map<DeviceType, Set<CommandDefinition>
    private Map<Integer, Set<CommandDefinition>> deviceCommandMap = null;
    
    private Resource currentDefinitionResource;

    public void setInputFile(Resource inputFile) {
        this.inputFile = inputFile;
    }
    
    public void setSchemaFile(Resource schemaFile) {
        this.schemaFile = schemaFile;
    }
    
    public void setCustomInputFile(File customInputFile) {
        this.customInputFile = customInputFile;
    }
    
    public void setPaoGroupsWrapper(PaoGroupsWrapper paoGroupsWrapper) {
        this.paoGroupsWrapper = paoGroupsWrapper;
    }

    public void setJavaConstantClassName(String javaConstantClassName) {
        this.javaConstantClassName = javaConstantClassName;
    }

    public void setStateDao(StateDao stateDao) {
        this.stateDao = stateDao;
    }

    public void setUnitMeasureDao(UnitMeasureDao unitMeasureDao) {
        this.unitMeasureDao = unitMeasureDao;
    }
    public void setPointLegendFile(Resource pointLegendFile) {
        this.pointLegendFile = pointLegendFile;
    }
    
    public Set<Attribute> getAvailableAttributes(YukonDevice device) {
        int deviceType = device.getType();

        if (this.deviceAttributeAttrDefinitionMap.containsKey(deviceType)) {
            Map<Attribute, AttributeLookup> attributeMap = this.deviceAttributeAttrDefinitionMap.get(deviceType);
            return Collections.unmodifiableSet(attributeMap.keySet());
        } else {
            throw new IllegalArgumentException("Device type '" + deviceType
                    + "' is not supported.");
        }
    }

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

    public Map<String, List<DeviceDefinition>> getDeviceDisplayGroupMap() {
        return Collections.unmodifiableMap(this.deviceDisplayGroupMap);
    }

    public Set<DeviceDefinition> getChangeableDevices(DeviceDefinition deviceDefinition) {

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
    
    public Set<CommandDefinition> getAffected(YukonDevice device, Set<? extends DevicePointIdentifier> pointSet) {

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

    /**
     * Method to initialize the AttributeDao. This method will read the
     * deviceAttributes from the xml file and load the appropriate point
     * template maps
     */
    public void initialize() throws Exception {

        this.deviceTypeMap = new HashMap<Integer, DeviceDefinition>();
        this.deviceAllPointTemplateMap = new HashMap<Integer, Set<PointTemplate>>();
        this.deviceDisplayGroupMap = new LinkedHashMap<String, List<DeviceDefinition>>();
        this.changeGroupDevicesMap = new HashMap<String, Set<DeviceDefinition>>();
        this.deviceAttributeAttrDefinitionMap = new HashMap<Integer, Map<Attribute, AttributeLookup>>();
        this.deviceCommandMap = new HashMap<Integer, Set<CommandDefinition>>();

        if (customInputFile != null && customInputFile.exists()) {
            currentDefinitionResource = new FileSystemResource(customInputFile);

        } else {
            currentDefinitionResource = inputFile;
        }
        
        // make sure the file validates
        validateXmlSchema(currentDefinitionResource);

        InputStreamReader reader = new InputStreamReader(currentDefinitionResource.getInputStream());
        
        try {

            // Use castor to parse the xml file
            DeviceDefinitions definition = (DeviceDefinitions) Unmarshaller.unmarshal(DeviceDefinitions.class,
                                                                                      reader);

            // Add each device in the xml into the device maps
            for (Device device : definition.getDevice()) {
                this.validateDeviceConstant(device);
                this.addDevice(device);
            }
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                // Do nothing - tried to close
            }
        }
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
        Set<PointTemplate> templateSet = new HashSet<PointTemplate>();

        if (this.deviceAllPointTemplateMap.containsKey(deviceType)) {
            Set<PointTemplate> allTemplateSet = this.deviceAllPointTemplateMap.get(deviceType);
            for (PointTemplate template : allTemplateSet) {
                if (template.isShouldInitialize()) {
                    templateSet.add(template);
                }
            }
        } else {
            throw new IllegalArgumentException("Device type '"
                    + paoGroupsWrapper.getPAOTypeString(deviceType) + "' is not supported.");
        }

        return templateSet;
    }

    private void validateDeviceConstant(Device device) throws Exception {

        // Validate xml device type int / java constant
        String javaConstant = device.getType().getJavaConstant();
        int deviceType = device.getType().getValue();
        int constantValue = ReflectivePropertySearcher.getIntForFQN(javaConstantClassName,
                                                                    javaConstant);
        if (deviceType != constantValue) {
            throw new Exception("Java constant value for " + javaConstant + ": " + constantValue
                    + " does not match value in deviceDefinition.xml file " + deviceType);
        }

    }

    /**
     * Helper method to add the device and its point templates to the
     * appropriate maps
     * @param device - Device to add
     */
    private void addDevice(Device device) {

        int deviceType = device.getType().getValue();
        String javaConstant = device.getType().getJavaConstant();

        String displayName = null;
        if (device.getDisplayName() != null) {
            displayName = device.getDisplayName().getValue();
        }
        String group = null;
        if (device.getDisplayGroup() != null) {
            group = device.getDisplayGroup().getValue();
        }

        DeviceDefinition deviceDefinition = new DeviceDefinitionImpl(deviceType,
                                                                     displayName,
                                                                     group,
                                                                     javaConstant,
                                                                     device.getType()
                                                                           .getChangeGroup());

        // Add deviceDefinition to type map
        this.deviceTypeMap.put(deviceType, deviceDefinition);

        // Add deviceDefinition to change group map
        if (device.getType().getChangeGroup() != null) {
            String changeGroup = device.getType().getChangeGroup();

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

        Map<String, PointTemplate> pointNameTemplateMap = new HashMap<String, PointTemplate>();

        if (device.getPoints() != null) {
            Point[] points = device.getPoints().getPoint();
            for (Point point : points) {
                PointTemplate template = this.createPointTemplate(point);
                pointSet.add(template);

                if (pointNameTemplateMap.containsKey(template.getName())) {
                    throw new RuntimeException("Point name: " + template.getName() + " is used twice for device type: " + javaConstant + " in the deviceDefinition.xml file - point names must be unique within a device type");
                }
                pointNameTemplateMap.put(template.getName(), template);
            }
        }
        this.deviceAllPointTemplateMap.put(deviceType, pointSet);
        
        Map<Attribute, AttributeLookup> attributeMap = new HashMap<Attribute, AttributeLookup>();
        if (device.getAttributes() != null) {
            com.cannontech.common.device.definition.model.castor.Attribute[] attributes = device.getAttributes().getAttribute();
            for (com.cannontech.common.device.definition.model.castor.Attribute attribute : attributes) {
            	
            	AttributeLookup attributeDefinition = 
            		createAttributeDefinition(attribute.getName(), attribute.getChoiceValue());
            	Attribute pointAttribute = BuiltInAttribute.valueOf(attribute.getName());
            	
                if (attributeMap.containsKey(pointAttribute)) {
                    throw new RuntimeException("Attribute: " + attribute.getName() + " is used twice for device type: " + javaConstant + " in the deviceDefinition.xml file - attribute names must be unique within a device type");
                }
            	attributeMap.put(pointAttribute, attributeDefinition);
            }
        }        
        this.deviceAttributeAttrDefinitionMap.put(deviceType, attributeMap);

        // Add device commands
        Set<CommandDefinition> commandSet = new HashSet<CommandDefinition>();
        if (device.getCommands() != null) {
            Command[] commands = device.getCommands().getCommand();
            for (Command command : commands) {
                CommandDefinition definition = this.createCommandDefinition(device.getDisplayName().getValue(), 
                                                                            command,
                                                                            pointNameTemplateMap);
                commandSet.add(definition);
            }
        }
        this.deviceCommandMap.put(deviceType, commandSet);
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
                throw new RuntimeException("Point name: " + pointRef.getName() + " not found for device: " + deviceName + ".  command pointRefs must reference a point name from the same device in the deviceDefinition.xml file.");
            }
            PointTemplate template = pointNameTemplateMap.get(pointRef.getName());
            DevicePointIdentifier dpi = new DevicePointIdentifier(template.getType(), template.getOffset());
            definition.addAffectedPoint(dpi);
        }

        return definition;
    }

    /**
     * Helper method to convert a castor point to a point template
     * @param point - Point to convert
     * @return The point template representing the castor point
     */
    private PointTemplate createPointTemplate(Point point) {

        PointTemplate template = new PointTemplate(PointTypes.getType(point.getType()), point.getOffset().getValue());

        template.setName(point.getName());
        template.setShouldInitialize(point.getInit());

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

    public Resource getCurrentDefinitionResource() {
        return currentDefinitionResource;
    }

}
