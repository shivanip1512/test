package com.cannontech.core.dao;

import java.util.Map;

import com.cannontech.capcontrol.RegulatorPointMapping;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.database.data.lite.LitePoint;

public interface ExtraPaoPointAssignmentDao {

    int getPointId(YukonPao pao, RegulatorPointMapping regulatorPointMapping);

    void saveAssignments(YukonPao pao, Map<RegulatorPointMapping, Integer> pointMappings);

    void removeAssignments(YukonPao pao);

    /**
     * Uses pointDao to retrieve point, throws NotFoundException if point does not exist for this mapping
     */
    LitePoint getLitePoint(YukonPao regulator, RegulatorPointMapping regulatorMapping);
    
    /**
     * Removes the specified mapping on the pao.
     * @return True if the mapping was removed. False if the mapping did not exist.
     */
    boolean removeAssignment(PaoIdentifier paoIdentifier, RegulatorPointMapping regulatorMapping);
    
    /**
     * Adds the specified mapping to the pao. If overwriteExistingPoint is true, any existing mapping
     * will be silently overwritten. If overwriteExistingPoint is false and there is a conflicting
     * point mapping, an IllegalStateException will be thrown.
     */
    void addAssignment(YukonPao pao, int pointId, RegulatorPointMapping regulatorMapping, boolean overwriteExistingPoint);
    
    /**
     * Retrieves all ExtraPaoPointMappings for the specified pao.
     */
    Map<RegulatorPointMapping, Integer> getAssignments(PaoIdentifier paoIdentifier);
}