package com.cannontech.web.admin.service.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.events.loggers.SystemEventLogService;
import com.cannontech.common.exception.DataDependencyException;
import com.cannontech.common.pao.attribute.dao.AttributeDao;
import com.cannontech.common.pao.attribute.model.Assignment;
import com.cannontech.common.pao.attribute.model.AttributeAssignment;
import com.cannontech.common.pao.attribute.model.CustomAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.message.DbChangeManager;
import com.cannontech.message.dispatch.message.DbChangeCategory;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.admin.dao.CustomAttributeDao;

public class CustomAttributeServiceImpl implements CustomAttributeService {
    
    @Autowired private CustomAttributeDao customAttributeDao;
    @Autowired private AttributeService attributeService;
    @Autowired private AttributeDao attributeDao;
    @Autowired private DbChangeManager dbChangeManager;
    @Autowired private SystemEventLogService systemEventLogService;

    @Override
    public AttributeAssignment createAttributeAssignment(Assignment assignment, YukonUserContext userContext) {
        if (!attributeService.isValidAttributeId(assignment.getAttributeId())) {
            throw new NotFoundException("Attribute id:" + assignment.getAttributeId() + " is not in the database.");
        }
        AttributeAssignment createdAssignment = customAttributeDao.createAttributeAssignment(assignment);
        dbChangeManager.processDbChange(DbChangeType.ADD, DbChangeCategory.ATTRIBUTE_ASSIGNMENT,
                createdAssignment.getAttributeAssignmentId());

        systemEventLogService.attributeAssigned(userContext.getYukonUser(), createdAssignment.getCustomAttribute().getName(), assignment.getPaoType(), assignment.getPointType(), assignment.getOffset());

        return createdAssignment;
    }

    @Override
    public CustomAttribute createCustomAttribute(CustomAttribute attribute, YukonUserContext userContext) {
        CustomAttribute createdAttribute = customAttributeDao.createCustomAttribute(attribute);
        dbChangeManager.processDbChange(DbChangeType.ADD, DbChangeCategory.ATTRIBUTE,
                createdAttribute.getCustomAttributeId());
        systemEventLogService.attributeCreated(userContext.getYukonUser(), createdAttribute.getCustomAttributeId(), createdAttribute.getName());
        return createdAttribute;
    }

    @Override
    public AttributeAssignment updateAttributeAssignment(Assignment assignment, YukonUserContext userContext) {
        if (assignment.isEmpty()) {
            return attributeDao.getAssignmentById(assignment.getAttributeAssignmentId());
        }
        
        if (!attributeService.isValidAttributeId(assignment.getAttributeId())) {
            throw new NotFoundException("Attribute id:" + assignment.getAttributeId() + " is not in the database.");
        }
        if (!attributeService.isValidAssignmentId(assignment.getAttributeAssignmentId())) {
            throw new NotFoundException(
                    "Attribute Assignment id:" + assignment.getAttributeAssignmentId() + " is not in the database.");
        }

        // Gather information for logging
        Assignment originalAssignment = attributeDao.getAssignmentById(assignment.getAttributeAssignmentId());
        String originalAttributeName = attributeService.findCustomAttribute(originalAssignment.getAttributeId()).getName();

        AttributeAssignment updatedAssignment = customAttributeDao.updateAttributeAssignment(assignment);
        dbChangeManager.processDbChange(DbChangeType.UPDATE, DbChangeCategory.ATTRIBUTE_ASSIGNMENT,
                updatedAssignment.getAttributeAssignmentId());

        systemEventLogService.attributeAssignmentDeleted(userContext.getYukonUser(), originalAttributeName, originalAssignment.getPaoType(), originalAssignment.getPointType(), originalAssignment.getOffset());
        systemEventLogService.attributeAssigned(userContext.getYukonUser(), updatedAssignment.getCustomAttribute().getName(), updatedAssignment.getPaoType(), updatedAssignment.getPointType(), updatedAssignment.getOffset());

        return updatedAssignment;
    }

    @Override
    public CustomAttribute updateCustomAttribute(CustomAttribute attribute, YukonUserContext userContext) {
        if (attribute.isEmpty()) {
            return attributeDao.getCustomAttribute(attribute.getCustomAttributeId());
        }
        if (!attributeService.isValidAttributeId(attribute.getCustomAttributeId())) {
            throw new NotFoundException("Attribute id:" + attribute.getCustomAttributeId() + " is not in the database.");
        }
        String originalAttributeName = attributeService.findCustomAttribute(attribute.getCustomAttributeId()).getName();
        CustomAttribute updatedAttribute = customAttributeDao.updateCustomAttribute(attribute);
        dbChangeManager.processDbChange(DbChangeType.UPDATE, DbChangeCategory.ATTRIBUTE, attribute.getCustomAttributeId());
        systemEventLogService.attributeUpdated(userContext.getYukonUser(), originalAttributeName, attribute.getName());
        return updatedAttribute;
    }

    @Override
    public void deleteCustomAttribute(int attributeId, YukonUserContext userContext) throws DataDependencyException {
        if (!attributeService.isValidAttributeId(attributeId)) {
            throw new NotFoundException("Attribute id:" + attributeId + " is not in the database.");
        }
        String attributeName = attributeService.findCustomAttribute(attributeId).getName();
        customAttributeDao.deleteCustomAttribute(attributeId);
        dbChangeManager.processDbChange(DbChangeType.DELETE, DbChangeCategory.ATTRIBUTE, attributeId);
        systemEventLogService.attributeDeleted(userContext.getYukonUser(), attributeName);
    }

    @Override
    public void deleteAttributeAssignment(int attributeAssignmentId, YukonUserContext userContext) {
        if (!attributeService.isValidAssignmentId(attributeAssignmentId)) {
            throw new NotFoundException("Attribute Assignment id:" + attributeAssignmentId + " is not in the database.");
        }

        Assignment assignment = attributeDao.getAssignmentById(attributeAssignmentId);
        String attributeName = attributeDao.getCustomAttribute(assignment.getAttributeId()).getName();

        customAttributeDao.deleteAttributeAssignment(attributeAssignmentId);
        dbChangeManager.processDbChange(DbChangeType.DELETE, DbChangeCategory.ATTRIBUTE_ASSIGNMENT, attributeAssignmentId);
        systemEventLogService.attributeAssignmentDeleted(userContext.getYukonUser(), attributeName, assignment.getPaoType(), assignment.getPointType(), assignment.getOffset());
    }
}
 