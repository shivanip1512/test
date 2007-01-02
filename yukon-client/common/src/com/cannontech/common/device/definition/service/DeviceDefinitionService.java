package com.cannontech.common.device.definition.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.cannontech.common.device.definition.model.DeviceDefinition;
import com.cannontech.common.device.definition.model.PointTemplate;
import com.cannontech.database.data.device.DeviceBase;
import com.cannontech.database.data.point.PointBase;

/**
 * Class which provides functionality for manipulating devices based on their
 * definition
 */
public interface DeviceDefinitionService {

    /**
     * Method to create all of the default points for the given device. NOTE:
     * this will create the points in memory ONLY - the default points will NOT
     * be persisted
     * @param device - Device to create points for
     * @return A list of the default points for the device
     */
    public abstract List<PointBase> createDefaultPointsForDevice(DeviceBase device);

    /**
     * Method to create all of the points for the given device. NOTE: this will
     * create the points in memory ONLY - the points will NOT be persisted
     * @param device - Device to create points for
     * @return A list of all the points for the device
     */
    public abstract List<PointBase> createAllPointsForDevice(DeviceBase device);

    /**
     * Method to get a map of device display groups and their associated device
     * types
     * @return Map with key: display group name, value: list of device display
     */
    public abstract Map<String, List<DeviceDefinition>> getDeviceDisplayGroupMap();

    /**
     * Method used to determine if a device can have its type changed
     * @param device - Device to change
     * @return True if the device's type can be changed
     */
    public abstract boolean isDeviceTypeChangeable(DeviceBase device);

    /**
     * Method to get a set of device definitions for devices that the given
     * device can be changed into
     * @param device - Device to change
     * @return A set of device definitions that the given device can change into
     */
    public abstract Set<DeviceDefinition> getChangeableDevices(DeviceBase device);

    /**
     * Method to get a set of point templates that will be added to the given
     * device if its type is changed to the given device definition
     * @param device - Device to change type
     * @param deviceDefinition - Definition of type to change to
     * @return Set of points that will be added to the device
     */
    public abstract Set<PointTemplate> getPointTemplatesToAdd(DeviceBase device,
            DeviceDefinition deviceDefinition);

    /**
     * Method to get a set of points that will be removed from the given device
     * if its type is changed to the given device definition
     * @param device - Device to change type
     * @param deviceDefinition - Definition of type to change to
     * @return Set of points that will be removed from the device
     */
    public abstract Set<PointTemplate> getPointTemplatesToRemove(DeviceBase device,
            DeviceDefinition deviceDefinition);
}