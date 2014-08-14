package com.cannontech.common.rfn.service;

import java.util.Set;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.model.PaoLocation;
import com.cannontech.common.rfn.model.NetworkManagerCommunicationException;
import com.cannontech.common.rfn.model.RfnGateway;

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
     * Retrieves all gateways that have paos in the Yukon database. If the gateway data is not cached, a request will be
     * sent to Network Manager.
     */
    public Set<RfnGateway> getAllGateways();
    
    /**
     * Retrieves the specified gateway. If the gateway data is not cached, a request will be sent to Network Manager.
     * 
     * @throws IllegalArgumentException if a gateway with the specified identifier does not exist.
     * @throws NetworkManagerCommunicationException if there is a communication error between Yukon and Network Manager.
     */
    public RfnGateway getGatewayByPaoId(PaoIdentifier paoIdentifier);
    
    /**
     * Creates a new gateway in Yukon and Network Manager.
     * 
     * @param location if null, no location information will be saved. Otherwise, this should contain latitude and
     * longitude (any paoidentifier will be overwritten with the newly created pao's id).
     * @return true if the creation succeeded.
     * @throws NetworkManagerCommunicationException if there is a communication error between Yukon and Network Manager.
     */
    public boolean createGateway(String name, String ipAddress, String username, String password, PaoLocation location);
    
    /**
     * Update the gateway. If the name or location are updated, they will be stored in the Yukon database, but changes
     * will not propagate back to Network Manager. All other changes will be sent back to Network Manager and updated in
     * Yukon's cache.
     * 
     * @return true if the gateway was updated successfully.
     * @throws IllegalArgumentException if a gateway with the specified identifier does not exist.
     * @throws NetworkManagerCommunicationException if there is a communication error between Yukon and Network Manager.
     */
    public boolean updateGateway(RfnGateway gateway);
    
    /**
     * Delete the gateway. This will attempt to delete the gateway in Network Manager as well as Yukon.
     * 
     * @return true if the gateway was deleted successfully.
     * @throws IllegalArgumentException if a gateway with the specified identifier does not exist.
     * @throws NetworkManagerCommunicationException if there is a communication error between Yukon and Network Manager.
     */
    public boolean deleteGateway(PaoIdentifier paoIdentifier);
    
    /**
     * Test the gateway's connection to Network Manager.
     * 
     * @return true if the gateway is connected, false if the connection failed.
     * @throws IllegalArgumentException if a gateway with the specified identifier does not exist.
     * @throws NetworkManagerCommunicationException if there is a communication error between Yukon and Network Manager.
     */
    public boolean testConnection(PaoIdentifier paoIdentifier);
    
    /**
     * Initiates a gateway "connect" action in Network Manager.
     * 
     * @return true if the gateway was connected successfully.
     * @throws IllegalArgumentException if a gateway with the specified identifier does not exist.
     * @throws NetworkManagerCommunicationException if there is a communication error between Yukon and Network Manager.
     */
    public boolean connectGateway(PaoIdentifier paoIdentifier);
    
    /**
     * Initiates a gateway "disconnect" action in Network Manager.
     * 
     * @return true if the gateway was disconnected successfully.
     * @throws IllegalArgumentException if a gateway with the specified identifier does not exist.
     * @throws NetworkManagerCommunicationException if there is a communication error between Yukon and Network Manager.
     */
    public boolean disconnectGateway(PaoIdentifier paoIdentifier);
    
    /**
     * Initiates gateway data collection in Network Manager.
     * 
     * @return true if the data collection action succeeded.
     * @throws IllegalArgumentException if a gateway with the specified identifier does not exist.
     * @throws NetworkManagerCommunicationException if there is a communication error between Yukon and Network Manager.
     */
    public boolean collectData(PaoIdentifier paoIdentifier);
    
    /**
     * Sets the data collection schedule for the specified gateway.
     * 
     * @return true if the schedule was set successfully.
     * @throws IllegalArgumentException if a gateway with the specified identifier does not exist.
     * @throws NetworkManagerCommunicationException if there is a communication error between Yukon and Network Manager.
     */
    public boolean setCollectionSchedule(PaoIdentifier paoIdentifier, String cronExpression);
    
    /**
     * Delete the data collection schedule for the specified gateway, preventing any subsequent data collection.
     * 
     * @return true if the data collection schedule was deleted successfully.
     * @throws IllegalArgumentException if a gateway with the specified identifier does not exist.
     * @throws NetworkManagerCommunicationException if there is a communication error between Yukon and Network Manager.
     */
    public boolean deleteCollectionSchedule(PaoIdentifier paoIdentifier);
    
}
