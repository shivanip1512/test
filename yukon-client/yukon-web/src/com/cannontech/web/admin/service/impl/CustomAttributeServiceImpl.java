package com.cannontech.web.admin.service.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.exception.DataDependencyException;
import com.cannontech.common.pao.attribute.model.Assignment;
import com.cannontech.common.pao.attribute.model.AttributeAssignment;
import com.cannontech.common.pao.attribute.model.CustomAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.message.DbChangeManager;
import com.cannontech.message.dispatch.message.DbChangeCategory;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.cannontech.web.admin.dao.CustomAttributeDao;

public class CustomAttributeServiceImpl implements CustomAttributeService {
    
    @Autowired private CustomAttributeDao customAttributeDao;
    @Autowired private AttributeService attributeService;
    @Autowired private DbChangeManager dbChangeManager;

    @Override
    public AttributeAssignment createAttributeAssignment(Assignment assignment) {
        if (!attributeService.isValidAttributeId(assignment.getAttributeId())) {
            throw new NotFoundException("Attribute id:" + assignment.getAttributeId() + " is not in the database.");
        }
        AttributeAssignment createdAssignment = customAttributeDao.createAttributeAssignment(assignment);
        dbChangeManager.processDbChange(DbChangeType.ADD, DbChangeCategory.ATTRIBUTE_ASSIGNMENT,
                createdAssignment.getAttributeAssignmentId());
        return createdAssignment;
    }

    @Override
    public CustomAttribute createCustomAttribute(CustomAttribute attribute) {
        CustomAttribute createdAttribute = customAttributeDao.createCustomAttribute(attribute);
        dbChangeManager.processDbChange(DbChangeType.ADD, DbChangeCategory.ATTRIBUTE,
                createdAttribute.getCustomAttributeId());
        return createdAttribute;
    }

    @Override
    public AttributeAssignment updateAttributeAssignment(Assignment assignment) {
        if (!attributeService.isValidAttributeId(assignment.getAttributeId())) {
            throw new NotFoundException("Attribute id:" + assignment.getAttributeId() + " is not in the database.");
        }
        if (!attributeService.isValidAssignmentId(assignment.getAttributeAssignmentId())) {
            throw new NotFoundException("Attribute Assignment id:" + assignment.getAttributeAssignmentId() + " is not in the database.");
        }
        AttributeAssignment updatedAssignment = customAttributeDao.updateAttributeAssignment(assignment);
        dbChangeManager.processDbChange(DbChangeType.UPDATE, DbChangeCategory.ATTRIBUTE_ASSIGNMENT,
                updatedAssignment.getAttributeAssignmentId());
        return updatedAssignment;
    }

    @Override
    public CustomAttribute updateCustomAttribute(CustomAttribute attribute) {
        if (!attributeService.isValidAttributeId(attribute.getCustomAttributeId())) {
            throw new NotFoundException("Attribute id:" + attribute.getCustomAttributeId() + " is not in the database.");
        }
        CustomAttribute updatedAttribute = customAttributeDao.updateCustomAttribute(attribute);
        dbChangeManager.processDbChange(DbChangeType.UPDATE, DbChangeCategory.ATTRIBUTE, attribute.getCustomAttributeId());
        return updatedAttribute;
    }

    @Override
    public void deleteCustomAttribute(int attributeId) throws DataDependencyException {
        if (!attributeService.isValidAttributeId(attributeId)) {
            throw new NotFoundException("Attribute id:" + attributeId + " is not in the database.");
        }
        customAttributeDao.deleteCustomAttribute(attributeId);
        dbChangeManager.processDbChange(DbChangeType.DELETE, DbChangeCategory.ATTRIBUTE, attributeId);
    }

    @Override
    public void deleteAttributeAssignment(int attributeAssignmentId) {
        if (!attributeService.isValidAssignmentId(attributeAssignmentId)) {
            throw new NotFoundException("Attribute Assignment id:" + attributeAssignmentId + " is not in the database.");
        }
        customAttributeDao.deleteAttributeAssignment(attributeAssignmentId);
        dbChangeManager.processDbChange(DbChangeType.DELETE, DbChangeCategory.ATTRIBUTE_ASSIGNMENT, attributeAssignmentId);
    }
}
 