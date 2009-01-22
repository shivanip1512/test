package com.cannontech.multispeak.dao;

import java.util.List;

import com.cannontech.multispeak.db.MspLMInterfaceMapping;

public interface MspLMInterfaceMappingDao {

    public boolean add(String strategyName, String substationName, int paobjectId);

    public boolean remove(int mspLMInterfaceMappingId);

    public boolean removeAllByStrategyName(String strategyName);

    public MspLMInterfaceMapping getForId(int mspLMInterfaceMappingId);
    
    public MspLMInterfaceMapping getForStrategyAndSubstation(String strategyName, String substationName);
    
    public List<MspLMInterfaceMapping> getForStrategy(String strategyName);

}
