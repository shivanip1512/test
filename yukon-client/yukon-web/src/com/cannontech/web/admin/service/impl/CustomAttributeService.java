package com.cannontech.web.admin.service.impl;

import com.cannontech.common.exception.DataDependencyException;
import com.cannontech.common.pao.attribute.model.Assignment;
import com.cannontech.common.pao.attribute.model.AttributeAssignment;
import com.cannontech.common.pao.attribute.model.CustomAttribute;
import com.cannontech.user.YukonUserContext;

public interface CustomAttributeService {
    /**
     * Deletes custom attribute
     * 
     * @throws DataDependencyException, NotFoundException
     */
    void deleteCustomAttribute(int attributeId, YukonUserContext userContext) throws DataDependencyException;

    /**
     * Deletes attribute assignment
     * @throws NotFoundException
     */
    void deleteAttributeAssignment(int attributeAssignmentId, YukonUserContext userContext);

    /**
     * Creates attribute assignment
     * 
     * @return AttributeAssignment
     * @throws NotFoundException, DuplicateException
     */
    AttributeAssignment updateAttributeAssignment(Assignment assignment, YukonUserContext userContext);

    /**
     * Updates attribute assignment
     * 
     * @return AttributeAssignment
     * @throws NotFoundException, DuplicateException
     */
    AttributeAssignment createAttributeAssignment(Assignment assignment, YukonUserContext userContext);

    /**
     * Creates attribute
     * 
     * @return CustomAttribute
     * @throws NotFoundException, DuplicateException
     */
    CustomAttribute createCustomAttribute(CustomAttribute attribute, YukonUserContext userContext);

    /**
     * Updates attribute
     * 
     * @return CustomAttribute
     * @throws NotFoundException, DuplicateException
     */
    CustomAttribute updateCustomAttribute(CustomAttribute attribute, YukonUserContext userContext);
}
