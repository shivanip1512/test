package com.cannontech.common.rfn.service;

import java.util.Collection;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.rfn.model.NmCommunicationException;
import com.cannontech.common.rfn.model.RfnGatewayData;
import com.google.common.cache.LoadingCache;

/**
 * A cache of RfnGatewayData - essentially all the gateway data that Network Manager stores and Yukon doesn't.
 * 
 * Backed internally by a loading cache. If data is requested for a gateway that isn't cached, it will be automatically
 * requested from Network Manager. The method call will block until the data is returned or the requests time out.
 */
public interface RfnGatewayDataCache {
    
    /**
     * Gets RfnGatewayData for a single id. If no data is present for that id, execution blocks while the cache requests
     * an update from Network Manager.
     * @throws NmCommunicationException if an error occurs retrieving data from Network Manager.
     */
    public RfnGatewayData get(PaoIdentifier paoIdentifier) throws NmCommunicationException;
    
    /**
     * Gets RfnGatewayData for a single id. This is a non-blocking call - if no data is present for that id, returns 
     * null immediately and updates the cache in a separate thread.
     */
    public RfnGatewayData getIfPresent(PaoIdentifier paoIdentifier);
    
    /**
     * Gets all RfnGatewayData for the specified ids.
     * @throws NmCommunicationException if an error occurs retrieving data from Network Manager.
     */
    public Collection<RfnGatewayData> getAll(Iterable<? extends PaoIdentifier> paoIdentifiers) 
            throws NmCommunicationException;
    
    /**
     * Put an RfnGatewayData value into the cache.
     */
    public void put(PaoIdentifier paoIdentifier, RfnGatewayData data);
    
    /**
     * Remove RfnGatewayData for the specified id from the cache.
     */
    public void remove(PaoIdentifier paoIdentifier);
    
    /** Return underlying loading cache. */
    public LoadingCache<PaoIdentifier, RfnGatewayData> getCache();
}