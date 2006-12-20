package com.cannontech.common.device.definition.service;

import java.util.List;
import java.util.Map;

import com.cannontech.common.device.definition.model.DeviceDisplay;
import com.cannontech.database.data.device.DeviceBase;
import com.cannontech.database.data.point.PointBase;

/**
 * Class which provides functionality for manipulating devices based on their
 * definition
 */
public interface DeviceDefinitionService {

    /**
     * Method to create all of the default points for the given device
     * @param device - Device to create points for
     * @return A list of the default points for the device
     */
    public abstract List<PointBase> createDefaultPointsForDevice(DeviceBase device);

    /**
     * Method to create all of the points for the given device
     * @param device - Device to create points for
     */
    public abstract void createAllPointsForDevice(DeviceBase device);

    /**
     * Method to get a map of device display groups and their associated device
     * types
     * @return Map with key: display group name, value: list of device display
     */
    public abstract Map<String, List<DeviceDisplay>> getDeviceDisplayGroupMap();
}