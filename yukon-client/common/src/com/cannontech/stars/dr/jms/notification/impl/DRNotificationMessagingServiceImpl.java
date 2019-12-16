package com.cannontech.stars.dr.jms.notification.impl;

import javax.jms.ConnectionFactory;

import org.apache.logging.log4j.Logger;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.jms.api.JmsApiDirectory;
import com.cannontech.stars.dr.hardware.model.LMHardwareControlGroup;
import com.cannontech.stars.dr.jms.notification.DRNotificationMessagingService;
import com.cannontech.stars.dr.jms.notification.message.DRNotificationDataMessage;
import com.cannontech.stars.dr.jms.notification.message.DRNotificationMessageType;
import com.cannontech.stars.dr.jms.notification.message.EnrollmentNotificationMessage;
import com.cannontech.stars.dr.jms.notification.message.OptOutInNotificationMessage;
import com.cannontech.stars.dr.optout.model.OptOutEvent;

public class DRNotificationMessagingServiceImpl implements DRNotificationMessagingService {

    private final static Logger log = YukonLogManager.getLogger(DRNotificationMessagingServiceImpl.class);
    private JmsTemplate jmsTemplate;

    @Override
    public void sendEnrollmentNotification(LMHardwareControlGroup controlInformation) {
        EnrollmentNotificationMessage message = new EnrollmentNotificationMessage();

        setEnrollmentNotificationMessageFields(message, controlInformation);
        message.setMessageType(DRNotificationMessageType.ENROLLMENT);

        log.debug("Enrollment message pushed to jms queue: " + message);
        jmsTemplate.convertAndSend(JmsApiDirectory.ENROLLMENT_NOTIFICATION.getQueue().getName(), message);
    }

    @Override
    public void sendUnenrollmentNotification(LMHardwareControlGroup controlInformation) {
        EnrollmentNotificationMessage message = new EnrollmentNotificationMessage();

        setEnrollmentNotificationMessageFields(message, controlInformation);
        message.setEnrollmentStopTime(controlInformation.getGroupEnrollStop());
        message.setMessageType(DRNotificationMessageType.UNENROLLMENT);

        log.debug("Unenrollment message pushed to jms queue: " + message);
        jmsTemplate.convertAndSend(JmsApiDirectory.ENROLLMENT_NOTIFICATION.getQueue().getName(), message);
    }

    private void setEnrollmentNotificationMessageFields(EnrollmentNotificationMessage message, LMHardwareControlGroup controlInformation) {
        message.setAccountId(controlInformation.getAccountId());
        message.setInventoryId(controlInformation.getInventoryId());
        message.setProgramId(controlInformation.getProgramId());
        message.setLoadGroupId(controlInformation.getLmGroupId());
        message.setRelayNumber(controlInformation.getRelay());
        message.setEnrollmentStartTime(controlInformation.getGroupEnrollStart());

    }
    @Override
    public void sendStartOptOutNotification(Integer inventoryId, OptOutEvent event) {
        OptOutInNotificationMessage message = new OptOutInNotificationMessage();

        message.setStopDate(event.getStopDate());
        message.setStartDate(event.getStartDate());
        message.setInventoryId(inventoryId);
        message.setMessageType(DRNotificationMessageType.OPTOUT);

        log.debug("OptOut message pushed to jms queue: " + message);
        jmsTemplate.convertAndSend(JmsApiDirectory.OPTOUTIN_NOTIFICATION.getQueue().getName(), message);
    }

    @Override
    public void sendStopOptOutNotification(Integer inventoryId, Instant stopDate) {
        OptOutInNotificationMessage message = new OptOutInNotificationMessage();

        message.setStopDate(stopDate);
        message.setInventoryId(inventoryId);
        message.setMessageType(DRNotificationMessageType.STOPOPTOUT);

        log.debug("Stop OptOut message pushed to jms queue: " + message);
        jmsTemplate.convertAndSend(JmsApiDirectory.OPTOUTIN_NOTIFICATION.getQueue().getName(), message);

    }

    @Autowired
    public void setConnectionFactory(ConnectionFactory connectionFactory) {
        jmsTemplate = new JmsTemplate(connectionFactory);
    }

    @Override
    public void sendDataMessageNotification(DRNotificationDataMessage message) {
        log.debug("Stop OptOut message pushed to jms queue: " + message);
        jmsTemplate.convertAndSend(JmsApiDirectory.DATA_NOTIFICATION.getQueue().getName(), message);

    }

}
