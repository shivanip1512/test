package com.cannontech.common.rfn.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.jms.ConnectionFactory;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.rfn.dao.GatewayCertificateUpdateDao;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.message.gateway.RfnGatewayUpgradeRequest;
import com.cannontech.common.rfn.message.gateway.RfnGatewayUpgradeRequestAck;
import com.cannontech.common.rfn.model.GatewayCertificateException;
import com.cannontech.common.rfn.model.GatewayCertificateUpdateStatus;
import com.cannontech.common.rfn.model.RfnGateway;
import com.cannontech.common.rfn.service.RfnDeviceLookupService;
import com.cannontech.common.rfn.service.RfnGatewayService;
import com.cannontech.common.rfn.service.RfnGatewayUpdateCallback;
import com.cannontech.common.rfn.service.RfnGatewayUpdateService;
import com.cannontech.common.util.jms.JmsReplyHandler;
import com.cannontech.common.util.jms.RequestReplyTemplate;
import com.cannontech.common.util.jms.RequestReplyTemplateImpl;

public class RfnGatewayUpdateServiceImpl implements RfnGatewayUpdateService {

    private static final Logger log = YukonLogManager.getLogger(RfnGatewayUpdateServiceImpl.class);

    private final static String configName = "RFN_GATEWAY_UPGRADE_REQUEST";
    private final static String queueName = "yukon.qr.obj.rfn.GatewayUpgradeRequest";

    @Autowired private ConnectionFactory connectionFactory;
    @Autowired private ConfigurationSource configurationSource;
    @Autowired private RfnGatewayService gatewayService;
    @Autowired private GatewayCertificateUpdateDao certificateUpdateDao;
    @Autowired private RfnDeviceLookupService rfnDeviceLookupService;

    private RequestReplyTemplate<RfnGatewayUpgradeRequestAck> qrTemplate;

    @PostConstruct
    public void initialize() {
        qrTemplate =
            new RequestReplyTemplateImpl<RfnGatewayUpgradeRequestAck>(configName,
                                                                    configurationSource,
                                                                    connectionFactory,
                                                                    queueName,
                                                                    false);
    }

    @Override
    public void sendUpdate(Set<RfnGateway> rfnGateways, File upgradePackage, final RfnGatewayUpdateCallback callback) {
        // Read upgrade package into byte[].
        byte[] upgradeData = null;
        try (FileInputStream fis = new FileInputStream(upgradePackage)) {
            upgradeData = IOUtils.toByteArray(fis);
        } catch (IOException e) {
            callback.handleException(e);
            callback.complete();
            return;
        }
        
        String upgradeId;
        try {
            upgradeId = getCertificateId(upgradePackage);
        } catch (Exception e) {
            callback.handleException(e);
            callback.complete();
            return;
        }
        
        final int updateDbId = certificateUpdateDao.createUpdate(upgradeId, upgradePackage.getName());
        
        Set<RfnIdentifier> rfnIdentifiers = new HashSet<>();
        Set<Integer> paoIds = new HashSet<>();
        if (rfnGateways != null) {
            for (RfnGateway rfnGateway : rfnGateways) {
                paoIds.add(rfnGateway.getPaoIdentifier().getPaoId());
                rfnIdentifiers.add(rfnGateway.getRfnIdentifier());
            }
        }
        
        certificateUpdateDao.createEntries(updateDbId, GatewayCertificateUpdateStatus.STARTED, paoIds);
        
        RfnGatewayUpgradeRequest request = new RfnGatewayUpgradeRequest();
        request.setRfnIdentifiers(rfnIdentifiers);
        request.setUpgradeId(upgradeId);
        request.setUpgradeData(upgradeData);

        // Expect acknowledgment reply only.
        // Gateway's upgrade reply will be received by RfnGatewayUpgradeListener.
        JmsReplyHandler<RfnGatewayUpgradeRequestAck> handler = new JmsReplyHandler<RfnGatewayUpgradeRequestAck>() {
                // Delegate all calls to callback handler.
                @Override
                public void handleException(Exception e) {
                    callback.handleException(e);
                }

                @Override
                public void complete() {
                    callback.complete();
                }

                @Override
                public void handleTimeout() {
                    callback.handleTimeout();
                }

                @Override
                public void handleReply(RfnGatewayUpgradeRequestAck reply) {
                    logGatewayUpgradeStatusFromAck(updateDbId, reply);
                    callback.handleReply(reply);
                }

                @Override
                public Class<RfnGatewayUpgradeRequestAck> getExpectedType() {
                    return RfnGatewayUpgradeRequestAck.class;
                }
            };
        qrTemplate.send(request, handler);
    }
    
    @Override
    public void sendUpdateAll(File upgradePackage, RfnGatewayUpdateCallback callback) {
        sendUpdate(null, upgradePackage, callback);
    }

    /** 
     * *.pkg.nm itself is composed of fragments/parts for transport purposes:
     *
     * <upgrade img length> :== <int32>
     * <upgrade img>_______ :== <byte>*<upgrade img_length>
     * ____________________ :== [<fragment length><fragment>]*
     * <fragment length>___ :== <int32>
     * <fragment>__________ :== <byte>*<fragment length>
     *
     * At the beginning of the first fragment will be the following data structure:
     *
     * <total length> :== <int32>
     * <info length>_ :== <int16>
     * <info data>___ :== <byte>*<info length>
     * ______________ :== [<type><length><value>]*
     * <type>________ :== <int16>
     * <length>______ :== <int16>
     * <value>_______ :== <byte>*<length>
     *
     * I.E. After the total length there is an info length, the following info length number of
     * bytes are broken into upgrade info TLV fields. The type and length of each of these fields is
     * a short int.
     *
     * The upgradeId itself is stored in upgrade info type 0. You should be able to build the
     * upgradeId string from that array of bytes.
     */
    @Override
    public String getCertificateId(File upgradePackage) throws GatewayCertificateException {
        byte[] upgradeData = null;
        try (FileInputStream fis = new FileInputStream(upgradePackage)) {
            upgradeData = IOUtils.toByteArray(fis);
            ByteBuffer buf = ByteBuffer.wrap(upgradeData);
            int upgradeImgLen = buf.getInt();
            if (upgradeImgLen > buf.remaining()) {
                throw new GatewayCertificateException("Upgrade package " + upgradePackage.getName()
                                                      + " is invalid: Upgrade image length " + upgradeImgLen
                                                      + " exceeds package size");
            }
            // We are only interested in the first fragment.
            int fragmentLen = buf.getInt();
            if (fragmentLen > upgradeImgLen) {
                throw new GatewayCertificateException("Upgrade package " + upgradePackage.getName()
                                                      + " is invalid: Fragment length " + fragmentLen
                                                      + " exceeds upgrade image length " + upgradeImgLen);
            }
            int totalLen = buf.getInt();
            if (totalLen + 4 > fragmentLen) {
                throw new GatewayCertificateException("Upgrade package " + upgradePackage.getName()
                                    + " is invalid: Fragment's total length " + fragmentLen
                                    + " exceeds fragment length " + upgradeImgLen);
            }
            short infoLen = buf.getShort();
            if (infoLen > fragmentLen) {
                throw new GatewayCertificateException("Upgrade package " + upgradePackage.getName()
                                    + " is invalid: Fragment's total length " + fragmentLen
                                    + " exceeds fragment length " + upgradeImgLen);
            }
            // Check each TLV (type,length,value) in info data until we find the upgradeId.
            while (infoLen > 4) {
                short tlvType = buf.getShort();
                infoLen -= 2;
                short tlvLen = buf.getShort();
                infoLen -= 2;
                if (tlvLen > infoLen) {
                    throw new GatewayCertificateException("Upgrade package " + upgradePackage.getName()
                                                          + " is invalid: Info TLV length " + tlvLen
                                                          + " exceeds remaining info length " + infoLen);
                }
                byte[] cbuf = new byte[tlvLen];
                buf.get(cbuf);
                if (tlvType == 0) {
                    return new String(cbuf);
                }
            }
            throw new GatewayCertificateException("Upgrade package " + upgradePackage.getName()
                                                  + " is invalid: upgradeId not found.");
        } catch (IOException e) {
            for (Throwable t : e.getSuppressed()) {
                log.warn("Failed to close upgrade package file.", t);
            }
            throw new GatewayCertificateException("Unable to access upgrade package file "
                                                  + upgradePackage.getName(), e);
        }
    }
    
    //TODO: clean up inefficient calls
    private void logGatewayUpgradeStatusFromAck(int updateId, RfnGatewayUpgradeRequestAck message) {
        for (RfnIdentifier rfnId : message.getBeingUpgradedRfnIdentifiers()) {
            int paoId = rfnDeviceLookupService.getDevice(rfnId).getPaoIdentifier().getPaoId();
            certificateUpdateDao.updateEntry(updateId, paoId, GatewayCertificateUpdateStatus.REQUEST_ACCEPTED);
        }
        
        for (RfnIdentifier rfnId : message.getInvalidRfnIdentifiers().keySet()) {
            int paoId = rfnDeviceLookupService.getDevice(rfnId).getPaoIdentifier().getPaoId();
            certificateUpdateDao.updateEntry(updateId, paoId, GatewayCertificateUpdateStatus.INVALID_RFN_ID);
        }
        
        for (RfnIdentifier rfnId : message.getInvalidSuperAdminPasswordRfnIdentifiers().keySet()) {
            int paoId = rfnDeviceLookupService.getDevice(rfnId).getPaoIdentifier().getPaoId();
            certificateUpdateDao.updateEntry(updateId, paoId, GatewayCertificateUpdateStatus.INVALID_SUPER_ADMIN_PASSWORD);
        }
        
        for (RfnIdentifier rfnId : message.getLastUpgradeInProcessRfnIdentifiers().keySet()) {
            int paoId = rfnDeviceLookupService.getDevice(rfnId).getPaoIdentifier().getPaoId();
            certificateUpdateDao.updateEntry(updateId, paoId, GatewayCertificateUpdateStatus.ALREADY_IN_PROGRESS);
        }
    }
}
