package com.cannontech.stars.dr.jms.service.impl;

import javax.jms.ConnectionFactory;

import org.apache.logging.log4j.Logger;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.jms.api.JmsApiDirectory;
import com.cannontech.stars.dr.hardware.model.LMHardwareControlGroup;
import com.cannontech.stars.dr.jms.message.DrAttributeDataJmsMessage;
import com.cannontech.stars.dr.jms.message.DrJmsMessageType;
import com.cannontech.stars.dr.jms.message.EnrollmentJmsMessage;
import com.cannontech.stars.dr.jms.message.OptOutOptInJmsMessage;
import com.cannontech.stars.dr.jms.service.DrJmsMessagingService;
import com.cannontech.stars.dr.optout.model.OptOutEvent;

public class DrJmsMessagingServiceImpl implements DrJmsMessagingService {

    private final static Logger log = YukonLogManager.getLogger(DrJmsMessagingServiceImpl.class);
    private JmsTemplate jmsTemplate;

    @Override
    public void publishEnrollmentNotice(LMHardwareControlGroup controlInformation) {
        EnrollmentJmsMessage message = new EnrollmentJmsMessage();

        setEnrollmentJmsMessageFields(message, controlInformation);
        message.setMessageType(DrJmsMessageType.ENROLLMENT);

        log.debug("Enrollment message pushed to jms queue: " + message);
        jmsTemplate.convertAndSend(JmsApiDirectory.ENROLLMENT_NOTIFICATION.getQueue().getName(), message);
    }

    @Override
    public void publishUnEnrollmentNotice(LMHardwareControlGroup controlInformation) {
        EnrollmentJmsMessage message = new EnrollmentJmsMessage();

        setEnrollmentJmsMessageFields(message, controlInformation);
        message.setEnrollmentStopTime(controlInformation.getGroupEnrollStop());
        message.setMessageType(DrJmsMessageType.UNENROLLMENT);

        log.debug("Unenrollment message pushed to jms queue: " + message);
        jmsTemplate.convertAndSend(JmsApiDirectory.ENROLLMENT_NOTIFICATION.getQueue().getName(), message);
    }

    private void setEnrollmentJmsMessageFields(EnrollmentJmsMessage message, LMHardwareControlGroup controlInformation) {
        message.setAccountId(controlInformation.getAccountId());
        message.setInventoryId(controlInformation.getInventoryId());
        message.setProgramId(controlInformation.getProgramId());
        message.setLoadGroupId(controlInformation.getLmGroupId());
        message.setRelayNumber(controlInformation.getRelay());
        message.setEnrollmentStartTime(controlInformation.getGroupEnrollStart());

    }
    @Override
    public void publishStartOptOutNotice(Integer inventoryId, OptOutEvent event) {
        OptOutOptInJmsMessage message = new OptOutOptInJmsMessage();

        message.setStopDate(event.getStopDate());
        message.setStartDate(event.getStartDate());
        message.setInventoryId(inventoryId);
        message.setMessageType(DrJmsMessageType.OPTOUT);

        log.debug("OptOut message pushed to jms queue: " + message);
        jmsTemplate.convertAndSend(JmsApiDirectory.OPTOUTIN_NOTIFICATION.getQueue().getName(), message);
    }

    @Override
    public void publishStopOptOutNotice(Integer inventoryId, Instant stopDate) {
        OptOutOptInJmsMessage message = new OptOutOptInJmsMessage();

        message.setStopDate(stopDate);
        message.setInventoryId(inventoryId);
        message.setMessageType(DrJmsMessageType.STOPOPTOUT);

        log.debug("Stop OptOut message pushed to jms queue: " + message);
        jmsTemplate.convertAndSend(JmsApiDirectory.OPTOUTIN_NOTIFICATION.getQueue().getName(), message);

    }

    @Autowired
    public void setConnectionFactory(ConnectionFactory connectionFactory) {
        jmsTemplate = new JmsTemplate(connectionFactory);
    }

    @Override
    public void publishAttributeDataMessageNotice(DrAttributeDataJmsMessage message) {
        log.debug("Attribute Data message pushed to jms queue: " + message);
        jmsTemplate.convertAndSend(JmsApiDirectory.DATA_NOTIFICATION.getQueue().getName(), message);

    }

}
