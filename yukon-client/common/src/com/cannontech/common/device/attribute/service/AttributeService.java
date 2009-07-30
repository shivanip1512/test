package com.cannontech.common.device.attribute.service;

import java.util.Set;

import com.cannontech.common.device.attribute.model.Attribute;
import com.cannontech.common.device.definition.model.DevicePointTemplate;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.database.data.lite.LitePoint;

public interface AttributeService {

    /**
     * Method to get the lite point for the given device for the given attribute
     * @param device - Device to get point for
     * @param attribute - Attribute to get point for
     * @return The point for the given attribute
     */
    public LitePoint getPointForAttribute(SimpleDevice device, Attribute attribute);

    /**
     * Method to get a set of attributes available for the given device
     * @param device - The device to get attributes for
     * @return An immutable set of all attributes available for this device
     */
    public Set<Attribute> getAvailableAttributes(SimpleDevice device);

    /**
     * Method to get a set of all attributes for which points exist for a given
     * device
     * @param device - Device to get points for
     * @return A set of attributes (returns a new copy each time the method is
     *         called)
     */
    public Set<Attribute> getAllExistingAttributes(SimpleDevice device);
    
    public Attribute resolveAttributeName(String name);
    
    /**
     * Method used to determine if a device supports a given attribute
     * @param device - Device in question
     * @param attribute - Attribute to determine support for
     * @return True if the device supports the attribute
     */
    public boolean isAttributeSupported(SimpleDevice device, Attribute attribute);

    /**
     * Method used to determine if a point exists on a device for a given attribute
     * @param device - Device in question
     * @param attribute - Attribute to determine if point exists
     * @return True if the point exists on the device
     */
    public boolean pointExistsForAttribute(SimpleDevice device, Attribute attribute);
    
    public DevicePointTemplate getDevicePointTemplateForAttribute(SimpleDevice device, Attribute attribute);
    
    /**
     * Method used to create a point for the device and given attribute
     * @param device - Device to create point for
     * @param attribute - Attribute the point will represent
     */
    public void createPointForAttribute(SimpleDevice device, Attribute attribute);
}
