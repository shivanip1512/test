package com.cannontech.common.pao.attribute.dao;

import java.util.List;

import com.cannontech.common.pao.attribute.model.AttributeAssignment;
import com.cannontech.common.pao.attribute.model.CustomAttribute;

public interface AttributeDao {

    /**
     * Returns attribute assignment
     * @throws NotFoundException
     */
    AttributeAssignment getAssignmentById(int attributeAssignmentId);

    /**
     * Returns all custom attributes
     */
    List<CustomAttribute> getCustomAttributes();

    /**
     * Returns all assignments 
     */
    List<AttributeAssignment> getAssignments();

    /**
     * Returns custom attribute for Id
     * @throws NotFoundException
     */
    CustomAttribute getCustomAttribute(int attributeId);
}
