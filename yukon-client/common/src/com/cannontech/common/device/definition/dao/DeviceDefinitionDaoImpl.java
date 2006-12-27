package com.cannontech.common.device.definition.dao;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.exolab.castor.xml.Unmarshaller;

import com.cannontech.common.device.attribute.model.Attribute;
import com.cannontech.common.device.definition.model.DeviceDefinition;
import com.cannontech.common.device.definition.model.PointTemplate;
import com.cannontech.common.device.definition.model.castor.Device;
import com.cannontech.common.device.definition.model.castor.DeviceDefinitions;
import com.cannontech.common.device.definition.model.castor.Point;
import com.cannontech.database.data.device.DeviceBase;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.database.data.point.PointUnits;
import com.cannontech.database.db.state.StateGroupUtils;
import com.cannontech.util.ReflectivePropertySearcher;

/**
 * Implementation class for DeviceDefinitionDao
 */
public class DeviceDefinitionDaoImpl implements DeviceDefinitionDao {

    private String inputFile = null;

    // Map<DeviceType, Map<Attribute, PointTemplate>>
    private Map<Integer, Map<Attribute, PointTemplate>> deviceAttributePointTemplateMap = null;

    // Map<DeviceType, List<PointTemplate>>
    private Map<Integer, Set<PointTemplate>> deviceAllPointTemplateMap = null;

    // Map<DeviceType, DeviceDefinition>
    private Map<Integer, DeviceDefinition> deviceTypeMap = null;

    // <Map<DeviceDisplayGroup, List<DeviceDisplay>>
    private Map<String, List<DeviceDefinition>> deviceDisplayGroupMap = null;

    public void setInputFile(String inputFile) {
        this.inputFile = inputFile;
    }

    public Set<Attribute> getAvailableAttributes(DeviceBase device) {

        Integer deviceType = PAOGroups.getDeviceType(device.getPAOType());
        if (this.deviceAttributePointTemplateMap.containsKey(deviceType)) {
            Map<Attribute, PointTemplate> attributeMap = this.deviceAttributePointTemplateMap.get(deviceType);
            return attributeMap.keySet();
        } else {
            throw new IllegalArgumentException("Device type '" + device.getPAOType()
                    + "' is not supported.");
        }

    }

    public PointTemplate getPointTemplateForAttribute(DeviceBase device, Attribute attribute) {

        Integer deviceType = PAOGroups.getDeviceType(device.getPAOType());
        if (this.deviceAttributePointTemplateMap.containsKey(deviceType)) {
            Map<Attribute, PointTemplate> attributeMap = this.deviceAttributePointTemplateMap.get(deviceType);
            if (attributeMap.containsKey(attribute)) {
                return attributeMap.get(attribute);
            } else {
                throw new IllegalArgumentException("Attribute " + attribute.getKey()
                        + " is not supported for Device type '" + deviceType + "'");
            }
        } else {
            throw new IllegalArgumentException("Device type '" + device.getPAOType()
                    + "' is not supported.");
        }
    }

    public Set<PointTemplate> getAllPointTemplates(DeviceBase device) {

        Integer deviceType = PAOGroups.getDeviceType(device.getPAOType());
        if (this.deviceAllPointTemplateMap.containsKey(deviceType)) {
            return this.deviceAllPointTemplateMap.get(deviceType);
        } else {
            throw new IllegalArgumentException("Device type '" + device.getPAOType()
                    + "' is not supported.");
        }
    }

    public Set<PointTemplate> getInitPointTemplates(DeviceBase device) {

        Set<PointTemplate> templateSet = new HashSet<PointTemplate>();

        Integer deviceType = PAOGroups.getDeviceType(device.getPAOType());
        if (this.deviceAllPointTemplateMap.containsKey(deviceType)) {
            Set<PointTemplate> allTemplateSet = this.deviceAllPointTemplateMap.get(deviceType);
            for (PointTemplate template : allTemplateSet) {
                if (template.isShouldInitialize()) {
                    templateSet.add(template);
                }
            }
        } else {
            throw new IllegalArgumentException("Device type '" + device.getPAOType()
                    + "' is not supported.");
        }

        return templateSet;
    }

    public Map<String, List<DeviceDefinition>> getDeviceDisplayGroupMap() {
        return this.deviceDisplayGroupMap;
    }

    public boolean isDeviceTypeChangeable(DeviceBase device) {

        Integer deviceType = PAOGroups.getDeviceType(device.getPAOType());
        if (this.deviceTypeMap.containsKey(deviceType)) {
            DeviceDefinition deviceDefinition = this.deviceTypeMap.get(deviceType);
            return deviceDefinition.isChangeable();
        } else {
            throw new IllegalArgumentException("Device type '" + device.getPAOType()
                    + "' is not supported.");
        }

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
        this.deviceAttributePointTemplateMap = new HashMap<Integer, Map<Attribute, PointTemplate>>();

        InputStreamReader reader = null;
        try {

            URL resource = this.getClass().getClassLoader().getResource(inputFile);
            reader = new InputStreamReader(resource.openStream());

            // Use castor to parse the xml file
            DeviceDefinitions definition = (DeviceDefinitions) Unmarshaller.unmarshal(DeviceDefinitions.class,
                                                                                      reader);

            // Add each device in the xml into the device maps
            for (Device device : definition.getDevice()) {
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

    /**
     * Helper method to add the device and it's point templates to the
     * appropriate maps
     * @param device - Device to add
     */
    private void addDevice(Device device) {

        // Validate xml device type int / java constant
        String javaConstant = device.getType().getJavaConstant();
        int deviceType = device.getType().getValue();
        try {
            int constantValue = ReflectivePropertySearcher.getIntForFQN("com.cannontech.database.data.pao.DeviceTypes."
                    + javaConstant);
            if (deviceType != constantValue) {
                throw new Exception("Java constant value for '" + javaConstant + "' ("
                        + constantValue + ") does not match value in xml file (" + deviceType + ")");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        if (device.getDisplayName() != null) {
            String displayName = device.getDisplayName().getValue();
            String group = null;
            if (device.getDisplayGroup() != null) {
                group = device.getDisplayGroup().getValue();
            }

            DeviceDefinition deviceDefinition = new DeviceDefinition(deviceType,
                                                                     displayName,
                                                                     group,
                                                                     javaConstant,
                                                                     device.getType()
                                                                           .getChangeable());

            // Add deviceDefinition to type map
            this.deviceTypeMap.put(deviceType, deviceDefinition);

            // Add deviceDisplayName to group map
            if (group != null) {
                List<DeviceDefinition> typeList = this.deviceDisplayGroupMap.get(group);
                if (typeList == null) {
                    typeList = new ArrayList<DeviceDefinition>();
                    this.deviceDisplayGroupMap.put(group, typeList);
                }

                typeList.add(deviceDefinition);
            }
        }

        // Add device points
        Map<Attribute, PointTemplate> pointMap = new HashMap<Attribute, PointTemplate>();
        Set<PointTemplate> pointSet = new HashSet<PointTemplate>();

        if (device.getPoints() != null) {
            Point[] points = device.getPoints().getPoint();
            for (Point point : points) {
                PointTemplate template = this.createPointTemplate(point);
                if (template.getAttribute() != null) {
                    pointMap.put(template.getAttribute(), template);
                }
                pointSet.add(template);
            }
        }

        this.deviceAllPointTemplateMap.put(deviceType, pointSet);
        this.deviceAttributePointTemplateMap.put(deviceType, pointMap);

    }

    /**
     * Helper method to convert a castor point to a point template
     * @param point - Point to convert
     * @return The point template representing the castor point
     */
    private PointTemplate createPointTemplate(Point point) {

        PointTemplate template = new PointTemplate();

        if (point.getAttribute() != null) {
            String attributeName = point.getAttribute().getName();
            template.setAttribute(new Attribute(attributeName, attributeName));
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
                unitOfMeasure = point.getPointChoice()
                                     .getPointChoiceSequence()
                                     .getUnitofmeasure()
                                     .getValue();
            }
        }
        template.setMultiplier(multiplier);
        template.setUnitOfMeasure(unitOfMeasure);

        int stateGroupId = StateGroupUtils.SYSTEM_STATEGROUPID;
        if (point.getPointChoice().getStategroup() != null) {
            stateGroupId = point.getPointChoice().getStategroup().getValue();
        }
        template.setStateGroupId(stateGroupId);

        return template;
    }
}
