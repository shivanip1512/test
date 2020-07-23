package com.cannontech.common.pao.attribute.dao;

import java.util.List;

import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.model.AttributeAssignment;
import com.cannontech.common.pao.attribute.model.CustomAttribute;
import com.cannontech.common.pao.definition.model.PaoTypePointIdentifier;
import com.cannontech.common.pao.definition.model.PointIdentifier;

public interface AttributeDao {


    /**
     * Returns the list of attributes
     */
    List<CustomAttribute> getCustomAttributes();

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
     * Returns PaoType by Attribute Id
     */
    PaoType getPaoTypeByAttributeId(int attributeId);

    /**
     * Returns attribute for PaoType and Point
     */
    Attribute findCustomAttributeForPaoTypeAndPoint(PaoTypePointIdentifier paoTypePointIdentifier);

    void cacheAttributes();
}
