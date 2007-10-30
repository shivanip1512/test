package com.cannontech.cbc.dao;

import java.util.List;

import org.springframework.dao.DataAccessException;

import com.cannontech.cbc.model.SubstationBus;

public interface SubstationBusDao {
    public boolean add( SubstationBus feeder );
    
    public boolean remove( SubstationBus feeder );
    
    public boolean update( SubstationBus feeder );
    
    public SubstationBus getById( int id ) throws DataAccessException;
    
    public List<Integer> getAllUnassignedBuses ();
}
