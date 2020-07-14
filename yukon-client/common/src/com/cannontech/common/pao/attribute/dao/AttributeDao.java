package com.cannontech.common.pao.attribute.dao;

import java.util.List;

import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.exception.DataDependencyException;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.model.AttributeAssignment;
import com.cannontech.common.pao.attribute.model.CustomAttribute;
import com.cannontech.common.pao.definition.model.PointIdentifier;

public interface AttributeDao {

    /**
     * Creates or updates attribute
     * 
     * @throws DuplicateException - if attribute name already exists
     */
    void saveCustomAttribute(CustomAttribute attribute);

    /**
     * Deletes custom attribute
     * 
     * @throws DataDependencyException
     */
    void deleteCustomAttribute(int attributeId) throws DataDependencyException;

    /**
     * Returns the list of attributes
     */
    List<CustomAttribute> getCustomAttributes();

    /**
     * Creates or updates attribute assignment
     * 
     * @throws DuplicateException - if assignment has either same attribute assigned to multiple points on the same device or
     *                            multiple entries with the exact same mapping
     */
    void saveAttributeAssignment(AttributeAssignment assignment);

    /**
     * Deletes attribute assignment
     */
    void deleteAttributeAssignment(int attributeAssignmentId);

    /**
     * Returns attribute assignment
     */
    AttributeAssignment getAssignmentById(int attributeAssignmentId);

    /**
     * Returns custom attribute
     */
    CustomAttribute getCustomAttribute(int attributeId);

    /**
     * Returns Point Identifier for attributeId and paoType
     */
    PointIdentifier getPointIdentifier(int attributeId, PaoType paoType);

    /**
     * Returns a list of all the devices in a given DeviceGroup that support the given Attribute.
     * This method works recursively on each child group of the requested group.
     */
    List<SimpleDevice> getDevicesInGroupThatSupportAttribute(DeviceGroup group, Attribute attribute);
}
