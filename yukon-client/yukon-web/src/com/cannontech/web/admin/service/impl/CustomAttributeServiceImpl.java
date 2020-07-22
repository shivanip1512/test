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
    public AttributeAssignment saveAttributeAssignment(Assignment assignment) {
        return customAttributeDao.saveAttributeAssignment(assignment);
        //dbchange
        //event log
    }
    
    @Override
    public CustomAttribute saveCustomAttribute(CustomAttribute attribute) {
        return customAttributeDao.saveCustomAttribute(attribute);
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
 