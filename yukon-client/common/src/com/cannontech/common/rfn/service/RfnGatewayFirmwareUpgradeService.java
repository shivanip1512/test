package com.cannontech.common.rfn.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.cannontech.common.rfn.dao.impl.GatewayDataException;
import com.cannontech.common.rfn.model.NmCommunicationException;
import com.cannontech.common.rfn.model.RfnGateway;
import com.cannontech.common.rfn.model.RfnGatewayFirmwareUpdateResult;
import com.cannontech.common.rfn.model.RfnGatewayFirmwareUpdateSummary;

/**
 * Service that handles sending RFN firmware upgrades and retrieving the status of those upgrades. 
 */
public interface RfnGatewayFirmwareUpgradeService {
    
    /**
     * Sends a request to Network Manager to initiate a firmware upgrade operation, and adds information to the 
     * database to track the operation.
     * @return The upgrade id used to request upgrade results.
     */
    int sendFirmwareUpgrade(Collection<RfnGateway> gateways) throws GatewayDataException;
    
    /**
     * Retrieves the list of results (per gateway) for the specified firmware upgrade operation.
     */
    List<RfnGatewayFirmwareUpdateResult> getFirmwareUpgradeResults(int updateId);
    
    /**
     * Retrieves summaries for all firmware upgrade operations.
     */
    List<RfnGatewayFirmwareUpdateSummary> getFirmwareUpdateSummaries();
    
    /**
     * This method communicates to Network Manager and fetches the available version for update servers.
     * @throws NmCommunicationException If there is an error communicating with Network Manager.
     */
    Map<String, String> getFirmwareUpdateServerVersions() throws NmCommunicationException;
    
}
