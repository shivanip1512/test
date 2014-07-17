package com.cannontech.multispeak.dao;

import java.util.List;

import com.cannontech.core.dao.NotFoundException;
import com.cannontech.multispeak.db.MspLMInterfaceMapping;

public interface MspLMMappingDao {

    public boolean add(String strategyName, String substationName, int paobjectId);
    
    /**
     * Updates PaobjectId for mappings where strategy and substation match given names.
     * Returns true if an a row was affected.
     * @param strategyName
     * @param substationName
     * @param paobjectId
     * @return
     */
    public boolean updatePaoIdForStrategyAndSubstation(String strategyName, String substationName, int paobjectId);

    public boolean remove(int mspLMInterfaceMappingId);

    public boolean removeAllByStrategyName(String strategyName);

    public MspLMInterfaceMapping getForId(int mspLMInterfaceMappingId) throws NotFoundException;
    
    public MspLMInterfaceMapping getForStrategyAndSubstation(String strategyName, String substationName) throws NotFoundException;
    
    /**
     * Search for a mapping for a given strategy and substation.
     * If found, the MspLmInterfaceMappingId for the mapping is returned.
     * If not found, null is returned.
     * @param strategyName
     * @param substationName
     * @return
     */
    public Integer findIdForStrategyAndSubstation(String strategyName, String substationName);
			
    public List<MspLMInterfaceMapping> getAllMappings();

}
