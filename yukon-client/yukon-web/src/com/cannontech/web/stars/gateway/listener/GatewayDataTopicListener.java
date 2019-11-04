package com.cannontech.web.stars.gateway.listener;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.apache.logging.log4j.Logger;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.rfn.impl.NmSyncServiceImpl;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigBoolean;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.rfn.dao.GatewayCertificateUpdateDao;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.message.gateway.ConnectionStatus;
import com.cannontech.common.rfn.message.gateway.GatewayDataResponse;
import com.cannontech.common.rfn.message.gateway.RfnGatewayUpgradeResponse;
import com.cannontech.common.rfn.message.gateway.RfnGatewayUpgradeResponseType;
import com.cannontech.common.rfn.model.GatewayCertificateUpdateStatus;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.common.rfn.model.RfnGatewayData;
import com.cannontech.common.rfn.service.RfnDeviceLookupService;
import com.cannontech.common.rfn.service.RfnGatewayDataCache;
import com.cannontech.common.rfn.service.RfnGatewayService;
import com.cannontech.common.util.TimeUtil;
import com.cannontech.core.dao.NotFoundException;

/**
 * This listener receives gateway data messages and gateway certificate update responses from service manager.
 * Gateway data messages are cached in RfnGatewayDataCache.
 * Gateway upgrade responses are used to update the database on the state of the gateway certificate update.
 */
public class GatewayDataTopicListener implements MessageListener {
    
    private static final Logger log = YukonLogManager.getLogger(GatewayDataTopicListener.class);
    private static final int minReadyNodeArchiveFrequencyMinutes = 60;
    
    @Autowired private RfnGatewayDataCache cache;
    @Autowired private RfnDeviceLookupService rfnDeviceLookupService;
    @Autowired private GatewayCertificateUpdateDao certificateUpdateDao;
    @Autowired private ConfigurationSource configSource;
    @Autowired private RfnGatewayService rfnGatewayService;
    @Autowired private NmSyncServiceImpl nmSyncService;
    
    private boolean isDataStreamingEnabled;
    private Map<PaoIdentifier, Instant> deviceReadyNodeLastArchiveTimes = new HashMap<>();
    
    
    @PostConstruct
    public void init() {
        isDataStreamingEnabled = configSource.getBoolean(MasterConfigBoolean.RF_DATA_STREAMING_ENABLED, false);
    }
    
    @Override
    public void onMessage(Message message) {
        if (message instanceof ObjectMessage) {
            ObjectMessage objMessage = (ObjectMessage) message;
            try {
                Serializable object = objMessage.getObject();
                if (object instanceof GatewayDataResponse) {
                    GatewayDataResponse gatewayDataMessage = (GatewayDataResponse) object;
                    handleDataMessage(gatewayDataMessage);
                } else if (object instanceof RfnGatewayUpgradeResponse) {
                    RfnGatewayUpgradeResponse gatewayUpgradeMessage = (RfnGatewayUpgradeResponse) object;
                    handleGatewayUpgradeMessage(gatewayUpgradeMessage);
                }
            } catch (JMSException e) {
                log.warn("Unable to extract GatewayDataResponse or RfnGatewayUpgradeResponse from message", e);
            }
        }
    }
    
    private void handleDataMessage(GatewayDataResponse message) {
        RfnIdentifier rfnIdentifier = message.getRfnIdentifier();
        try {
            RfnDevice rfnDevice = rfnDeviceLookupService.getDevice(rfnIdentifier);
            log.debug("Handling gateway data message: " + message);
            RfnGatewayData data = new RfnGatewayData(message, rfnDevice.getName());
            
            nmSyncService.syncGatewayName(rfnDevice, message.getName());
            
            cache.put(rfnDevice.getPaoIdentifier(), data);
            
            // Archive data streaming point values only if data streaming is enabled and gateway supports it
            if (isDataStreamingEnabled) {
                if (rfnDevice.getPaoIdentifier().getPaoType() == PaoType.GWY800
                        || rfnDevice.getPaoIdentifier().getPaoType() == PaoType.VIRTUAL_GATEWAY) {
                    rfnGatewayService.generatePointData(rfnDevice, BuiltInAttribute.DATA_STREAMING_LOAD,
                            data.getDataStreamingLoadingPercent(), false);
                }
            }
            
            // Archive ready nodes values, at most, once per hour
            archiveReadyNodesValue(rfnDevice, message.getGwTotalReadyNodes());
            
            // Always archive comm status
            int commStatus = message.getConnectionStatus() == ConnectionStatus.CONNECTED ? 0 : 1;
            rfnGatewayService.generatePointData(rfnDevice, BuiltInAttribute.COMM_STATUS, commStatus, true);
        } catch (NotFoundException e) {
            log.error("Unable to add gateway data to cache. Device lookup failed for " + rfnIdentifier);
        }
    }
    
    private void archiveReadyNodesValue(RfnDevice rfnDevice, int gwTotalReadyNodes) {
        Instant previousArchiveTime = deviceReadyNodeLastArchiveTimes.get(rfnDevice.getPaoIdentifier());
        
        if (previousArchiveTime == null || TimeUtil.isXMinutesBeforeNow(minReadyNodeArchiveFrequencyMinutes, previousArchiveTime)) {
            // Update time exceeded, so flag as "must archive"
            deviceReadyNodeLastArchiveTimes.put(rfnDevice.getPaoIdentifier(), Instant.now());
            rfnGatewayService.generatePointData(rfnDevice, BuiltInAttribute.READY_NODES, gwTotalReadyNodes, true);
        } else {
            // Send to dispatch, but don't flag as "must archive"
            rfnGatewayService.generatePointData(rfnDevice, BuiltInAttribute.READY_NODES, gwTotalReadyNodes, false);
        }
    }
    
    private void handleGatewayUpgradeMessage(RfnGatewayUpgradeResponse gatewayUpgradeMessage) {
        
        String certificateId = gatewayUpgradeMessage.getUpgradeId();
        int updateId = certificateUpdateDao.getLatestUpdateForCertificate(certificateId);
        
        RfnIdentifier rfnId = gatewayUpgradeMessage.getRfnIdentifier();
        int paoId = rfnDeviceLookupService.getDevice(rfnId).getPaoIdentifier().getPaoId();
        
        RfnGatewayUpgradeResponseType responseType = gatewayUpgradeMessage.getResponseType();
        GatewayCertificateUpdateStatus status = GatewayCertificateUpdateStatus.of(responseType);
        
        log.info("Gateway certificate upgrade response received. PaoId: " + paoId + ", ResponseType: " + responseType);
        certificateUpdateDao.updateEntry(updateId, paoId, status);
    }
}
