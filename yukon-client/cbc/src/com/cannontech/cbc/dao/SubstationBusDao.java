package com.cannontech.cbc.dao;

import java.util.List;

import com.cannontech.cbc.model.SubstationBus;
import com.cannontech.core.dao.NotFoundException;

public interface SubstationBusDao {
    public boolean add( SubstationBus feeder );
    
    public boolean remove( SubstationBus feeder );
    
    public boolean update( SubstationBus feeder );
    
    public SubstationBus getById( int id ) throws NotFoundException;
    
    public List<Integer> getAllUnassignedBuses ();
}
