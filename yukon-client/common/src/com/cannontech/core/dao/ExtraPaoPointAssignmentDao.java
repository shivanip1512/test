package com.cannontech.core.dao;

import java.util.List;

import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.definition.model.PaoPointIdentifier;
import com.cannontech.database.data.capcontrol.AttributePointMapping;

public interface ExtraPaoPointAssignmentDao {
    
    public PaoPointIdentifier getPaoPointIdentifier(YukonPao pao, Attribute attribute);

    public void saveAssignments(int paoId, List<AttributePointMapping> pointMappings);

}
