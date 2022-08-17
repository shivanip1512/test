package com.cannontech.web.capcontrol.service;

import java.util.List;

import com.cannontech.database.data.capcontrol.CapControlSubBus;
import com.cannontech.web.capcontrol.models.Assignment;
import com.cannontech.web.capcontrol.models.ViewableFeeder;

public interface BusService {

    /**
     * Retrieves a complete CapControlSubBus
     */
    CapControlSubBus get(int id);

    /**
     * Saves all components of a CapControlSubBus
     * @return paoId of the bus (necessary for create)
     */
    int save(CapControlSubBus bus);

    /**
     * Delete the bus with the given id
     */
    void delete(int busId);

    /**
     * @return All feeders, as {@link ViewableFeeder}s,  attached to the bus with given busId in display order<br>
     * <i>Note:</i> Strategy-based fields are not filled in if the bus itself is an orphan.
     */
    List<ViewableFeeder> getFeedersForBus(int busId);

    /**
     * @param busId
     * @return All feeders, as {@link Assignment}s, attached to the bus with given busId.
     */
    List<Assignment> getAssignedFeedersFor(int busId);

    /**
     * @return All feeders not attached to any bus 
     */
    List<Assignment> getUnassignedFeeders();

    /**
     * Assigns exactly the list of feederIds to the bus, removing any other existing assignments.
     * The assignments are done in order given with increasing consecutive displayOrders.
     */
    void assignFeeders(int busId, Iterable<Integer> feederIds);
    
    /**
     * Saves the schedule assignments for the bus
     */
    void saveSchedules(CapControlSubBus bus);

    /**
     * Returns whether the capbank associated with feeder is assigned to zone.
     */
    boolean isCapBanksAssignedToZone(List<Integer> availableFeederIds);

}