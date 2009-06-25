package com.cannontech.cbc.dao;

import java.util.List;

import com.cannontech.cbc.model.LiteCapControlObject;
import com.cannontech.cbc.model.Substation;
import com.cannontech.cbc.model.SubstationBus;

public interface SubstationBusDao {
    public boolean add( SubstationBus feeder );
    
    public boolean remove( SubstationBus feeder );
    
    public boolean update( SubstationBus feeder );
    
    public SubstationBus getById( int id );
    
    public int getParentId(SubstationBus subtationBus);
    
    public List<Integer> getAllUnassignedBuses ();
    public List<LiteCapControlObject> getOrphans();
    
    public boolean assignSubstationBus(Substation substation, SubstationBus substationBus);
    public boolean assignSubstationBus(int substationId, int substationBusId);

    public boolean unassignSubstationBus(SubstationBus substationBus);
    public boolean unassignSubstationBus(int substationBusId);
}
