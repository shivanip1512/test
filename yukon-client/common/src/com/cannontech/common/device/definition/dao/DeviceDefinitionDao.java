package com.cannontech.common.device.definition.dao;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.cannontech.common.device.attribute.model.Attribute;
import com.cannontech.common.device.definition.model.DeviceDisplay;
import com.cannontech.common.device.definition.model.PointTemplate;
import com.cannontech.database.data.device.DeviceBase;

/**
 * Data access object for device definition information
 */
public interface DeviceDefinitionDao {

    /**
     * Method to get a set of attributes defined for a given device
     * @param device - Device to get attributes for
     * @return A set of attributes for the device
     * @throws IllegalArgumentException - If the device is not supported
     */
    public abstract Set<Attribute> getAvailableAttributes(DeviceBase device);

    /**
     * Method to get all of the point templates for a given device
     * @param device - Device to get point templates for
     * @return A set of all point templates for the device
     */
    public abstract Set<PointTemplate> getAllPointTemplates(DeviceBase device);

    /**
     * Method to get all of the point templates for a given device that should
     * be initialized
     * @param device - Device to get point templates for
     * @return A set of all point templates for the device that should be
     *         initialized
     */
    public abstract Set<PointTemplate> getInitPointTemplates(DeviceBase device);

    /**
     * Method to get a map of device display groups and their associated device
     * types
     * @return Map with key: display group name, value: list of device display
     */
    public abstract Map<String, List<DeviceDisplay>> getDeviceDisplayGroupMap();
}