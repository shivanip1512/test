package com.cannontech.common.rfn.service;

import java.util.Map;
import java.util.Set;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.rfn.message.gateway.DataType;
import com.cannontech.common.rfn.model.GatewaySettings;
import com.cannontech.common.rfn.model.GatewayUpdateException;
import com.cannontech.common.rfn.model.NetworkManagerCommunicationException;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.common.rfn.model.RfnGateway;
import com.cannontech.core.dao.NotFoundException;

/**
 * Handles most work relating to RFN gateways.
 * <p>
 * Yukon only stores a simple device & rfnIdentifier for gateways (and optionally, latitude and longitude). The rest of 
 * the gateway data is stored in Network Manager and only cached by Yukon.
 * 
 * @see {@link GatewayArchiveRequestListener} Handles incoming <code>GatewayArchiveRequest</code>s.
 * @see {@link RfnDeviceCreationService#createGateway(RfnIdentifier)} Used to create new gateways.
 */
public interface RfnGatewayService {
    
    /**
     * Retrieves all gateways that have paos in the Yukon database. If the gateway data is not
     * cached, it will be set as null in the RfnGateway, and the cache will be updated in a separate thread.
     */
    Set<RfnGateway> getAllGateways();
    
    /**
     * Retrieves the specified gateway. If the gateway data is not cached, a request will be sent to
     * Network Manager. The request for gateway data from Network Manager is a blocking request.
     * 
     * @throws IllegalArgumentException if a gateway with the specified identifier does not exist.
     * @throws NetworkManagerCommunicationException if there is a communication error between Yukon
     *             and Network Manager.
     */
    RfnGateway getGatewayByPaoId(int paoId) throws NetworkManagerCommunicationException;
    
    /**
     * Creates a new gateway in Yukon and Network Manager.
     * 
     * @return The {@link RfnDevice} of the newly created gateway.
     * @throws NetworkManagerCommunicationException if there is a communication error between Yukon and Network Manager.
     * @throws GatewayUpdateException if gateway creation failed in Network Manager.
     */
    RfnDevice createGateway(GatewaySettings settings) 
            throws NetworkManagerCommunicationException, GatewayUpdateException;
    
    /**
     * Update the gateway. If the name or location are updated, they will be stored in the Yukon
     * database, but changes will not propagate back to Network Manager. All other changes will be
     * sent back to Network Manager and updated in Yukon's cache. Any null parameters will not be
     * updated. This is a blocking operation.
     * 
     * @return true if the gateway was updated successfully.
     * @throws NetworkManagerCommunicationException if there is a communication error between Yukon
     *             and Network Manager.
     */
    boolean updateGateway(RfnGateway gateway) throws NetworkManagerCommunicationException;
    
    /**
     * Delete the gateway. This will attempt to delete the gateway in Network Manager as well as Yukon.
     * 
     * @return true if the gateway was deleted successfully.
     * @throws NotFoundException if a gateway with the specified identifier does not exist.
     * @throws NetworkManagerCommunicationException if there is a communication error between Yukon and Network Manager.
     */
    boolean deleteGateway(PaoIdentifier paoIdentifier) throws NetworkManagerCommunicationException;
    
    /**
     * Test the gateway's connection to Network Manager.
     * 
     * @return true if the gateway is connected, false if the connection failed.
     * @throws NotFoundException if a gateway with the specified identifier does not exist.
     * @throws NetworkManagerCommunicationException if there is a communication error between Yukon and Network Manager.
     */
    boolean testConnection(int deviceId, String ipAddress, String username, String password) 
            throws NetworkManagerCommunicationException;
    
    /**
     * Initiates a gateway "connect" action in Network Manager.
     * 
     * @return true if the gateway was connected successfully.
     * @throws NotFoundException if a gateway with the specified identifier does not exist.
     * @throws NetworkManagerCommunicationException if there is a communication error between Yukon and Network Manager.
     */
    boolean connectGateway(PaoIdentifier paoIdentifier) throws NetworkManagerCommunicationException;
    
    /**
     * Initiates a gateway "disconnect" action in Network Manager.
     * 
     * @return true if the gateway was disconnected successfully.
     * @throws NotFoundException if a gateway with the specified identifier does not exist.
     * @throws NetworkManagerCommunicationException if there is a communication error between Yukon and Network Manager.
     */
    boolean disconnectGateway(PaoIdentifier paoIdentifier) throws NetworkManagerCommunicationException;
    
    /**
     * Initiates gateway data collection in Network Manager.
     * 
     * @return true if the data collection action succeeded.
     * @throws NotFoundException if a gateway with the specified identifier does not exist.
     * @throws NetworkManagerCommunicationException if there is a communication error between Yukon and Network Manager.
     */
    boolean collectData(PaoIdentifier paoIdentifier, DataType... types) throws NetworkManagerCommunicationException;
    
    /**
     * Sets the data collection schedule for the specified gateway.
     * 
     * @return true if the schedule was set successfully.
     * @throws NotFoundException if a gateway with the specified identifier does not exist.
     * @throws NetworkManagerCommunicationException if there is a communication error between Yukon and Network Manager.
     */
    boolean setCollectionSchedule(PaoIdentifier paoIdentifier, String cronExpression) 
            throws NetworkManagerCommunicationException;
    
    /**
     * Delete the data collection schedule for the specified gateway, preventing any subsequent data collection.
     * 
     * @return true if the data collection schedule was deleted successfully.
     * @throws NotFoundException if a gateway with the specified identifier does not exist.
     * @throws NetworkManagerCommunicationException if there is a communication error between Yukon and Network Manager.
     */
    boolean deleteCollectionSchedule(PaoIdentifier paoIdentifier) throws NetworkManagerCommunicationException;
    
    /** Clears gateway data cache, mostly for testing/debugging */
    void clearCache();
    
    /**
     * Gets all RfnGateways with the specified paoids. If any ids are invalid, they will be ignored.
     */
    Set<RfnGateway> getGatewaysByPaoIds(Iterable<Integer> paoIds);
    
    /**
     * Get a map of paoId to gateway for all gateways.
     */
    Map<Integer, RfnGateway> getAllGatewaysByPaoId();
    
}