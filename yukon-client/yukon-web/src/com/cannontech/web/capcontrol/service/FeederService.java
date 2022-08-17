package com.cannontech.web.capcontrol.service;

import java.util.List;

import org.springframework.dao.EmptyResultDataAccessException;

import com.cannontech.database.data.capcontrol.CapControlFeeder;
import com.cannontech.web.capcontrol.models.Assignment;
import com.cannontech.web.capcontrol.models.CapBankAssignment;
import com.cannontech.web.capcontrol.models.ViewableCapBank;

public interface FeederService {

    /**
     * Retrieves a complete CapControlFeeder
     */
    CapControlFeeder get(int id);

    /**
     * Saves all components of a CapControlFeeder
     * @return paoId of the feeder (necessary for create)
     */
    int save(CapControlFeeder feeder);

    /**
     * Delete the feeder with the given id
     * @return true when successful, false otherwise
     */
    boolean delete(int feederId); 
    
    /**
     * Returns whether the capbanks assigned to feeder are assigned within zone with the given feederId
     * @return true when successful, false otherwise
     */
    boolean isCapBanksAssignedToZone(int feederId) throws EmptyResultDataAccessException;

    /**
     * @return All cap banks, as {@link ViewableCapBank}s,  attached to the feeder with given feederId in display order<br>
     * <i>Note:</i> Strategy-based fields are not filled in if the feeder itself is an orphan.
     */
    List<ViewableCapBank> getCapBanksForFeeder(int busId);

    /**
     * @return All cap banks not attached to any feeder 
     */
    List<CapBankAssignment> getUnassignedCapBanks();

    /**
     * Assigns exactly the list of cap bank ids to the feeder, removing any other existing assignments.
     * The assignments are done in order given with increasing consecutive displayOrders.
     */
    void assignCapBanks(int feederId, List<Integer> capBankIds, List<Integer> closeOrder, List<Integer> tripOrder);
    /**
     * @param feederId
     * @return All cap banks, as {@link Assignment}s, attached to the feeder with given feederId.
     */
    List<CapBankAssignment> getAssignedCapBanksForFeeder(int feederId);

    /**
     * Returns whether the capbank is assigned to zone.
     */
    boolean isCapBanksAssignedToZone(List<Integer> availableCapBanksIds);
}