package com.cannontech.cbc.cyme;

import java.util.Collection;
import java.util.Map;

import com.cannontech.capcontrol.model.PointPaoIdentifier;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.core.dynamic.PointValueQualityHolder;

public interface CymePointDataCache {

    public void registerListener(CymeSimulationListener listener);
    public void registerPointsForSubStationBus(PaoIdentifier subbus);
    
    public Collection<PointPaoIdentifier> getPaosInSystem();
    
    public Map<Integer,PointValueQualityHolder> getCurrentValues();
    public PointValueQualityHolder getCurrentValue(int pointId);
    
    /**
     * Throws NotFoundException if the listener is not registered.
     * Returns null if there is not value stored for the LoadFactor.
     * @param listener
     * @return
     */
    public PointValueQualityHolder getCurrentLoadFactor(int subbusId);
    
    public boolean isCymeEnabled(int subbusId);
    
    public void unRegisterCymeSimulationListener(int subbusId);
}