package com.cannontech.common.rfn.service.impl;

import static com.cannontech.common.rfn.service.RfnDeviceCreationService.*;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

import javax.annotation.PostConstruct;
import javax.jms.ConnectionFactory;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.cannontech.amr.rfn.dao.RfnDeviceDao;
import com.cannontech.amr.rfn.impl.NmSyncServiceImpl;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.message.gateway.GatewayDataRequest;
import com.cannontech.common.rfn.message.gateway.GatewayDataResponse;
import com.cannontech.common.rfn.model.NmCommunicationException;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.common.rfn.model.RfnGatewayData;
import com.cannontech.common.rfn.service.BlockingJmsReplyHandler;
import com.cannontech.common.rfn.service.RfnGatewayDataCache;
import com.cannontech.common.util.jms.RequestReplyTemplate;
import com.cannontech.common.util.jms.RequestReplyTemplateImpl;
import com.google.common.cache.AbstractLoadingCache;
import com.google.common.cache.LoadingCache;

public class RfnGatewayDataCacheImpl implements RfnGatewayDataCache {
    
    private static final Logger log = YukonLogManager.getLogger(RfnGatewayDataCacheImpl.class);
    private static final String gatewayDataRequestCparm = "RFN_GATEWAY_DATA_REQUEST";
    private static final String gatewayDataRequestQueue = "yukon.qr.obj.common.rfn.GatewayDataRequest";
    
    //Autowired in constructor
    private ConnectionFactory connectionFactory;
    private ConfigurationSource configurationSource;
    private LoadingCache<PaoIdentifier, RfnGatewayData> cache;
    private Executor executor;
    @Autowired private NmSyncServiceImpl nmSyncService;
    
    //Created in post-construct
    private RequestReplyTemplate<GatewayDataResponse> requestTemplate;
    
    @Autowired
    public RfnGatewayDataCacheImpl(ConnectionFactory connectionFactory, 
                                   ConfigurationSource configurationSource, 
                                   final RfnDeviceDao rfnDeviceDao, 
                                   @Qualifier("gatewayCache") Executor executor) {
        
        this.connectionFactory = connectionFactory;
        this.configurationSource = configurationSource;
        this.executor = executor;
        cache = new InternalGatewayDataCache(rfnDeviceDao);
    }
    
    @PostConstruct
    public void init() {
        requestTemplate = new RequestReplyTemplateImpl<>(gatewayDataRequestCparm, configurationSource,
                connectionFactory, gatewayDataRequestQueue, false, true);
    }
    
    @Override
    public RfnGatewayData get(PaoIdentifier paoIdentifier) throws NmCommunicationException {
        try {
            return cache.get(paoIdentifier);
        } catch (ExecutionException e) {
            throw new NmCommunicationException("Failed to retrieve gateway data from Network Manager.", e);
        }
    }
    
    @Override
    public RfnGatewayData getIfPresent(final PaoIdentifier paoIdentifier) {
        
        RfnGatewayData data = cache.getIfPresent(paoIdentifier);
        
        //spin off new thread to update the cache for this value
        if (data == null) {
            Thread thread = new Thread() {
                @Override
                public void run() {
                    try {
                        cache.get(paoIdentifier);
                    } catch (Exception e) {
                        log.error("Asynchronous rfn gateway cache update failed for: " + paoIdentifier, e);
                    }
                }
            };
            executor.execute(thread);
        }
        
        return data;
    }
    
    @Override
    public Collection<RfnGatewayData> getAll(Iterable<? extends PaoIdentifier> paoIdentifiers) throws NmCommunicationException {
        try {
            return cache.getAll(paoIdentifiers).values();
        } catch (ExecutionException e) {
            throw new NmCommunicationException("Failed to retrieve gateway data from Network Manager.", e);
        }
    }
    
    @Override
    public void put(PaoIdentifier paoIdentifier, RfnGatewayData data) {
       cache.put(paoIdentifier, data);
    }

    @Override
    public void remove(PaoIdentifier paoIdentifier) {
        cache.invalidate(paoIdentifier);
    }
    
    /**
     * This LoadingCache requests RfnGatewayData from Network Manager if it is not present for the requested id. The
     * call blocks until the data is returned or an individual request times out.
     */
    private class InternalGatewayDataCache extends AbstractLoadingCache<PaoIdentifier, RfnGatewayData> {
        
        private ConcurrentHashMap<PaoIdentifier, RfnGatewayData> cacheMap = new ConcurrentHashMap<>();
        private RfnDeviceDao rfnDeviceDao;
        
        public InternalGatewayDataCache(RfnDeviceDao rfnDeviceDao) {
            this.rfnDeviceDao = rfnDeviceDao;
        }
        
        @Override
        public RfnGatewayData get(PaoIdentifier key) throws ExecutionException {
            //return cached data, if present
            RfnGatewayData data = cacheMap.get(key);
            if (data != null) {
                return data;
            }
            
            //Prepare the request
            RfnDevice device = rfnDeviceDao.getDeviceForId(key.getPaoId());
            RfnIdentifier rfnIdentifier = device.getRfnIdentifier();
            GatewayDataRequest request = new GatewayDataRequest();
            request.setRfnIdentifier(rfnIdentifier);
            
            //Send the request and wait for the response
            BlockingJmsReplyHandler<GatewayDataResponse> replyHandler = new BlockingJmsReplyHandler<>(GatewayDataResponse.class);
            requestTemplate.send(request, replyHandler);
            GatewayDataResponse response = replyHandler.waitForCompletion();
            nmSyncService.syncGatewayName(device, response.getName());
            //If the response rfnId has a newer model, update the Yukon gateway device
            RfnIdentifier responseRfnIdentifier = response.getRfnIdentifier();
            if (rfnIdentifier.getSensorModel().equalsIgnoreCase(GATEWAY_1_MODEL_STRING) &&
                    responseRfnIdentifier.getSensorModel().equalsIgnoreCase(GATEWAY_2_MODEL_STRING)) {
                
                rfnDeviceDao.updateGatewayType(device);
            }
            
            //Update the cache and return the data
            data = new RfnGatewayData(response, device.getName());
            cacheMap.put(key, data);
            return data;
        }
        
        @Override
        public RfnGatewayData getIfPresent(Object key) {
            return cacheMap.get(key);
        }
        
        @Override
        public void put(PaoIdentifier paoIdentifier, RfnGatewayData data) {
            cacheMap.put(paoIdentifier, data);
        }
        
        @Override
        public void invalidate(Object key) {
            cacheMap.remove(key);
        }
        
        @Override
        public ConcurrentMap<PaoIdentifier, RfnGatewayData> asMap() {
            return cacheMap;
        }
    }
    
    @Override
    public LoadingCache<PaoIdentifier, RfnGatewayData> getCache() {
        return cache;
    }
    
}
