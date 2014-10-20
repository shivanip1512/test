package com.cannontech.common.rfn.service;

import java.util.Set;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.rfn.message.gateway.Authentication;
import com.cannontech.common.rfn.model.GatewaySettings;
import com.cannontech.common.rfn.model.GatewayUpdateException;
import com.cannontech.common.rfn.model.NetworkManagerCommunicationException;
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
    public Set<RfnGateway> getAllGateways();
    
    /**
     * Retrieves the specified gateway. If the gateway data is not cached, a request will be sent to
     * Network Manager. The request for gateway data from Network Manager is a blocking request.
     * 
     * @throws IllegalArgumentException if a gateway with the specified identifier does not exist.
     * @throws NetworkManagerCommunicationException if there is a communication error between Yukon
     *             and Network Manager.
     */
    public RfnGateway getGatewayByPaoId(PaoIdentifier paoIdentifier) throws NetworkManagerCommunicationException;
    
    /**
     * Creates a new gateway in Yukon and Network Manager.
     * 
     * @return The PaoIdentifier of the newly created gateway.
     * @throws NetworkManagerCommunicationException if there is a communication error between Yukon and Network Manager.
     * @throws GatewayUpdateException if gateway creation failed in Network Manager.
     */
    public PaoIdentifier createGateway(GatewaySettings settings) 
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
    public boolean updateGateway(RfnGateway gateway) throws NetworkManagerCommunicationException;
    
    /**
     * Delete the gateway. This will attempt to delete the gateway in Network Manager as well as Yukon.
     * 
     * @return true if the gateway was deleted successfully.
     * @throws NotFoundException if a gateway with the specified identifier does not exist.
     * @throws NetworkManagerCommunicationException if there is a communication error between Yukon and Network Manager.
     */
    public boolean deleteGateway(PaoIdentifier paoIdentifier) throws NetworkManagerCommunicationException;
    
    /**
     * Test the gateway's connection to Network Manager.
     * 
     * @return true if the gateway is connected, false if the connection failed.
     * @throws NotFoundException if a gateway with the specified identifier does not exist.
     * @throws NetworkManagerCommunicationException if there is a communication error between Yukon and Network Manager.
     */
    public boolean testConnection(PaoIdentifier paoIdentifier) throws NetworkManagerCommunicationException;
    
    /**
     * Test a gateway connection, without requiring the gateway to already exist in Yukon.
     * @return true if the connection succeeded, false if the connection failed.
     * @throws NetworkManagerCommunicationException if there is a communication error between Yukon and Network Manager.
     */
    boolean testConnection(String ipAddress, Authentication user, Authentication admin,
                           Authentication superAdmin) throws NetworkManagerCommunicationException;
    
    /**
     * Initiates a gateway "connect" action in Network Manager.
     * 
     * @return true if the gateway was connected successfully.
     * @throws NotFoundException if a gateway with the specified identifier does not exist.
     * @throws NetworkManagerCommunicationException if there is a communication error between Yukon and Network Manager.
     */
    public boolean connectGateway(PaoIdentifier paoIdentifier) throws NetworkManagerCommunicationException;
    
    /**
     * Initiates a gateway "disconnect" action in Network Manager.
     * 
     * @return true if the gateway was disconnected successfully.
     * @throws NotFoundException if a gateway with the specified identifier does not exist.
     * @throws NetworkManagerCommunicationException if there is a communication error between Yukon and Network Manager.
     */
    public boolean disconnectGateway(PaoIdentifier paoIdentifier) throws NetworkManagerCommunicationException;
    
    /**
     * Initiates gateway data collection in Network Manager.
     * 
     * @return true if the data collection action succeeded.
     * @throws NotFoundException if a gateway with the specified identifier does not exist.
     * @throws NetworkManagerCommunicationException if there is a communication error between Yukon and Network Manager.
     */
    public boolean collectData(PaoIdentifier paoIdentifier) throws NetworkManagerCommunicationException;
    
    /**
     * Sets the data collection schedule for the specified gateway.
     * 
     * @return true if the schedule was set successfully.
     * @throws NotFoundException if a gateway with the specified identifier does not exist.
     * @throws NetworkManagerCommunicationException if there is a communication error between Yukon and Network Manager.
     */
    public boolean setCollectionSchedule(PaoIdentifier paoIdentifier, String cronExpression) 
            throws NetworkManagerCommunicationException;
    
    /**
     * Delete the data collection schedule for the specified gateway, preventing any subsequent data collection.
     * 
     * @return true if the data collection schedule was deleted successfully.
     * @throws NotFoundException if a gateway with the specified identifier does not exist.
     * @throws NetworkManagerCommunicationException if there is a communication error between Yukon and Network Manager.
     */
    public boolean deleteCollectionSchedule(PaoIdentifier paoIdentifier) throws NetworkManagerCommunicationException;
    
}
