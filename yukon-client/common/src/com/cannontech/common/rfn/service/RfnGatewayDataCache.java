package com.cannontech.common.rfn.service;

import java.util.Collection;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.rfn.model.NetworkManagerCommunicationException;
import com.cannontech.common.rfn.model.RfnGatewayData;

/**
 * A cache of RfnGatewayData - essentially all the gateway data that Network Manager stores and Yukon doesn't.
 * 
 * Backed internally by a loading cache. If data is requested for a gateway that isn't cached, it will be automatically
 * requested from Network Manager. The method call will block until the data is returned or the requests time out.
 */
public interface RfnGatewayDataCache {
    
    /**
     * Gets RfnGatewayData for a single id.
     * @throws NetworkManagerCommunicationException if an error occurs retrieving data from Network Manager.
     */
    public RfnGatewayData get(PaoIdentifier paoIdentifier) throws NetworkManagerCommunicationException;
    
    /**
     * Gets all RfnGatewayData for the specified ids.
     * @throws NetworkManagerCommunicationException if an error occurs retrieving data from Network Manager.
     */
    public Collection<RfnGatewayData> getAll(Iterable<? extends PaoIdentifier> paoIdentifiers) 
            throws NetworkManagerCommunicationException;
    
    /**
     * Put an RfnGatewayData value into the cache.
     */
    public void put(PaoIdentifier paoIdentifier, RfnGatewayData data);
    
}
