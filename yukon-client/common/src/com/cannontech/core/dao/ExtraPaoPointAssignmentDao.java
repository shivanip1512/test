package com.cannontech.core.dao;

import java.util.List;

import com.cannontech.common.pao.PaoIdentifier;
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
    
    /**
     * Removes the specified mapping on the pao.
     * @return True if the mapping was removed. False if the mapping did not exist.
     */
    public boolean removeAssignment(PaoIdentifier paoIdentifier, RegulatorPointMapping regulatorMapping);
    
    /**
     * Adds the specified mapping to the pao. If overwriteExistingPoint is true, any existing mapping
     * will be silently overwritten. If overwriteExistingPoint is false and there is a conflicting
     * point mapping, an IllegalStateException will be thrown.
     */
    public void addAssignment(YukonPao pao, int pointId, RegulatorPointMapping regulatorMapping, boolean overwriteExistingPoint);
    
    /**
     * Retrieves all ExtraPaoPointMappings for the specified pao.
     */
    public List<ExtraPaoPointMapping> getAssignments(PaoIdentifier paoIdentifier);
}