package com.cannontech.common.rfn.dao;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.rfn.dao.impl.GatewayDataException;
import com.cannontech.common.rfn.message.gateway.GatewayFirmwareUpdateRequestResult;
import com.cannontech.common.rfn.model.FirmwareUpdateServerInfo;
import com.cannontech.common.rfn.model.RfnGateway;
import com.cannontech.common.rfn.model.RfnGatewayFirmwareUpdateResult;
import com.cannontech.common.rfn.model.RfnGatewayFirmwareUpdateSummary;

/**
 * Handles the creation, update and retrieval of RFN firmware upgrade information from the database and Network
 * Manager.
 */
public interface RfnGatewayFirmwareUpgradeDao {
    
    /**
     * Create a new RFN firmware upgrade in the database, and entries for each gateway.
     * @return The upgradeId used to reference this upgrade.
     */
    int createUpgrade(Collection<RfnGateway> gateways) throws GatewayDataException;
    
    /**
     * Sets the completed status of the specified gateway in the specified update, based on the request result.
     */
    void complete(int updateId, PaoIdentifier gatewayId, GatewayFirmwareUpdateRequestResult result);
    
    /**
     * @return A List of summary objects for all upgrades.
     */
    List<RfnGatewayFirmwareUpdateSummary> getAllUpdateSummaries();
    
    /**
     * @return A List of upgrade results for the specified upgrade.
     */
    List<RfnGatewayFirmwareUpdateResult> getUpdateResults(int updateId);
    
    /**
     * @return A Map of gateway paoIds and firmware update server information for the specified gateways.
     * @throws GatewayDataException If gateway data cannot be retrieved from the cache, and the subsequent request to
     * Network Manager fails.
     */
    Map<Integer, FirmwareUpdateServerInfo> getAllFirmwareUpdateServerInfo(Collection<RfnGateway> gateways) 
            throws GatewayDataException;
}
