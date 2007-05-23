package com.cannontech.common.device.attribute.service;

import java.util.Set;

import com.cannontech.common.device.attribute.model.Attribute;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;

public interface AttributeService {

    /**
     * Method to get the lite point for the given device for the given attribute
     * @param device - Device to get point for
     * @param attribute - Attribute to get point for
     * @return The point for the given attribute
     */
    public abstract LitePoint getPointForAttribute(LiteYukonPAObject device, Attribute attribute);

    /**
     * Method to get a set of attributes available for the given device
     * @param device - The device to get attributes for
     * @return An immutable set of all attributes available for this device
     */
    public abstract Set<Attribute> getAvailableAttributes(LiteYukonPAObject device);

    /**
     * Method to get a set of all attributes for which points exist for a given
     * device
     * @param device - Device to get points for
     * @return A set of attributes (returns a new copy each time the method is
     *         called)
     */
    public abstract Set<Attribute> getAllExistingAtributes(LiteYukonPAObject device);
}
