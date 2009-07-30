package com.cannontech.common.device.definition.dao;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.cannontech.common.device.attribute.model.BuiltInAttribute;
import com.cannontech.common.device.definition.attribute.lookup.AttributeDefinition;
import com.cannontech.common.device.definition.model.CommandDefinition;
import com.cannontech.common.device.definition.model.DeviceDefinition;
import com.cannontech.common.device.definition.model.DeviceTag;
import com.cannontech.common.device.definition.model.PointIdentifier;
import com.cannontech.common.device.definition.model.PointTemplate;
import com.cannontech.common.pao.PaoType;

/**
 * Data access object for device definition information
 */
public interface DeviceDefinitionDao {

	// ATTRIBUTES
	//============================================
    public abstract Set<AttributeDefinition> getDefinedAttributes(PaoType deviceType);
    
    public abstract AttributeDefinition getAttributeLookup(PaoType deviceType, BuiltInAttribute attribute);

    // POINTS
    //============================================

    /**
     * Method to get all of the point templates for a given device
     * @param device - Device to get point templates for
     * @return A set of all point templates for the device (returns a new copy
     *         each time the method is called)
     */
    public abstract Set<PointTemplate> getAllPointTemplates(PaoType deviceType);

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
    public abstract Set<PointTemplate> getInitPointTemplates(PaoType deviceType);

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
    public abstract PointTemplate getPointTemplateByTypeAndOffset(PaoType deviceType, PointIdentifier pointIdentifier);

    // COMMANDS
    //============================================
    /**
     * Method to get a list of command definitions for the given device which
     * affect one or more of the points in the given set of points
     * @param device - Device to get commands for
     * @param pointSet - Set of points to get affecting commands for
     * @return The set of commands affecting one or more of the points
     */
    public Set<CommandDefinition> getCommandsThatAffectPoints(PaoType deviceType, Set<? extends PointIdentifier> pointSet);
    
    public Set<CommandDefinition> getAvailableCommands(DeviceDefinition newDefinition);
    
    // TAGS
    //============================================
    public abstract Set<DeviceTag> getSupportedTags(PaoType deviceType);
    public abstract Set<DeviceTag> getSupportedTags(DeviceDefinition deviceDefiniton);
    
    public abstract Set<DeviceDefinition> getDevicesThatSupportTag(DeviceTag feature);
    
    public abstract boolean isTagSupported(PaoType deviceType, DeviceTag feature);
    public abstract boolean isTagSupported(DeviceDefinition deviceDefiniton, DeviceTag feature);
    
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
    public abstract DeviceDefinition getDeviceDefinition(PaoType deviceType);
    
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