package com.cannontech.common.device.definition.dao;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.device.attribute.model.Attribute;
import com.cannontech.common.device.definition.model.CommandDefinition;
import com.cannontech.common.device.definition.model.DeviceDefinition;
import com.cannontech.common.device.definition.model.DeviceFeature;
import com.cannontech.common.device.definition.model.DevicePointIdentifier;
import com.cannontech.common.device.definition.model.PointTemplate;
import com.cannontech.database.data.lite.LiteYukonPAObject;

/**
 * Data access object for device definition information
 */
public interface DeviceDefinitionDao {

	// ATTRIBUTES
	//============================================
    /**
     * Method to get a set of attributes defined for a given device
     * @param device - Device to get attributes for
     * @return An immutable set of attributes for the device
     * @throws IllegalArgumentException - If the device is not supported
     */
    public abstract Set<Attribute> getAvailableAttributes(YukonDevice meter);
    public abstract Set<Attribute> getAvailableAttributes(DeviceDefinition deviceDefinition);

    // POINTS
    //============================================
    /**
     * Method to get a set of point templates for a given device and set of attributes
     * @param device - Device to get set of point templates for
     * @param attributes - Attributes to get set of point templates for
     * @return The Set of DevicePointIdentifier for the device and Attribute Set
     */
    @Deprecated
    public abstract Set<DevicePointIdentifier> getDevicePointIdentifierForAttributes(YukonDevice device, Set<? extends Attribute> attributes);
    
    /**
     * Method to get the point template for a given device and attribute
     * @param device - Device to get point template for
     * @param attribute - Attribute to get point template for
     * @return The PointTemplate for the device and Attribute
     */
    public abstract PointTemplate getPointTemplateForAttribute(YukonDevice device,
            Attribute attribute);

    /**
     * Method to get all of the point templates for a given device
     * @param device - Device to get point templates for
     * @return A set of all point templates for the device (returns a new copy
     *         each time the method is called)
     */
    public abstract Set<PointTemplate> getAllPointTemplates(YukonDevice device);

    /**
     * Method to get all of the point templates for a given device definition
     * @param deviceDefiniton - Device definition to get point templates for
     * @return A set of all point templates for the device (returns a new copy
     *         each time the method is called)
     */
    public abstract Set<PointTemplate> getAllPointTemplates(DeviceDefinition deviceDefiniton);

    /**
     * Method to get all of the point templates for a given device that should
     * be initialized
     * @param device - Device to get point templates for
     * @return A set of all point templates for the device that should be
     *         initialized (returns a new copy each time the method is called)
     */
    public abstract Set<PointTemplate> getInitPointTemplates(YukonDevice device);

    /**
     * Method to get all of the point templates for a given device definition
     * that should be initialized
     * @param newDefinition - Device definition to get point templates for
     * @return A set of all point templates for the device that should be
     *         initialized (returns a new copy each time the method is called)
     */
    public abstract Set<PointTemplate> getInitPointTemplates(DeviceDefinition newDefinition);
    
    /**
     * Method to get a point template for a device based on point type and offset
     * @param device - Device to get point template for
     * @param offset - Offset of point template
     * @param pointType - Type of point template
     * @return Point template for device
     */
    public abstract PointTemplate getPointTemplateByTypeAndOffset(YukonDevice device, Integer offset, Integer pointType);

    // COMMANDS
    //============================================
    /**
     * Method to get a list of command definitions for the given device which
     * affect one or more of the points in the given set of points
     * @param device - Device to get commands for
     * @param pointSet - Set of points to get affecting commands for
     * @return The set of commands affecting one or more of the points
     */
    public Set<CommandDefinition> getCommandsThatAffectPoints(YukonDevice device, Set<? extends DevicePointIdentifier> pointSet);
    
    public Set<CommandDefinition> getAvailableCommands(DeviceDefinition newDefinition);
    
    // FEATURES
    //============================================
    public abstract Set<DeviceFeature> getSupportedFeatures(YukonDevice device);
    public abstract Set<DeviceFeature> getSupportedFeatures(DeviceDefinition deviceDefiniton);
    
    public abstract Set<DeviceDefinition> getDevicesThatSupportFeature(DeviceFeature feature);
    
    public abstract boolean isFeatureSupported(YukonDevice device, DeviceFeature feature);
    public abstract boolean isFeatureSupported(DeviceDefinition deviceDefiniton, DeviceFeature feature);
    public abstract boolean isFeatureSupported(LiteYukonPAObject litePao, DeviceFeature feature);
    
    // DEFINITIONS
    //============================================
    public abstract Set<DeviceDefinition> getAllDeviceDefinitions();
    
    /**
     * Method to get a map of device display groups and their associated device
     * types
     * @return An immutable map with key: display group name, value: list of
     *         device display
     */
    public abstract Map<String, List<DeviceDefinition>> getDeviceDisplayGroupMap();

    /**
     * Method used to get a device definition for a device
     * @param device - Device to get definition for
     * @return The device's device definition
     */
    public abstract DeviceDefinition getDeviceDefinition(YukonDevice device);
    
    /**
     * Method used to get a device definition for a type
     * @param deviceType - Type to get definition for
     * @return The type's device definition
     */
    public abstract DeviceDefinition getDeviceDefinition(int deviceType);

    /**
     * Method to get a set of device types that the given device can change into
     * @param deviceDefinition - Definition of device to change
     * @return A set of device definitions (returns a new copy each time the
     *         method is called)
     */
    //TODO rename me
    public abstract Set<DeviceDefinition> getDevicesThatDeviceCanChangeTo(DeviceDefinition deviceDefinition);
    
    // MISC
    //============================================
    public String getPointLegendHtml(String displayGroup);

}