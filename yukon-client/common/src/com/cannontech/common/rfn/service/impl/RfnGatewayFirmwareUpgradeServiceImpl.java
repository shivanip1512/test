package com.cannontech.common.rfn.service.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.jms.ConnectionFactory;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.util.StringUtils;

import com.cannontech.amr.rfn.dao.RfnDeviceDao;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.rfn.dao.RfnGatewayFirmwareUpgradeDao;
import com.cannontech.common.rfn.dao.impl.GatewayDataException;
import com.cannontech.common.rfn.message.gateway.RfnGatewayFirmwareUpdateRequest;
import com.cannontech.common.rfn.model.FirmwareUpdateServerInfo;
import com.cannontech.common.rfn.model.NmCommunicationException;
import com.cannontech.common.rfn.model.RfnGateway;
import com.cannontech.common.rfn.model.RfnGatewayFirmwareUpdateResult;
import com.cannontech.common.rfn.model.RfnGatewayFirmwareUpdateSummary;
import com.cannontech.common.rfn.service.RfnGatewayFirmwareUpgradeService;
import com.cannontech.common.rfn.service.RfnGatewayService;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

public class RfnGatewayFirmwareUpgradeServiceImpl implements RfnGatewayFirmwareUpgradeService {
    
    private static final Logger log = YukonLogManager.getLogger(RfnGatewayServiceImpl.class);
    private static final String firmwareUpdateRequestQueue = "yukon.qr.obj.common.rfn.RfnGatewayFirmwareUpdateRequest";
    
    @Autowired private RfnGatewayFirmwareUpgradeDao firmwareUpgradeDao;
    @Autowired private RfnDeviceDao rfnDeviceDao;
    @Autowired private RfnGatewayService rfnGatewayService;
    
    private Cache<String, String> firmwareUpdateVersionCache;
    private JmsTemplate firmwareUpdateRequestTemplate;
    
    @Autowired
    public void setConnectionFactory(ConnectionFactory connectionFactory) {
        firmwareUpdateRequestTemplate = new JmsTemplate(connectionFactory);
        firmwareUpdateRequestTemplate.setExplicitQosEnabled(true);
        firmwareUpdateRequestTemplate.setDeliveryPersistent(false);
    }
    
    @PostConstruct
    public void init() {
        firmwareUpdateVersionCache = CacheBuilder.newBuilder()
                                                 .expireAfterWrite(5, TimeUnit.MINUTES)
                                                 .initialCapacity(2) //We expect only 1 or 2 firmware update servers
                                                 .build();
    }
    
    @Override
    public int sendFirmwareUpgrade(Collection<RfnGateway> gateways) throws GatewayDataException {
        
        log.info("Sending firmware upgrade for gateways: " + StringUtils.collectionToCommaDelimitedString(gateways));
        
        Map<Integer, FirmwareUpdateServerInfo> firmwareUpdateServerInfos = 
                firmwareUpgradeDao.getAllFirmwareUpdateServerInfo(gateways);
        
        int updateId = firmwareUpgradeDao.createUpgrade(gateways);
        
        for (RfnGateway gateway : gateways) {
            Integer gatewayId = rfnDeviceDao.getDeviceForExactIdentifier(gateway.getRfnIdentifier()).getPaoIdentifier().getPaoId();
            FirmwareUpdateServerInfo serverInfo = firmwareUpdateServerInfos.get(gatewayId);
            
            RfnGatewayFirmwareUpdateRequest request = new RfnGatewayFirmwareUpdateRequest();
            request.setGateway(gateway.getRfnIdentifier());
            request.setUpdateId(updateId);
            request.setReleaseVersion(serverInfo.getReleaseVersion());
            
            log.debug("Sending firmware update request: " + request);
            firmwareUpdateRequestTemplate.convertAndSend(firmwareUpdateRequestQueue, request);
        }
        
        return updateId;
    }
    
    @Override
    public List<RfnGatewayFirmwareUpdateSummary> getFirmwareUpdateSummaries() {
        return firmwareUpgradeDao.getAllUpdateSummaries();
    }
    
    @Override
    public List<RfnGatewayFirmwareUpdateResult> getFirmwareUpgradeResults(int updateId) {
        return firmwareUpgradeDao.getUpdateResults(updateId);
    }
    
    @Override
    public Map<String, String> getFirmwareUpdateServerVersions() throws NmCommunicationException {
        
        Map<String, String> updateServerVersions = new HashMap<>();
        Set<RfnGateway> uncachedValueGateways = new HashSet<>();
        
        // Collect cached values for as many gateways as possible
        // Make a set of gateways without cached values
        Set<RfnGateway> gateways = rfnGatewayService.getAllGatewaysWithData();
        for (RfnGateway gateway : gateways) {
            if (gateway.getData() != null) {
                String url = gateway.getData().getUpdateServerUrl();
                String version = firmwareUpdateVersionCache.getIfPresent(url);
                if (version != null) {
                    updateServerVersions.put(url, version);
                } else {
                    uncachedValueGateways.add(gateway);
                }
            }
        }
        
        // If any values aren't in the cache, retrieve them from Network Manager and update the cache
        if (uncachedValueGateways.size() > 0) {
            Map<String, String> uncachedVersions = firmwareUpgradeDao.getFirmwareUpdateServerVersions(uncachedValueGateways);
            firmwareUpdateVersionCache.putAll(uncachedVersions);
            updateServerVersions.putAll(uncachedVersions);
        }
        
        return updateServerVersions;
    }
}
