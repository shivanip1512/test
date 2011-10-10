package com.cannontech.capcontrol.dao;

import java.util.Collection;
import java.util.List;

import com.cannontech.capcontrol.model.LiteCapControlObject;
import com.cannontech.capcontrol.model.SubstationBus;
import com.cannontech.common.search.SearchResult;

public interface SubstationBusDao {    
    public SubstationBus getById( int id );
    
    public List<Integer> getAllUnassignedBuses ();
    
    public SearchResult<LiteCapControlObject> getOrphans(final int start, final int count);
    
    public boolean assignSubstationBus(int subBusId, String substationName);
    
    public boolean assignSubstationBus(int substationId, int substationBusId);
    
    public boolean unassignSubstationBus(int substationBusId);
    
    public Collection<Integer> getBankStatusPointIdsBySubbusId(int substationId);
}
