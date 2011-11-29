package com.cannontech.capcontrol.dao;

import java.util.List;

import com.cannontech.capcontrol.model.LiteCapControlObject;
import com.cannontech.capcontrol.model.Substation;
import com.cannontech.common.search.SearchResult;

public interface SubstationDao {
    
    /**
     * This method assigns a {@link Substation} to an Area and performs the necessary db
     * change messaging.
     * @param substationId the paoId of the {@link Substation} being assigned.
     * @param areaName the name of the area being assigned to.
     * @return true if the assignment occurred and only one row in the db was updated, 
     * false otherwise.
     */
    public boolean assignSubstation(int substationId, String areaName);
    
    /**
     * This method assigns a {@link Substation} to an Area and performs the necessary db
     * change messaging.
     * @param substationId the paoId of the {@link Substation} being assigned.
     * @param areaId the paoId of the area being assigned to.
     * @return true if the assignment occurred and only one row in the db was updated, 
     * false otherwise.
     */
    public boolean assignSubstation(int areaId, int substationId);
    
    /**
     * This method removes all assignments for a given substation.
     * @param substationId the paoId of the substation whose assignments are being removed.
     * @return true if the unassignment occurred and only one row in the db was updated,
     * false otherwise.
     */
    public boolean unassignSubstation(int substationId);
    
    /**
     * This method returns a list of integers representing the paoIds of all
     * unassigned substations.
     */
    public List<Integer> getAllUnassignedSubstationIds();
    
    public SearchResult<LiteCapControlObject> getOrphans(int startIndex, int itemsPerPage);
    
    public List<Integer> getAllSpecialAreaUnassignedSubstationIds (Integer areaId);

    /**
     * This method returns the {@link Substation} specified by the parameter id if it exists,
     * null if it doesn't.
     */
    public Substation findSubstationById(int id);
}
