package com.cannontech.common.rfn.service.impl;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

import javax.annotation.PostConstruct;
import javax.jms.ConnectionFactory;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.rfn.dao.RfnDeviceDao;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.message.gateway.GatewayDataRequest;
import com.cannontech.common.rfn.message.gateway.GatewayDataResponse;
import com.cannontech.common.rfn.model.NetworkManagerCommunicationException;
import com.cannontech.common.rfn.model.RfnGatewayData;
import com.cannontech.common.rfn.service.RfnGatewayDataCache;
import com.cannontech.common.rfn.service.RfnGatewayDataReplyHandler;
import com.cannontech.common.util.jms.RequestReplyTemplate;
import com.cannontech.common.util.jms.RequestReplyTemplateImpl;
import com.google.common.cache.AbstractLoadingCache;
import com.google.common.cache.LoadingCache;

public class RfnGatewayDataCacheImpl implements RfnGatewayDataCache {
    //Autowired in constructor
    private ConnectionFactory connectionFactory;
    private ConfigurationSource configurationSource;
    private LoadingCache<PaoIdentifier, RfnGatewayData> cache;
    
    //Created in post-construct
    private RequestReplyTemplate<GatewayDataResponse> requestTemplate;
    
    @Autowired
    public RfnGatewayDataCacheImpl(ConnectionFactory connectionFactory, ConfigurationSource configurationSource, 
                                final RfnDeviceDao rfnDeviceDao) {
        
        this.connectionFactory = connectionFactory;
        this.configurationSource = configurationSource;
        cache = new InternalGatewayDataCache(rfnDeviceDao);
    }
    
    @PostConstruct
    public void init() {
        requestTemplate = new RequestReplyTemplateImpl<GatewayDataResponse>("RFN_GATEWAY_DATA_REQUEST", configurationSource,
                connectionFactory, "yukon.qr.obj.common.rfn.GatewayDataRequest", false);
    }
    
    @Override
    public RfnGatewayData get(PaoIdentifier paoIdentifier) throws NetworkManagerCommunicationException {
        try {
            return cache.get(paoIdentifier);
        } catch (ExecutionException e) {
            throw new NetworkManagerCommunicationException("Failed to retrieve gateway data from Network Manager.", e);
        }
    }
    
    @Override
    public Collection<RfnGatewayData> getAll(Iterable<? extends PaoIdentifier> paoIdentifiers) throws NetworkManagerCommunicationException {
        try {
            return cache.getAll(paoIdentifiers).values();
        } catch (ExecutionException e) {
            throw new NetworkManagerCommunicationException("Failed to retrieve gateway data from Network Manager.", e);
        }
    }
    
    @Override
    public void put(PaoIdentifier paoIdentifier, RfnGatewayData data) {
       cache.put(paoIdentifier, data);
    }
    
    /**
     * This LoadingCache requests RfnGatewayData from Network Manager if it is not present for the requested id. The
     * call blocks until the data is returned or an individual request times out.
     */
    private class InternalGatewayDataCache extends AbstractLoadingCache<PaoIdentifier, RfnGatewayData> {
        private Map<PaoIdentifier, RfnGatewayData> cacheMap = new ConcurrentHashMap<>();
        private RfnDeviceDao rfnDeviceDao;
        
        public InternalGatewayDataCache(RfnDeviceDao rfnDeviceDao) {
            this.rfnDeviceDao = rfnDeviceDao;
        }
        
        @Override
        public RfnGatewayData get(PaoIdentifier key) throws ExecutionException {
            if (cacheMap.containsKey(key)) {
                //return cached data
                return cacheMap.get(key);
            } else {
                //Prepare the request
                RfnIdentifier rfnIdentifier = rfnDeviceDao.getDeviceForId(key.getPaoId()).getRfnIdentifier();
                GatewayDataRequest request = new GatewayDataRequest();
                request.setRfnIdentifier(rfnIdentifier);
                
                //Send the request and wait for the response
                RfnGatewayDataReplyHandler replyHandler = new RfnGatewayDataReplyHandler();
                requestTemplate.send(request, replyHandler);
                GatewayDataResponse response = replyHandler.waitForCompletion();
                RfnGatewayData data = new RfnGatewayData(response);
                
                //Update the cache and return the data
                cacheMap.put(key, data);
                return data;
            }
        }
        
        @Override
        public RfnGatewayData getIfPresent(Object key) {
            return cacheMap.get(key);
        }
        
        @Override
        public void put(PaoIdentifier paoIdentifier, RfnGatewayData data) {
            cacheMap.put(paoIdentifier, data);
        }
    }
}
