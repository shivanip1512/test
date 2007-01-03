package com.cannontech.common.device.definition.dao;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.cannontech.common.device.attribute.model.Attribute;
import com.cannontech.common.device.definition.model.DeviceDefinition;
import com.cannontech.common.device.definition.model.PointTemplate;
import com.cannontech.database.data.device.DeviceBase;

/**
 * Data access object for device definition information
 */
public interface DeviceDefinitionDao {

    /**
     * Method to get a set of attributes defined for a given device
     * @param device - Device to get attributes for
     * @return An immutable set of attributes for the device
     * @throws IllegalArgumentException - If the device is not supported
     */
    public abstract Set<Attribute> getAvailableAttributes(DeviceBase device);

    /**
     * Method to get the point template for a given device and attribute
     * @param device - Device to get point template for
     * @param attribute - Attribute to get point template for
     * @return
     */
    public abstract PointTemplate getPointTemplateForAttribute(DeviceBase device,
            Attribute attribute);

    /**
     * Method to get all of the point templates for a given device
     * @param device - Device to get point templates for
     * @return A set of all point templates for the device (returns a new copy
     *         each time the method is called)
     */
    public abstract Set<PointTemplate> getAllPointTemplates(DeviceBase device);

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
    public abstract Set<PointTemplate> getInitPointTemplates(DeviceBase device);

    /**
     * Method to get all of the point templates for a given device definition
     * that should be initialized
     * @param newDefinition - Device definition to get point templates for
     * @return A set of all point templates for the device that should be
     *         initialized (returns a new copy each time the method is called)
     */
    public abstract Set<PointTemplate> getInitPointTemplates(DeviceDefinition newDefinition);

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
    public abstract DeviceDefinition getDeviceDefinition(DeviceBase device);

    /**
     * Method to get a set of device types that the given device can change into
     * @param deviceDefinition - Definition of device to change
     * @return A set of device definitions (returns a new copy each time the
     *         method is called)
     */
    public abstract Set<DeviceDefinition> getChangeableDevices(DeviceDefinition deviceDefinition);

}