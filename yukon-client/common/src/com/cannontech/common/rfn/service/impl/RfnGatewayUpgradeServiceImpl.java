package com.cannontech.common.rfn.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.jms.ConnectionFactory;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.message.gateway.RfnGatewayUpgradeRequest;
import com.cannontech.common.rfn.message.gateway.RfnGatewayUpgradeResponse;
import com.cannontech.common.rfn.model.NetworkManagerCommunicationException;
import com.cannontech.common.rfn.model.RfnGateway;
import com.cannontech.common.rfn.service.RfnGatewayService;
import com.cannontech.common.rfn.service.RfnGatewayUpgradeCallback;
import com.cannontech.common.rfn.service.RfnGatewayUpgradeService;
import com.cannontech.common.util.jms.JmsReplyHandler;
import com.cannontech.common.util.jms.RequestReplyTemplate;
import com.cannontech.common.util.jms.RequestReplyTemplateImpl;
import com.google.common.collect.Sets;

public class RfnGatewayUpgradeServiceImpl implements RfnGatewayUpgradeService {
    
    private static final Logger log = YukonLogManager.getLogger(RfnGatewayUpgradeServiceImpl.class);
    
    private final static String configName = "RFN_GATEWAY_UPGRADE_REQUEST";
    private final static String queueName = "yukon.qr.obj.gw.rfn.upgradeRequest";
    
    @Autowired private ConnectionFactory connectionFactory;
    @Autowired private ConfigurationSource configurationSource;
    @Autowired private RfnGatewayService gatewayService;
    
    private RequestReplyTemplate<RfnGatewayUpgradeResponse> qrTemplate;
    
    @PostConstruct
    public void initialize() {
        qrTemplate =
            new RequestReplyTemplateImpl<RfnGatewayUpgradeResponse>(configName,
                                                                 configurationSource,
                                                                 connectionFactory,
                                                                 queueName,
                                                                 false);
    }
    
    @Override
    public void sendUpgrade(Set<? extends YukonPao> gwPaos, String upgradeID, File upgradePackage,
                            final RfnGatewayUpgradeCallback callback) {
        // PAOs to RfnIdentifiers. NM will treat a null or empty set of RfnIdentifiers to mean "all"
        Set<RfnIdentifier> rfnIdentifiers = null;
        if (gwPaos != null) {
            rfnIdentifiers = Sets.newHashSet();
            try {
                for (YukonPao yukonPao : gwPaos) {
                    RfnGateway rfnGateway =
                        gatewayService.getGatewayByPaoId(yukonPao.getPaoIdentifier());
                    rfnIdentifiers.add(rfnGateway.getRfnIdentifier());
                }
            } catch (NetworkManagerCommunicationException e) {
                callback.handleException(e);
                return;
            }
        }

        // Read upgrade package into byte[].
        byte[] upgradeData = null;
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(upgradePackage);
            upgradeData = IOUtils.toByteArray(fis);
        } catch (IOException e) {
            callback.handleException(e);
            return;
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    log.warn("Failed to close upgrade package file.", e);
                }
            }
        }

        RfnGatewayUpgradeRequest request = new RfnGatewayUpgradeRequest();
        request.setRfnIdentifiers(rfnIdentifiers);
        request.setUpgradeID(upgradeID);
        request.setUpgradeData(upgradeData);

        // Expect acknowledgment reply only.
        // Gateway's upgrade reply will be received by RfnGatewayUpgradeListener.
        JmsReplyHandler<RfnGatewayUpgradeResponse> handler =
            new JmsReplyHandler<RfnGatewayUpgradeResponse>() {
                // Delegate all calls to callback handler.
                @Override
                public void handleException(Exception e) {
                    callback.handleException(e);
                }

                @Override
                public void complete() {
                }

                @Override
                public void handleTimeout() {
                    callback.handleTimeout();
                }

                @Override
                public void handleReply(RfnGatewayUpgradeResponse reply) {
                    callback.handleReply(reply);
                }

                @Override
                public Class<RfnGatewayUpgradeResponse> getExpectedType() {
                    return RfnGatewayUpgradeResponse.class;
                }
            };
        qrTemplate.send(request, handler);
    }

    @Override
    public void sendUpgradeAll(String upgradeID, File upgradePackage,
                               RfnGatewayUpgradeCallback callback) {
        sendUpgrade(null, upgradeID, upgradePackage, callback);
    }
}
