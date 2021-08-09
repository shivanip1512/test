package com.cannontech.common.rfn.service;

import java.util.List;
import java.util.Set;

import com.cannontech.common.pao.PaoType;
import com.cannontech.common.rfn.model.RfnDeviceSearchCriteria;
import com.cannontech.common.rfn.model.RfnRelay;

/**
 * Handles most work relating to RFN relays.
 */
public interface RfnRelayService {
    
    /**
     * Retrieves all relays that have paos in the Yukon database. 
     */
    Set<RfnRelay> getAllRelays();

    /**
     * Retrieves all cellular relays that have PAOs in the Yukon database.
     */
    Set<RfnRelay> getRelaysOfType(PaoType type);

    /**
     * Searches relays with the given criteria
     */
    Set<RfnRelay> searchRelays(RfnDeviceSearchCriteria critera, List<PaoType> types);
    
    /**
     * Deletes the relay with the given id
     */
    boolean deleteRelay(int id);

}