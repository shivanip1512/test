package com.cannontech.capcontrol.dao;

import java.util.List;

import org.springframework.dao.EmptyResultDataAccessException;

import com.cannontech.capcontrol.model.Feeder;
import com.cannontech.capcontrol.model.LiteCapControlObject;
import com.cannontech.common.search.SearchResult;

public interface FeederDao {
    
    /**
     * This method returns a feeder object by a given id.
     * @param id the PaoID of the feeder.
     * @return the Feeder specified.
     */
    public Feeder findById( int id );
    
    /**
     * This method returns all the Feeder IDs that are not assigned
     *  to a SubBus.
     */
    public List<Integer> getUnassignedFeederIds();
    
    public SearchResult<LiteCapControlObject> getOrphans(int start, int count);
    
    /**
     * This method returns the SubBus ID that owns the given feeder ID.
     * If no parent is found, CtiUtilities.NONE_ZERO_ID is returned.
     */
    public int getParentSubBusID( int feederID ) throws EmptyResultDataAccessException;
    
    /**
     * This method assigns a feeder to a subbus and does all necessary db change messaging.
     * @param feederId the paoId of the feeder being assigned
     * @param subBusName the name of the subbus being assigned to.
     * @return true if the assignment occurred and only updated one row in the db, false otherwise.
     */
    public boolean assignFeeder(int feederId, String subBusName);
    
    /**
     * This method assigns a feeder to a subbus and does all necessary db change messaging.
     * @param feederId the paoId of the feeder being assigned
     * @param substationBusId the paoId of the subbus being assigned to.
     * @return true if the assignment occurred and only updated one row in the db, false otherwise.
     */
    public boolean assignFeeder(int substationBusId, int feederId);

    /**
     * This method removes all assignments for a given feeder.
     * @param feederId the paoId of the feeder.
     * @return true if the unassignment occurred and only updated one row in the db, false otherwise.
     */
    public boolean unassignFeeder(int feederId);
}
