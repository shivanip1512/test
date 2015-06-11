package com.cannontech.capcontrol.dao;

import java.util.List;
import java.util.Map;

import com.cannontech.capcontrol.model.LiteCapControlObject;
import com.cannontech.capcontrol.model.Substation;

public interface SubstationDao {
    
    /**
     * This method assigns a {@link Substation} to an Area and performs the necessary db
     * change messaging.
     * 
     * Works for both areas and special areas.
     * 
     * @param substationId the paoId of the {@link Substation} being assigned.
     * @param areaName the name of the area being assigned to.
     * @return true if the assignment occurred and only one row in the db was updated, 
     * false otherwise.
     */
    public boolean assignSubstation(int substationId, String areaName);
    
    /**
     * This method assigns a {@link Substation} to an Area and performs the necessary db
     * change messaging.
     * 
     * Works for both areas and special areas.
     * 
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
    
    public List<LiteCapControlObject> getOrphans();
    
    public List<Integer> getAllSpecialAreaUnassignedSubstationIds (Integer areaId);

    /**
     * This method returns the {@link Substation} specified by the parameter id if it exists,
     * null if it doesn't.
     */
    public Substation findSubstationById(int id);
    
    /**
     * Unassigns all substations from an area.
     * Works for both areas and special areas.
     */
    boolean unassignSubstations(int areaId);
    
    /**
     * Removes any existing sub to area assignments on the area and 
     * adds new sub to area assignments for the subs in the list.
     * The order of the sub id's is used as the display order stored
     * for the sub to area assignments.  Display order will be 1 based.
     * 
     * Works for both areas and special areas.
     * 
     * @param areaId - The area to update sub assignments.
     * @param subs - The ordered list of sub id's to assign to the area.
     */
    void updateSubAssignments(int areaId, List<Integer> subs);
    
    /**
     * Returns a map of sub id to display order for all subs assigned to this area.
     * 
     * Works for both areas and special areas.
     * 
     * @param areaId - The area to lookup sub display orders on. 
     * @return orders - A map of sub id to display order.
     */
    Map<Integer, Double> getSubOrders(int areaId);
    
    /**
     * Returns list of substations from database that are assigned to the area.
     * Useful when you can't trust the {@link CapControlCache} to be updated in time
     * by the cap control server.
     * 
     * Works for special areas as well.
     */
    List<Substation> getSubstationsByArea(int areaId);
}
