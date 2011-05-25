package com.cannontech.cbc.dao;

import java.util.List;

import com.cannontech.capcontrol.model.LiteCapControlObject;
import com.cannontech.cbc.model.Substation;
import com.cannontech.cbc.model.SubstationBus;
import com.cannontech.common.search.SearchResult;

public interface SubstationBusDao {
    public void add( SubstationBus substationBus );
    
    public boolean remove( SubstationBus substationBus );
    
    public boolean update( SubstationBus substationBus );
    
    public SubstationBus getById( int id );
    
    public int getParentId(SubstationBus subtationBus);
    
    public List<Integer> getAllUnassignedBuses ();
    
    public SearchResult<LiteCapControlObject> getOrphans(final int start, final int count);
    
    public boolean assignSubstationBus(Substation substation, SubstationBus substationBus);
    
    public boolean assignSubstationBus(int substationId, int substationBusId);

    public boolean unassignSubstationBus(SubstationBus substationBus);
    
    public boolean unassignSubstationBus(int substationBusId);
}
