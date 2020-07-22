package com.cannontech.web.admin.service.impl;

import com.cannontech.common.exception.DataDependencyException;
import com.cannontech.common.pao.attribute.model.Assignment;
import com.cannontech.common.pao.attribute.model.AttributeAssignment;
import com.cannontech.common.pao.attribute.model.CustomAttribute;

public interface CustomAttributeService {
    /**
     * Creates or updates attribute
     * @return CustomAttribute
     */
    public CustomAttribute saveCustomAttribute(CustomAttribute attribute);

    /**
     * Deletes custom attribute
     * @throws DataDependencyException
     */
    void deleteCustomAttribute(int attributeId) throws DataDependencyException;
    
    /**
     * Creates or updates attribute assignment
     * @return AttributeAssignment 
     */
    AttributeAssignment saveAttributeAssignment(Assignment assignment);

    /**
     * Deletes attribute assignment
     */
    void deleteAttributeAssignment(int attributeAssignmentId);
}
