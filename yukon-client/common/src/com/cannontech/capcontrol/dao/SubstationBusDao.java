package com.cannontech.capcontrol.dao;

import java.util.Collection;
import java.util.List;

import com.cannontech.capcontrol.model.LiteCapControlObject;
import com.cannontech.capcontrol.model.Substation;
import com.cannontech.capcontrol.model.SubstationBus;
import com.cannontech.common.search.SearchResult;

public interface SubstationBusDao {
    
    /**
     * This method returns a {@link SubstationBus} specified by an Id.
     * @param id the paoId of the {@link SubstationBus}
     * @return {@link SubstationBus} specified.
     */
    public SubstationBus finSubBusdById(int id);
    
    /**
     * This method returns a list of integers representing the {@link SubstationBus} objects
     * which don't have assignments.
     */
    public List<Integer> getAllUnassignedBuses();
    
    public SearchResult<LiteCapControlObject> getOrphans(final int start, final int count);
    
    /**
     * This method assigns a {@link SubstationBus} to a {@link Substation} and performs all
     * necessary db change messaging.
     * @param subBusId the PaoId of the {@link SubstationBus} being assigned.
     * @param substationName the name of the {@link Substation} being assigned to.
     * @return true if the assignment occurred and only updated one row in the db, false otherwise.
     */
    public boolean assignSubstationBus(int subBusId, String substationName);
    
    /**
     * This method assigns a {@link SubstationBus} to a {@link Substation} and performs all
     * necessary db change messaging.
     * @param substationBusId the PaoId of the {@link SubstationBus} being assigned.
     * @param substationId the PaoId of the {@link Substation} being assigned to.
     * @return true if the assignment occurred and only updated one row in the db, false otherwise.
     */
    public boolean assignSubstationBus(int substationId, int substationBusId);
    
    /**
     * This method removes all assignments for a given {@link SubstationBus}.
     * @param substationBusId the PaoId of the {@link SubstationBus}.
     * @return true if the unassignment occurred and only updated one row in the db, false otherwise.
     */
    public boolean unassignSubstationBus(int substationBusId);
    
    public Collection<Integer> getBankStatusPointIdsBySubbusId(int substationId);
}
