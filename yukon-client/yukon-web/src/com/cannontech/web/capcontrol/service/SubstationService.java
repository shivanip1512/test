package com.cannontech.web.capcontrol.service;

import java.util.List;

import com.cannontech.database.data.capcontrol.CapControlSubstation;
import com.cannontech.web.capcontrol.models.Assignment;
import com.cannontech.web.capcontrol.models.ViewableCapBank;
import com.cannontech.web.capcontrol.models.ViewableFeeder;
import com.cannontech.web.capcontrol.models.ViewableSubBus;

public interface SubstationService {

    /**
     * Retrieves a Substation
     */
    CapControlSubstation get(int id);

    /**
     * Saves all components of a Substation
     * @return paoId of the substation (necessary for create)
     */
    int save(CapControlSubstation substation);

    /**
     * Delete the substation with the given id
     */
    void delete(int substationId);

    /**
     * @param substationId
     * @return All buses, as {@link Assignment}s, attached to the substation with given substationId.
     */
    List<Assignment> getAssignedBusesFor(int substationId);

    /**
     * @return All buses not attached to any substation 
     */
    List<Assignment> getUnassignedBuses();

    /**
     * Assigns exactly the list of busIds to the substation, removing any other existing assignments.
     * The assignments are done in order given with increasing consecutive displayOrders.
     */
    void assignBuses(int substationId, Iterable<Integer> busIds);
    
    /**
     * Gets the Buses assigned to the Substation
     * @param substationId
     * @return All buses, as {@link ViewableSubBus}s, attached to the substation with given substationId.
     */
    List<ViewableSubBus> getBusesForSubstation(int substationId);
    
    /**
     * Gets the Feeders assigned to the given Substation Buses
     * @param subBuses
     * @return All feeders, as {@link ViewableFeeder}s, attached to the substation buses.
     */
    List<ViewableFeeder> getFeedersForSubBuses(List<ViewableSubBus> subBuses);

    /**
     * Gets the Cap Banks assigned to the given Feeders
     * @param feeders
     * @return All capbanks, as {@link ViewableCapBank}s, attached to the feeders.
     */
    List<ViewableCapBank> getCapBanksForFeeders(List<ViewableFeeder> feeders);

    void assignBus(int substationId, int busId);
    
}