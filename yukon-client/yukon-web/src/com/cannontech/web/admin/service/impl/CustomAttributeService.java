package com.cannontech.web.admin.service.impl;

import com.cannontech.common.exception.DataDependencyException;
import com.cannontech.common.pao.attribute.model.Assignment;
import com.cannontech.common.pao.attribute.model.AttributeAssignment;
import com.cannontech.common.pao.attribute.model.CustomAttribute;

public interface CustomAttributeService {
    /**
     * Deletes custom attribute
     * 
     * @throws DataDependencyException
     */
    void deleteCustomAttribute(int attributeId) throws DataDependencyException;

    /**
     * Deletes attribute assignment
     */
    void deleteAttributeAssignment(int attributeAssignmentId);

    /**
     * Creates attribute assignment
     * 
     * @return AttributeAssignment
     */
    AttributeAssignment updateAttributeAssignment(Assignment assignment);

    /**
     * Updates attribute assignment
     * 
     * @return AttributeAssignment
     */
    AttributeAssignment createAttributeAssignment(Assignment assignment);

    /**
     * Creates attribute
     * 
     * @return CustomAttribute
     */
    CustomAttribute createCustomAttribute(CustomAttribute attribute);

    /**
     * Updates attribute
     * 
     * @return CustomAttribute
     */
    CustomAttribute updateCustomAttribute(CustomAttribute attribute);
}
