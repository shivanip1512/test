package com.cannontech.common.device.definition.dao;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.exolab.castor.xml.Unmarshaller;
import org.springframework.dao.DataAccessException;

import com.cannontech.common.device.attribute.model.Attribute;
import com.cannontech.common.device.attribute.model.BuiltInAttribute;
import com.cannontech.common.device.definition.model.CommandDefinition;
import com.cannontech.common.device.definition.model.CommandDefinitionImpl;
import com.cannontech.common.device.definition.model.DeviceDefinition;
import com.cannontech.common.device.definition.model.DeviceDefinitionImpl;
import com.cannontech.common.device.definition.model.PointReference;
import com.cannontech.common.device.definition.model.PointTemplate;
import com.cannontech.common.device.definition.model.PointTemplateImpl;
import com.cannontech.common.device.definition.model.castor.Cmd;
import com.cannontech.common.device.definition.model.castor.Command;
import com.cannontech.common.device.definition.model.castor.Device;
import com.cannontech.common.device.definition.model.castor.DeviceDefinitions;
import com.cannontech.common.device.definition.model.castor.Point;
import com.cannontech.common.device.definition.model.castor.PointRef;
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

    private InputStream inputFile = null;
    private PaoGroupsWrapper paoGroupsWrapper = null;
    private String javaConstantClassName = null;
    private UnitMeasureDao unitMeasureDao = null;
    private StateDao stateDao = null;

    // Maps containing all of the data in the deviceDefinition.xml file

    // Map<DeviceType, Map<Attribute, PointTemplate>>
    private Map<Integer, Map<Attribute, PointTemplate>> deviceAttributePointTemplateMap = null;

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

    public void setInputFile(InputStream inputFile) {
        this.inputFile = inputFile;
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

    public Set<Attribute> getAvailableAttributes(LiteYukonPAObject device) {

        Integer deviceType = device.getType();
        if (this.deviceAttributePointTemplateMap.containsKey(deviceType)) {
            Map<Attribute, PointTemplate> attributeMap = this.deviceAttributePointTemplateMap.get(deviceType);
            return Collections.unmodifiableSet(attributeMap.keySet());
        } else {
            throw new IllegalArgumentException("Device type '" + device.getType()
                    + "' is not supported.");
        }

    }

    public PointTemplate getPointTemplateForAttribute(LiteYukonPAObject device, Attribute attribute) {

        Integer deviceType = device.getType();
        if (this.deviceAttributePointTemplateMap.containsKey(deviceType)) {
            Map<Attribute, PointTemplate> attributeMap = this.deviceAttributePointTemplateMap.get(deviceType);
            if (attributeMap.containsKey(attribute)) {
                return attributeMap.get(attribute);
            } else {
                throw new IllegalArgumentException("Attribute " + attribute.getKey()
                        + " is not supported for Device type '" + deviceType + "'");
            }
        } else {
            throw new IllegalArgumentException("Device type '" + device.getType()
                    + "' is not supported.");
        }
    }

    public Set<PointTemplate> getAllPointTemplates(LiteYukonPAObject device) {
        Integer deviceType = device.getType();
        return this.getAllPointTemplates(deviceType);
    }

    public Set<PointTemplate> getAllPointTemplates(DeviceDefinition deviceDefinition) {
        return this.getAllPointTemplates(deviceDefinition.getType());
    }

    public Set<PointTemplate> getInitPointTemplates(LiteYukonPAObject device) {
        Integer deviceType = device.getType();
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

    public DeviceDefinition getDeviceDefinition(LiteYukonPAObject device) {

        Integer deviceType = device.getType();
        if (this.deviceTypeMap.containsKey(deviceType)) {
            return this.deviceTypeMap.get(deviceType);
        } else {
            throw new IllegalArgumentException("Device type '" + device.getType()
                    + "' is not supported.");
        }

    }

    public Set<CommandDefinition> getAffected(LiteYukonPAObject device, Set<PointTemplate> pointSet) {

        Set<CommandDefinition> commandSet = new HashSet<CommandDefinition>();

        Integer deviceType = device.getType();
        Set<CommandDefinition> allCommandSet = this.deviceCommandMap.get(deviceType);

        for (CommandDefinition command : allCommandSet) {
            for (PointTemplate point : pointSet) {
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
        this.deviceAttributePointTemplateMap = new HashMap<Integer, Map<Attribute, PointTemplate>>();
        this.deviceCommandMap = new HashMap<Integer, Set<CommandDefinition>>();

        InputStreamReader reader = null;
        try {

            reader = new InputStreamReader(inputFile);

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
     * Helper method to add the device and it's point templates to the
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
        Map<Attribute, PointTemplate> pointMap = new HashMap<Attribute, PointTemplate>();
        Set<PointTemplate> pointSet = new HashSet<PointTemplate>();

        Map<String, PointTemplate> pointNameTemplateMap = new HashMap<String, PointTemplate>();

        if (device.getPoints() != null) {
            Point[] points = device.getPoints().getPoint();
            for (Point point : points) {
                PointTemplate template = this.createPointTemplate(point);
                if (template.getAttribute() != null) {
                    pointMap.put(template.getAttribute(), template);
                }
                pointSet.add(template);

                if (pointNameTemplateMap.containsKey(template.getName())) {
                    throw new RuntimeException("Point name: " + template.getName() + " is used twice for device type: " + javaConstant + " in the deviceDefinition.xml file - point names must be unique within a device type");
                }
                pointNameTemplateMap.put(template.getName(), template);
            }
        }
        this.deviceAllPointTemplateMap.put(deviceType, pointSet);
        this.deviceAttributePointTemplateMap.put(deviceType, pointMap);

        // Add device commands
        Set<CommandDefinition> commandSet = new HashSet<CommandDefinition>();
        if (device.getCommands() != null) {
            Command[] commands = device.getCommands().getCommand();
            for (Command command : commands) {
                CommandDefinition definition = this.createCommandDefinition(command,
                                                                            pointNameTemplateMap);
                commandSet.add(definition);
            }
        }
        this.deviceCommandMap.put(deviceType, commandSet);
    }

    /**
     * Helper method to convert a castor command to a command definition
     * @param command - Command to convert
     * @param pointNameTemplateMap
     * @return The command definition representing the castor command
     */
    private CommandDefinition createCommandDefinition(Command command,
            Map<String, PointTemplate> pointNameTemplateMap) {

        CommandDefinition definition = new CommandDefinitionImpl();

        // Add command text
        Cmd[] cmds = command.getCmd();
        for (Cmd cmd : cmds) {
            definition.addCommandString(cmd.getText());
        }

        // Add point reference
        PointRef[] pointRefs = command.getPointRef();
        for (PointRef pointRef : pointRefs) {
            if (!pointNameTemplateMap.containsKey(pointRef.getName())) {
                throw new RuntimeException("Point name: " + pointRef.getName() + " not found.  command pointRefs must reference a point name from the same device in the deviceDefinition.xml file.");
            }
            PointReference pointReference = new PointReference();
            pointReference.setPointName(pointRef.getName());
            definition.addAffectedPoint(pointReference);
        }

        return definition;
    }

    /**
     * Helper method to convert a castor point to a point template
     * @param point - Point to convert
     * @return The point template representing the castor point
     */
    private PointTemplate createPointTemplate(Point point) {

        PointTemplateImpl template = new PointTemplateImpl();

        if (point.getAttribute() != null) {
            String attributeName = point.getAttribute().getName();
            template.setAttribute(BuiltInAttribute.valueOf(attributeName));
        }

        template.setType(PointTypes.getType(point.getType()));
        template.setName(point.getName());
        template.setOffset(point.getOffset().getValue());
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
                            + unitOfMeasureName + ". Check the deviceDefinition.xml file ");
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
                        + ". Check the deviceDefinition.xml file ");
            }
            stateGroupId = stateGroup.getStateGroupID();
        }
        template.setStateGroupId(stateGroupId);

        return template;
    }
}
