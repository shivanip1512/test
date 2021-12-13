package com.cannontech.common.rfn.service.impl;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.rfn.dao.RfnDeviceDao;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.common.rfn.model.RfnDeviceDeleteConfirmationReply;
import com.cannontech.common.rfn.model.RfnDeviceDeleteInitialReply;
import com.cannontech.common.rfn.model.RfnDeviceDeleteRequest;
import com.cannontech.common.rfn.service.RfnDeviceDeletionMessageService;
import com.cannontech.common.util.jms.JmsReplyReplyHandler;
import com.cannontech.common.util.jms.RequestReplyReplyTemplate;
import com.cannontech.common.util.jms.YukonJmsTemplate;
import com.cannontech.common.util.jms.YukonJmsTemplateFactory;
import com.cannontech.common.util.jms.api.JmsApiDirectory;
import com.cannontech.core.dao.NotFoundException;

public class RfnDeviceDeletionMessageServiceImpl implements RfnDeviceDeletionMessageService {

    @Autowired private YukonJmsTemplateFactory jmsTemplateFactory;
    @Autowired private ConfigurationSource configurationSource;
    @Autowired private RfnDeviceDao rfnDeviceDao;
    private final static Logger log = YukonLogManager.getLogger(RfnDeviceDeletionMessageServiceImpl.class);

    private YukonJmsTemplate rfnDeviceDeletionJmsTemplate;
    private RequestReplyReplyTemplate<RfnDeviceDeleteInitialReply, RfnDeviceDeleteConfirmationReply> rfnDeviceDeletionTemplate;

    @Override
    public void sendRfnDeviceDeletionRequest(Integer paoId) {
        RfnDevice rfnDevice = null;
        try {
            rfnDevice = rfnDeviceDao.getDeviceForId(paoId);
        } catch (NotFoundException e) {
            return;
        }

        RfnDeviceDeleteRequest request = new RfnDeviceDeleteRequest();
        request.setRfnIdentifier(rfnDevice.getRfnIdentifier());

        JmsReplyReplyHandler<RfnDeviceDeleteInitialReply, RfnDeviceDeleteConfirmationReply> handler = new JmsReplyReplyHandler<RfnDeviceDeleteInitialReply, RfnDeviceDeleteConfirmationReply>() {

            @Override
            public void handleException(Exception e) {
                log.error(request.getRfnIdentifier() + " - device deletion request failed", e);
            }

            @Override
            public void complete() {
            }

            @Override
            public void handleTimeout1() {
                log.info("{} - device deletion request timed out receiving initial response.", request.getRfnIdentifier());
            }

            @Override
            public boolean handleReply1(RfnDeviceDeleteInitialReply t) {
                log.info("{} received reply - {} from NM", request.getRfnIdentifier(), t.getReplyType());
                if (!t.isSuccess()) {
                    // Request unsuccessful, device not present
                    return false;
                }
                // Device is present in the NM Db
                return true;
            }

            @Override
            public Class<RfnDeviceDeleteInitialReply> getExpectedType1() {
                return RfnDeviceDeleteInitialReply.class;
            }

            @Override
            public void handleTimeout2() {
                log.info("{} - device deletion request timed out.", request.getRfnIdentifier());
            }

            @Override
            public void handleReply2(RfnDeviceDeleteConfirmationReply t) {
                log.info("{} - received reply ({}) from NM ", request.getRfnIdentifier(), t.getReplyType());
            }

            @Override
            public Class<RfnDeviceDeleteConfirmationReply> getExpectedType2() {
                return RfnDeviceDeleteConfirmationReply.class;
            }
        };

        rfnDeviceDeletionTemplate.send(request, handler);
    }

    @PostConstruct
    public void initialize() {
        rfnDeviceDeletionJmsTemplate = jmsTemplateFactory.createTemplate(JmsApiDirectory.RFN_DEVICE_DELETE);
        rfnDeviceDeletionTemplate = new RequestReplyReplyTemplate<>("RFN_DEVICEDELETION_REQUEST", configurationSource,
                rfnDeviceDeletionJmsTemplate);
    }
}