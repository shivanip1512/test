package com.cannontech.cbc.dao;

import java.util.List;

import org.springframework.dao.EmptyResultDataAccessException;

import com.cannontech.cbc.model.Feeder;
import com.cannontech.cbc.model.SubstationBus;

public interface FeederDao {

    public boolean add( Feeder feeder );
    
    public boolean remove( Feeder feeder );
    
    public boolean update( Feeder feeder );
    
    public Feeder getById( int id );
    
    public int getParentId(Feeder feeder);
    
    /**
     * This method returns all the Feeder IDs that are not assigned
     *  to a SubBus.
     */
    public List<Integer> getUnassignedFeederIds();

    /**
     * This method returns the SubBus ID that owns the given feeder ID.
     * If no parent is found, CtiUtilities.NONE_ZERO_ID is returned.
     * 
     */
    public int getParentSubBusID( int feederID ) throws EmptyResultDataAccessException;
    
    public boolean assignFeeder(SubstationBus substationBus, Feeder feeder);
    public boolean assignFeeder(int substationBusId, int feederId);

    public boolean unassignFeeder(SubstationBus substationBus, Feeder feeder);
    public boolean unassignFeeder(int substationBusId, int feederId);
}
