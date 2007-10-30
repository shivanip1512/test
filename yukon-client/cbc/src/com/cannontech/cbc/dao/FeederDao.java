package com.cannontech.cbc.dao;

import java.util.List;

import org.springframework.dao.DataAccessException;

import com.cannontech.cbc.model.Feeder;
public interface FeederDao {

    public boolean add( Feeder feeder );
    
    public boolean remove( Feeder feeder );
    
    public boolean update( Feeder feeder );
    
    public Feeder getById( int id ) throws DataAccessException;

    /**
     * This method returns all the Feeder IDs that are not assgined
     *  to a SubBus.
     */
    public List<Integer> getUnassignedFeederIds();

    /**
     * This method returns the SubBus ID that owns the given feeder ID.
     * If no parent is found, CtiUtilities.NONE_ZERO_ID is returned.
     * 
     */
    public int getParentSubBusID( int feederID );

}
