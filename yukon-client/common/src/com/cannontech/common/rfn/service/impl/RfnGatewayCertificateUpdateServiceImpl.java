package com.cannontech.common.rfn.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.jms.ConnectionFactory;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.rfn.dao.GatewayCertificateUpdateDao;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.message.gateway.RfnGatewayUpgradeRequest;
import com.cannontech.common.rfn.message.gateway.RfnGatewayUpgradeRequestAck;
import com.cannontech.common.rfn.model.CertificateUpdate;
import com.cannontech.common.rfn.model.GatewayCertificateException;
import com.cannontech.common.rfn.model.GatewayCertificateUpdateInfo;
import com.cannontech.common.rfn.model.GatewayCertificateUpdateStatus;
import com.cannontech.common.rfn.model.RfnGateway;
import com.cannontech.common.rfn.service.RfnDeviceLookupService;
import com.cannontech.common.rfn.service.RfnGatewayCertificateUpdateService;
import com.cannontech.common.rfn.service.RfnGatewayService;
import com.cannontech.common.util.jms.JmsReplyHandler;
import com.cannontech.common.util.jms.RequestReplyTemplate;
import com.cannontech.common.util.jms.RequestReplyTemplateImpl;

public class RfnGatewayCertificateUpdateServiceImpl implements RfnGatewayCertificateUpdateService {

    private static final Logger log = YukonLogManager.getLogger(RfnGatewayCertificateUpdateServiceImpl.class);

    private final static String configName = "RFN_GATEWAY_UPGRADE_REQUEST";
    private final static String queueName = "yukon.qr.obj.common.rfn.GatewayUpgradeRequest";

    @Autowired private ConnectionFactory connectionFactory;
    @Autowired private ConfigurationSource configurationSource;
    @Autowired private RfnGatewayService gatewayService;
    @Autowired private GatewayCertificateUpdateDao certificateUpdateDao;
    @Autowired private RfnDeviceLookupService rfnDeviceLookupService;

    private RequestReplyTemplate<RfnGatewayUpgradeRequestAck> qrTemplate;

    @PostConstruct
    public void initialize() {
        qrTemplate = new RequestReplyTemplateImpl<RfnGatewayUpgradeRequestAck>(configName, configurationSource,
                                                                               connectionFactory, queueName, false);
    }

    @Override
    public String sendUpdate(Set<RfnGateway> rfnGateways, MultipartFile upgradePackage) 
            throws IOException, GatewayCertificateException {
        
        // Read upgrade package into byte[].
        byte[] upgradeData = null;
        try (InputStream fis = upgradePackage.getInputStream()) {
            upgradeData = IOUtils.toByteArray(fis);
        }
        
        //Throws GatewayCertificateException if parsing fails
        String upgradeId = getCertificateId(upgradePackage);
        
        // Use the id from the certificate file to record this certificate update in the database
        final int updateDbId = certificateUpdateDao.createUpdate(upgradeId, upgradePackage.getOriginalFilename());
        
        Set<RfnIdentifier> rfnIdentifiers = new HashSet<>();
        Set<Integer> paoIds = new HashSet<>();
        if (rfnGateways != null) {
            for (RfnGateway rfnGateway : rfnGateways) {
                paoIds.add(rfnGateway.getPaoIdentifier().getPaoId());
                rfnIdentifiers.add(rfnGateway.getRfnIdentifier());
            }
        }
        
        // Add database entries for each gateway being sent the certificate update
        certificateUpdateDao.createEntries(updateDbId, GatewayCertificateUpdateStatus.STARTED, paoIds);
        
        RfnGatewayUpgradeRequest request = new RfnGatewayUpgradeRequest();
        request.setRfnIdentifiers(rfnIdentifiers);
        request.setUpgradeId(upgradeId);
        request.setUpgradeData(upgradeData);

        // Expect acknowledgment reply only.
        // Gateway's upgrade reply will be received by GatewayDataResponseListener.
        JmsReplyHandler<RfnGatewayUpgradeRequestAck> handler = new JmsReplyHandler<RfnGatewayUpgradeRequestAck>() {
                @Override
                public void handleException(Exception e) {
                    log.error("Error occurred in gateway certificate upgrade.", e);
                }

                @Override
                public void complete() {
                    //Ack is complete. Any ids not updated are assumed failed.
                    log.debug("Callback complete for gateway certificate upgrade.");
                    certificateUpdateDao.failUnackedForUpdate(updateDbId);
                }

                @Override
                public void handleTimeout() {
                    log.error("Timeout waiting for acknowledgement of gateway certificate upgrade.");
                    certificateUpdateDao.timeoutUpdate(updateDbId);
                }

                @Override
                public void handleReply(RfnGatewayUpgradeRequestAck reply) {
                    log.info("Received acknowledgement of gateway certificate upgrade from Network Manager. " + 
                            "Status: " + reply.getRequestAckType() + ". Message: " + reply.getRequestAckMessage());
                    logGatewayUpgradeStatusFromAck(updateDbId, reply);
                }

                @Override
                public Class<RfnGatewayUpgradeRequestAck> getExpectedType() {
                    return RfnGatewayUpgradeRequestAck.class;
                }
            };
        qrTemplate.send(request, handler);
        
        //This is the certificate's internal identifier
        return upgradeId;
    }
    
    @Override
    public String sendUpdateAll(MultipartFile upgradePackage) throws IOException, GatewayCertificateException {
        return sendUpdate(null, upgradePackage);
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
    public String getCertificateId(MultipartFile upgradePackage) throws GatewayCertificateException {
        byte[] upgradeData = null;
        try (InputStream fis = upgradePackage.getInputStream()) {
            upgradeData = IOUtils.toByteArray(fis);
            ByteBuffer buf = ByteBuffer.wrap(upgradeData);
            
            int upgradeImgLen = buf.getInt();
            if (upgradeImgLen > buf.remaining()) {
                String message = "Upgrade package " + upgradePackage.getName() + " is invalid: Upgrade image length " 
                        + upgradeImgLen + " exceeds package size";
                log.error(message);
                throw new GatewayCertificateException(message);
            }
            
            // We are only interested in the first fragment.
            int fragmentLen = buf.getInt();
            if (fragmentLen > upgradeImgLen) {
                String message = "Upgrade package " + upgradePackage.getName() + " is invalid: Fragment length " 
                        + fragmentLen + " exceeds upgrade image length " + upgradeImgLen;
                log.error(message);
                throw new GatewayCertificateException(message);
            }
            
            int totalLen = buf.getInt();
            if (totalLen + 4 > fragmentLen) {
                String message = "Upgrade package " + upgradePackage.getName() + " is invalid: Fragment's total length " 
                        + fragmentLen + " exceeds fragment length " + upgradeImgLen;
                log.error(message);
                throw new GatewayCertificateException(message);
            }
            
            short infoLen = buf.getShort();
            if (infoLen > fragmentLen) {
                String message = "Upgrade package " + upgradePackage.getName() + " is invalid: Fragment's total length " 
                        + fragmentLen + " exceeds fragment length " + upgradeImgLen;
                log.error(message);
                throw new GatewayCertificateException(message);
            }
            
            // Check each TLV (type,length,value) in info data until we find the upgradeId.
            while (infoLen > 4) {
                short tlvType = buf.getShort();
                infoLen -= 2;
                short tlvLen = buf.getShort();
                infoLen -= 2;
                
                if (tlvLen > infoLen) {
                    String message = "Upgrade package " + upgradePackage.getName() + " is invalid: Info TLV length " 
                            + tlvLen + " exceeds remaining info length " + infoLen;
                    log.error(message);
                    throw new GatewayCertificateException(message);
                }
                
                byte[] cbuf = new byte[tlvLen];
                buf.get(cbuf);
                if (tlvType == 0) {
                    return new String(cbuf);
                }
            }
            
            String message = "Upgrade package " + upgradePackage.getName() + " is invalid: upgradeId not found.";
            log.error(message);
            throw new GatewayCertificateException(message);
            
        } catch (IOException e) {
            for (Throwable t : e.getSuppressed()) {
                log.warn("Failed to close upgrade package file.", t);
            }
            String message = "Unable to access upgrade package file " + upgradePackage.getName();
            log.error(message);
            throw new GatewayCertificateException(message, e);
        }
    }
    
    @Override
    public CertificateUpdate getCertificateUpdate(int updateId) {
        
        GatewayCertificateUpdateInfo info = certificateUpdateDao.getUpdateInfo(updateId);
        Map<Integer, RfnGateway> gateways = gatewayService.getAllGatewaysByPaoId();
        
        CertificateUpdate certificateUpdate = new CertificateUpdate();
        certificateUpdate.setFileName(info.getFileName());
        certificateUpdate.setTimestamp(info.getSendDate());
        certificateUpdate.setUpdateId(info.getCertificateId());
        certificateUpdate.setYukonUpdateId(info.getUpdateId());
        
        List<RfnGateway> successful = new ArrayList<>();
        Map<RfnGateway, GatewayCertificateUpdateStatus> failed = new HashMap<>();
        List<RfnGateway> pending = new ArrayList<>();
        for (Map.Entry<Integer, GatewayCertificateUpdateStatus> entry : info.getGatewayStatuses().entrySet()) {
            RfnGateway gateway = gateways.get(entry.getKey());
            if (entry.getValue().isSuccessful()) {
                successful.add(gateway);
            } else if (entry.getValue().isInProgress()) {
                pending.add(gateway);
            } else {
                failed.put(gateway, entry.getValue());
            }
        }
        
        certificateUpdate.setSuccessful(successful);
        certificateUpdate.setPending(pending);
        certificateUpdate.setFailed(failed);
        return certificateUpdate;
    }
    
    @Override
    public List<CertificateUpdate> getAllCertificateUpdates() {
        
        List<GatewayCertificateUpdateInfo> allUpdateInfo = certificateUpdateDao.getAllUpdateInfo();
        Map<Integer, RfnGateway> gateways = gatewayService.getAllGatewaysByPaoId();
        List<CertificateUpdate> updates = new ArrayList<>();
        
        for (GatewayCertificateUpdateInfo info : allUpdateInfo) {
            CertificateUpdate certificateUpdate = new CertificateUpdate();
            certificateUpdate.setFileName(info.getFileName());
            certificateUpdate.setTimestamp(info.getSendDate());
            certificateUpdate.setUpdateId(info.getCertificateId());
            certificateUpdate.setYukonUpdateId(info.getUpdateId());
            
            List<RfnGateway> successful = new ArrayList<>();
            Map<RfnGateway, GatewayCertificateUpdateStatus> failed = new HashMap<>();
            List<RfnGateway> pending = new ArrayList<>();
            for (Map.Entry<Integer, GatewayCertificateUpdateStatus> entry : info.getGatewayStatuses().entrySet()) {
                RfnGateway gateway = gateways.get(entry.getKey());
                if (entry.getValue().isSuccessful()) {
                    successful.add(gateway);
                } else if (entry.getValue().isInProgress()) {
                    pending.add(gateway);
                } else {
                    failed.put(gateway, entry.getValue());
                }
            }
            
            certificateUpdate.setSuccessful(successful);
            certificateUpdate.setPending(pending);
            certificateUpdate.setFailed(failed);
            updates.add(certificateUpdate);
        }
        return updates;
    }
    
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
