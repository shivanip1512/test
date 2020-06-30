package com.cannontech.common.pao.attribute.dao;

import java.util.List;

import com.cannontech.common.pao.attribute.model.AttributeAssignment;
import com.cannontech.common.pao.attribute.model.CustomAttribute;

public interface AttributeDao {

    /**
     * Creates or updates attribute
     * 
     * @throws DuplicateException - if attribute name already exists
     */
    void saveCustomAttribute(CustomAttribute attribute);

    /**
     * Deletes custom attribute
     */
    void deleteCustomAttribute(int attributeId);

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
    AttributeAssignment getAttributeAssignmentById(int attributeAssignmentId);
}
