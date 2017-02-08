package com.cannontech.common.rfn.service;

import java.util.Set;

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
     * Searches relays with the given criteria
     */
    Set<RfnRelay> searchRelays(RfnDeviceSearchCriteria critera);
    
    /**
     * Deletes the relay with the given id
     */
    boolean deleteRelay(int id);

}