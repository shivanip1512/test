package com.cannontech.core.dao;

import java.util.List;

import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.definition.model.PaoPointIdentifier;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.enums.RegulatorPointMapping;

public interface ExtraPaoPointAssignmentDao {
    
    public PaoPointIdentifier getPaoPointIdentifier(YukonPao pao, RegulatorPointMapping regulatorPointMapping);

    public int getPointId(YukonPao pao, RegulatorPointMapping regulatorPointMapping);

    public void saveAssignments(YukonPao pao, List<ExtraPaoPointMapping> pointMappings);

    public void removeAssignments(YukonPao pao);

    /**
     * Uses pointDao to retrieve point, throws NotFoundException if point does not exist for this mapping
     */
    public LitePoint getLitePoint(YukonPao regulator, RegulatorPointMapping regulatorMapping);

}