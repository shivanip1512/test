package com.cannontech.common.device.attribute.model;

import java.util.Set;

import com.cannontech.database.data.lite.LitePoint;

/**
 * Interface to be implemented by devices which have attributes
 */
public interface AttributeSource {

    /**
     * Method to get a point given an attribute
     * @param attribute - Attribute to get the point for
     * @return - attribute's point
     */
    LitePoint getPointForAttribute(Attribute attribute);

    /**
     * Method to get a set of all of the available attributes for this attribute
     * source
     * @return All available attributes
     */
    Set<Attribute> getAvailableAttributes();
}
