package com.cannontech.common.device.attribute.service;

import java.util.Set;

import com.cannontech.common.device.attribute.model.Attribute;
import com.cannontech.common.device.attribute.model.AttributeSource;
import com.cannontech.database.data.device.DeviceBase;
import com.cannontech.database.data.lite.LitePoint;

public interface AttributeService {

    /**
     * Method to get the current value of an attribute from an attribute source
     * @param source - Source to get the value from
     * @param attribute - Attribute to get the value for
     * @return - The value
     */
    public abstract double getCurrentValue(AttributeSource source, Attribute attribute);

    /**
     * Method to get the current state of an attribute from an attribute source
     * @param source - Source to get the state from
     * @param attribute - Attribute to get the state for
     * @return - The state
     */
    public abstract String getCurrentStateText(AttributeSource source, Attribute attribute);

    /**
     * Method to get the lite point for the given device for the given attribute
     * @param device - Device to get point for
     * @param attribute - Attribute to get point for
     * @return The point for the given attribute
     */
    public abstract LitePoint getPointForAttribute(DeviceBase device, Attribute attribute);

    /**
     * Method to get a set of attributes available for the given device
     * @param device - The device to get attributes for
     * @return All attributes available for this device
     */
    public abstract Set<Attribute> getAvailableAttributes(DeviceBase device);

    /**
     * Method to get a set of all attributes for which points exist for a given
     * device
     * @param device - Device to get points for
     * @return A set of attributes
     */
    public abstract Set<Attribute> getAllExistingAtributes(DeviceBase device);
}
