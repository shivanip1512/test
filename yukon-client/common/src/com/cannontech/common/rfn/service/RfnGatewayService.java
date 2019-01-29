package com.cannontech.common.rfn.service;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.rfn.message.gateway.DataType;
import com.cannontech.common.rfn.message.gateway.GatewayConfigResult;
import com.cannontech.common.rfn.message.gateway.GatewayUpdateResult;
import com.cannontech.common.rfn.model.GatewayConfiguration;
import com.cannontech.common.rfn.model.GatewaySettings;
import com.cannontech.common.rfn.model.GatewayUpdateException;
import com.cannontech.common.rfn.model.NmCommunicationException;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.common.rfn.model.RfnGateway;
import com.cannontech.core.dao.DuplicateException;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.google.common.collect.Multimap;

/**
 * Handles most work relating to RFN gateways.
 * <p>
 * Yukon only stores a simple device & rfnIdentifier for gateways (and optionally, latitude and longitude). The rest of 
 * the gateway data is stored in Network Manager and only cached by Yukon.
 * 
 * @see {@link GatewayArchiveRequestListener} Handles incoming <code>GatewayArchiveRequest</code>s.
 * @see {@link GatewayDataRequestListener} Handles incoming <code>GatewayDataResponse</code>s
 * @see {@link RfnDeviceCreationService#createGateway(RfnIdentifier)} Used to create new gateways.
 */
public interface RfnGatewayService {
    
    /**
     * Retrieves all gateways that have paos in the Yukon database. If the gateway data is not
     * cached, it will be set as null in the RfnGateway, and the cache will be updated in a separate thread.
     */
    Set<RfnGateway> getAllGateways();
    
    /**
     * Retrieves all "legacy" gateways (aka gateway 1.5s) that have paos in the Yukon database. If the gateway data is 
     * not cached, it will be set as null in the RfnGateway, and the cache will be updated in a separate thread.
     */
    Set<RfnGateway> getAllLegacyGateways();
    
    /**
     * Retrieves all non-"legacy" gateways (newer than gateway 1.5s) that have paos in the Yukon database. If the 
     * gateway data is not cached, it will be set as null in the RfnGateway, and the cache will be updated in a separate 
     * thread.
     */
    Set<RfnGateway> getAllNonLegacyGateways();
    
    /**
     * Retrieves gateways of the specified types that have paos in the Yukon database. 
     * If the gateway data is not cached, a request will be sent to Network Manager. 
     * The request for gateway data from Network Manager is a blocking request.
     * 
     * @throws NmCommunicationException if there is a communication error between Yukon
     *             and Network Manager.
     */
    Set<RfnGateway> getGatewaysWithData(Collection<PaoType> paoType) throws NmCommunicationException;

    /**
     * Retrieves all gateways that have paos in the Yukon database.
     * If the gateway data is not cached, a request will be sent to Network Manager.
     * The request for gateway data from Network Manager is a blocking request.
     * The upgrade version field is set based on the update server set on the gateway.
     *
     * @throws NmCommunicationException if there is a communication error between Yukon
     *             and Network Manager.
     */
    Set<RfnGateway> getAllGatewaysWithUpdateServer() throws NmCommunicationException;

    /**
     * Retrieves the specified gateway. If the gateway data is not cached, a request will be sent to
     * Network Manager. The request for gateway data from Network Manager is a blocking request.
     * 
     * @throws IllegalArgumentException if a gateway with the specified identifier does not exist.
     * @throws NmCommunicationException if there is a communication error between Yukon
     *             and Network Manager.
     */
    RfnGateway getGatewayByPaoIdWithData(int paoId) throws NmCommunicationException;
    
    /**
     * Retrieves the specified gateway. If the gateway data is not cached, the returned
     * gateways data field will be null. Use {@link #getGatewayByPaoIdWithData(int)} if you require data.
     * 
     * @throws IllegalArgumentException if a gateway with the specified identifier does not exist.
     */
    RfnGateway getGatewayByPaoId(int paoId);
    
    /**
     * Gets all RfnGateways with the specified paoids. If any ids are invalid, they will be ignored. If the gateway
     * data is not cached, it will be set as null in the RfnGateway, and the cache will be updated in a separate thread.
     */
    Set<RfnGateway> getGatewaysByPaoIds(Iterable<Integer> paoIds);
    
    /**
     * Gets all RfnGateways with the specified paoids. If any ids are invalid, they will be ignored. If the gateway
     * data is not cached, the call will block while it is retrieved.
     * @throws NmCommunicationException if a communication error occurs when requesting gateway data from Network Manager.
     */
    Set<RfnGateway> getGatewaysByPaoIdsWithData(Iterable<Integer> paoIds) throws NmCommunicationException;
    
    /**
     * Get a map of paoId to gateway for all gateways. If the gateway data is not cached, it will be set as null in the
     * RfnGateway, and the cache will be updated in a separate thread.
     */
    Map<Integer, RfnGateway> getAllGatewaysByPaoId();
    
    /**
     * Creates a new gateway in Yukon and Network Manager. This is a blocking operation.
     * 
     * @return The {@link RfnDevice} of the newly created gateway.
     * @throws NmCommunicationException if there is a communication error between Yukon and Network Manager.
     * @throws GatewayUpdateException if gateway creation failed in Network Manager.
     */
    RfnDevice createGateway(GatewaySettings settings, LiteYukonUser user) throws NmCommunicationException,
            GatewayUpdateException;
    
    /**
     * Update the gateway. If the name or location are updated, they will be stored in the Yukon
     * database, but changes will not propagate back to Network Manager. All other changes will be
     * sent back to Network Manager and updated in Yukon's cache. Any null parameters will not be
     * updated. This is a blocking operation.
     * 
     * @return The GatewayUpdateResult indicating the success or failure reason.
     * @throws NmCommunicationException if there is a communication error between Yukon
     *             and Network Manager.
     */
    GatewayUpdateResult updateGateway(RfnGateway gateway, LiteYukonUser user) throws NmCommunicationException;
    
    /**
     * Delete the gateway. This will attempt to delete the gateway in Network Manager as well as Yukon.
     * 
     * @return true if the gateway was deleted successfully.
     * @throws NotFoundException if a gateway with the specified identifier does not exist.
     * @throws NmCommunicationException if there is a communication error between Yukon and Network Manager.
     */
    boolean deleteGateway(PaoIdentifier paoIdentifier) throws NmCommunicationException;
    
    /**
     * Test the connection between Network Manager and the gateway using the given IP address,
     * user name and password.
     * <p>
     * Notes: If Network Manager is currently connected to that gateway, Network Manager will
     * disconnect from that gateway and then attempt to connect using the information given. If the
     * connection is successful, Network Manager will disconnect from that gateway.
     * 
     * @return true if the connection was successful, false if the connection failed.
     * @throws NotFoundException if a gateway with the specified identifier does not exist.
     * @throws NmCommunicationException if there is a communication error between Yukon and Network
     *             Manager.
     */
    boolean testConnection(int deviceId, String ipAddress, String username, String password) 
            throws NmCommunicationException;
    
    /**
     * Test the connection between Network Manager and the gateway using the default access level
     * account (ADMIN).
     * <p>
     * Notes: If Network Manager is currently connected to that gateway, Network Manager will
     * disconnect from that gateway first. If the connection is successful, Network Manager will
     * disconnect from that gateway.
     * 
     * @return true if the connection was successful, false if the connection failed.
     * @throws NmCommunicationException
     */
    boolean testConnection(int deviceId) throws NmCommunicationException;
    
    /**
     * Initiates a gateway "connect" action in Network Manager.
     * 
     * @return true if the gateway was connected successfully.
     * @throws NotFoundException if a gateway with the specified identifier does not exist.
     * @throws NmCommunicationException if there is a communication error between Yukon and Network Manager.
     */
    boolean connectGateway(PaoIdentifier paoIdentifier) throws NmCommunicationException;
    
    /**
     * Initiates a gateway "disconnect" action in Network Manager.
     * 
     * @return true if the gateway was disconnected successfully.
     * @throws NotFoundException if a gateway with the specified identifier does not exist.
     * @throws NmCommunicationException if there is a communication error between Yukon and Network Manager.
     */
    boolean disconnectGateway(PaoIdentifier paoIdentifier) throws NmCommunicationException;
    
    /**
     * Initiates gateway data collection in Network Manager.
     * 
     * @return true if the data collection action succeeded.
     * @throws NotFoundException if a gateway with the specified identifier does not exist.
     * @throws NmCommunicationException if there is a communication error between Yukon and Network Manager.
     */
    boolean collectData(PaoIdentifier paoIdentifier, DataType... types) throws NmCommunicationException;
    
    /**
     * Sets the data collection schedule for the specified gateway.
     * 
     * @return true if the schedule was set successfully.
     * @throws NotFoundException if a gateway with the specified identifier does not exist.
     * @throws NmCommunicationException if there is a communication error between Yukon and Network Manager.
     */
    boolean setCollectionSchedule(PaoIdentifier paoIdentifier, String cronExpression) 
            throws NmCommunicationException;
    
    /**
     * Delete the data collection schedule for the specified gateway, preventing any subsequent data collection.
     * 
     * @return true if the data collection schedule was deleted successfully.
     * @throws NotFoundException if a gateway with the specified identifier does not exist.
     * @throws NmCommunicationException if there is a communication error between Yukon and Network Manager.
     */
    boolean deleteCollectionSchedule(PaoIdentifier paoIdentifier) throws NmCommunicationException;
    
    /** Clears gateway data cache, mostly for testing/debugging */
    void clearCache();
    
    /** Converts an {@link RfnGateway} to a {@link GatewaySettings} */
    GatewaySettings gatewayAsSettings(RfnGateway gateway);

    /**
     * Update the gateways. For each gateway,
     * If the name or location are updated, they will be stored in the Yukon database,
     * but changes will not propagate back to Network Manager. All other changes will be
     * sent back to Network Manager and updated in Yukon's cache. Any null parameters will not be
     * updated. This is a blocking operation.
     * 
     * @return The GatewayUpdateResult indicating the success or failure reason.
     * @throws NmCommunicationException if there is a communication error between Yukon
     *             and Network Manager.
     */
    void updateGateways(Iterable<RfnGateway> gateways, LiteYukonUser user) throws NmCommunicationException;
    
    /**
     * Checks the specified gateways to determine if any colors are used by more than one. If any duplications are found,
     * the returned multimap will include the duplicated colors and all gateways using each duplicated color.
     * @param gateways A collection of gateways with gateway data. Gateways without data will be ignored.
     * @return A multimap of color -> gateway for colors that are duplicated. 
     */
    Multimap<Short, RfnGateway> getDuplicateColorGateways(Collection<RfnGateway> gateways);
    
    /**
     * Generates point data for an attribute and sends it to dispatch.
     * If point doesn't exits, creates a point.
     * 
     * @param tagsPointMustArchive - sets Pointdata.setTagsPointMustArchive to true or false.
     */
    void generatePointData(RfnDevice gateway, BuiltInAttribute attribute, double value,
                           boolean tagsPointMustArchive);
    /**
     * Generates point data for an attribute and allows time to be set
     */
    void generatePointData(RfnDevice gateway, BuiltInAttribute attribute, double value, boolean tagsPointMustArchive, Long time);

    /**
     * Sends Ipv6 prefix to NM.
     * 
     * @throws NmCommunicationException - didn't receive reply from NM
     * @throws DuplicateException - Ipv6 prefix already exists
     * @throws IllegalArgumentException - Ipv6 prefix is not in HEX format
     */
    GatewayConfigResult updateIpv6Prefix(RfnGateway gateway, String newIpv6Prefix)
            throws NmCommunicationException, DuplicateException, IllegalArgumentException;

    /** Converts an {@link RfnGateway} to a {@link GatewayConfiguration} */
    GatewayConfiguration gatewayAsConfiguration(RfnGateway gateway);

}