package com.cannontech.capcontrol.dao;

import java.util.List;

import org.springframework.dao.EmptyResultDataAccessException;

import com.cannontech.capcontrol.model.Feeder;
import com.cannontech.capcontrol.model.LiteCapControlObject;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.search.SearchResult;

public interface FeederDao {

    public void add( Feeder feeder );
    
    public boolean remove(PaoIdentifier feederId);
    
    public boolean update( Feeder feeder );
    
    public Feeder getById( int id );
    
    public int getParentId(Feeder feeder);
    
    /**
     * This method returns all the Feeder IDs that are not assigned
     *  to a SubBus.
     */
    public List<Integer> getUnassignedFeederIds();
    
    public SearchResult<LiteCapControlObject> getOrphans(int start, int count);
    
    /**
     * This method returns the SubBus ID that owns the given feeder ID.
     * If no parent is found, CtiUtilities.NONE_ZERO_ID is returned.
     * 
     */
    public int getParentSubBusID( int feederID ) throws EmptyResultDataAccessException;
    
    public boolean assignFeeder(int feederId, String subBusName);
    
    public boolean assignFeeder(int substationBusId, int feederId);

    public boolean unassignFeeder(Feeder feeder);
    
    public boolean unassignFeeder(int feederId);
}
