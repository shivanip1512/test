package com.cannontech.common.pao.definition.attribute.lookup;

import com.cannontech.common.pao.YukonDevice;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.definition.model.PaoPointIdentifier;
import com.cannontech.common.pao.definition.model.PaoPointTemplate;
import com.cannontech.core.dao.ExtraPaoPointAssignmentDao;
import com.cannontech.database.data.point.PointType;

public class MappedAttributeDefinition extends AttributeDefinition {

    private ExtraPaoPointAssignmentDao extraPaoPointAssignmentDao;
    private PointType pointType;
    
    public MappedAttributeDefinition(Attribute attribute, PointType pointType, ExtraPaoPointAssignmentDao extraPaoPointAssignmentDao) {
        super(attribute);
        this.pointType = pointType;
        this.extraPaoPointAssignmentDao = extraPaoPointAssignmentDao;
    }

    @Override
    public PaoPointIdentifier getPointIdentifier(YukonPao pao) {
        PaoPointIdentifier paoPointIdentifier = extraPaoPointAssignmentDao.getPaoPointIdentifier(pao, attribute);
        return paoPointIdentifier;
    }

    @Override
    public PaoPointTemplate getPointTemplate(YukonDevice device) {
        return null;
    }

    @Override
    public boolean isPointTemplateAvailable() {
        return false;
    }

    public PointType getPointType() {
        return pointType;
    }

}