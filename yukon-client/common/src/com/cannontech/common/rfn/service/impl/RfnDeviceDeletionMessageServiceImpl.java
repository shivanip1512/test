package com.cannontech.common.rfn.service.impl;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.rfn.dao.RfnDeviceDao;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.rfn.message.device.RfnDeviceDeleteInitialReply;
import com.cannontech.common.rfn.message.device.RfnDeviceDeleteRequest;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.common.rfn.service.RfnDeviceDeletionMessageService;
import com.cannontech.common.util.jms.JmsReplyHandler;
import com.cannontech.common.util.jms.RequestReplyTemplate;
import com.cannontech.common.util.jms.RequestReplyTemplateImpl;
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
    private RequestReplyTemplate<RfnDeviceDeleteInitialReply> rfnDeviceDeletionTemplate;

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

        JmsReplyHandler<RfnDeviceDeleteInitialReply> handler = new JmsReplyHandler<RfnDeviceDeleteInitialReply>() {

            @Override
            public void handleException(Exception e) {
                log.error(request.getRfnIdentifier() + " - device deletion request failed", e);
            }

            @Override
            public void complete() {
            }

            @Override
            public void handleTimeout() {
                log.info("{} - device deletion request timed out receiving initial response.", request.getRfnIdentifier());

            }

            @Override
            public void handleReply(RfnDeviceDeleteInitialReply t) {
                log.info("{} received reply - {} from NM", request.getRfnIdentifier(), t.getReplyType());

            }

            @Override
            public Class<RfnDeviceDeleteInitialReply> getExpectedType() {
                return RfnDeviceDeleteInitialReply.class;

            }
        };

        rfnDeviceDeletionTemplate.send(request, handler);
    }

    @PostConstruct
    public void initialize() {
        rfnDeviceDeletionJmsTemplate = jmsTemplateFactory.createTemplate(JmsApiDirectory.RFN_DEVICE_DELETE);
        rfnDeviceDeletionTemplate = new RequestReplyTemplateImpl<>("RFN_DEVICEDELETION_REQUEST", configurationSource,
                rfnDeviceDeletionJmsTemplate);
    }
}