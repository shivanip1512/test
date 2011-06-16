package com.cannontech.core.dao;

import java.util.List;

import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.definition.model.PaoPointIdentifier;

public interface ExtraPaoPointAssignmentDao {
    
    public PaoPointIdentifier getPaoPointIdentifier(YukonPao pao, Attribute attribute);

    public int getPointId(YukonPao pao, Attribute attribute);

    public void saveAssignments(YukonPao pao, List<ExtraPaoPointMapping> pointMappings);

    public void removeAssignments(YukonPao pao);

}
