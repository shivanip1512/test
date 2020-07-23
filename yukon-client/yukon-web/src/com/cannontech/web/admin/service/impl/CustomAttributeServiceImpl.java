package com.cannontech.web.admin.service.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.exception.DataDependencyException;
import com.cannontech.common.pao.attribute.model.Assignment;
import com.cannontech.common.pao.attribute.model.AttributeAssignment;
import com.cannontech.common.pao.attribute.model.CustomAttribute;
import com.cannontech.web.admin.dao.CustomAttributeDao;

public class CustomAttributeServiceImpl implements CustomAttributeService {
    
    @Autowired private CustomAttributeDao customAttributeDao;
    
    @Override
    public AttributeAssignment createAttributeAssignment(Assignment assignment) {
        return customAttributeDao.createAttributeAssignment(assignment);
        //dbchange
        //event log
    }
    
    @Override
    public CustomAttribute createCustomAttribute(CustomAttribute attribute) {
        return customAttributeDao.createCustomAttribute(attribute);
        //dbchange
        //event log
    }

    @Override
    public AttributeAssignment updateAttributeAssignment(Assignment assignment) {
        return customAttributeDao.updateAttributeAssignment(assignment);
        //dbchange
        //event log
    }
    
    @Override
    public CustomAttribute updateCustomAttribute(CustomAttribute attribute) {
        return customAttributeDao.updateCustomAttribute(attribute);
        //dbchange
        //event log
    }

    
    @Override
    public void deleteCustomAttribute(int attributeId) throws DataDependencyException {
        customAttributeDao.deleteCustomAttribute(attributeId);
        //dbchange
        //event log
    }

    @Override
    public void deleteAttributeAssignment(int attributeAssignmentId) {
        customAttributeDao.deleteAttributeAssignment(attributeAssignmentId);
        //dbchange
        //event log
    }
}
 