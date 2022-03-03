package com.cannontech.stars.dr.jms.service.impl;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.Logger;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.jms.YukonJmsTemplate;
import com.cannontech.common.util.jms.YukonJmsTemplateFactory;
import com.cannontech.common.util.jms.api.JmsApiDirectory;
import com.cannontech.stars.dr.hardware.model.LMHardwareControlGroup;
import com.cannontech.stars.dr.jms.message.DrJmsMessageType;
import com.cannontech.stars.dr.jms.message.DrProgramStatusJmsMessage;
import com.cannontech.stars.dr.jms.message.EnrollmentJmsMessage;
import com.cannontech.stars.dr.jms.message.OptOutOptInJmsMessage;
import com.cannontech.stars.dr.jms.service.DrJmsMessagingService;
import com.cannontech.stars.dr.optout.model.OptOutEvent;

public class DrJmsMessagingServiceImpl implements DrJmsMessagingService {

    private final static Logger log = YukonLogManager.getLogger(DrJmsMessagingServiceImpl.class);
    @Autowired private YukonJmsTemplateFactory jmsTemplateFactory;

    private YukonJmsTemplate enrollmentNotificationJmsTemplate;
    private YukonJmsTemplate optOutInNotificationJmsTemplate;
    private YukonJmsTemplate programStatusNotificationJmsTemplate;

    @PostConstruct
    public void init() {
        enrollmentNotificationJmsTemplate = jmsTemplateFactory.createTemplate(JmsApiDirectory.ENROLLMENT_NOTIFICATION);
        optOutInNotificationJmsTemplate = jmsTemplateFactory.createTemplate(JmsApiDirectory.OPTOUTIN_NOTIFICATION);
        programStatusNotificationJmsTemplate= jmsTemplateFactory.createTemplate(JmsApiDirectory.PROGRAM_STATUS_NOTIFICATION);
    }

    @Override
    public void publishEnrollmentNotice(LMHardwareControlGroup controlInformation) {
        EnrollmentJmsMessage message = new EnrollmentJmsMessage();

        setEnrollmentJmsMessageFields(message, controlInformation);
        message.setMessageType(DrJmsMessageType.ENROLLMENT);

        log.debug("Enrollment message pushed to jms queue: " + message);
        enrollmentNotificationJmsTemplate.convertAndSend(message);
    }

    @Override
    public void publishUnEnrollmentNotice(LMHardwareControlGroup controlInformation) {
        EnrollmentJmsMessage message = new EnrollmentJmsMessage();

        setEnrollmentJmsMessageFields(message, controlInformation);
        message.setEnrollmentStopTime(controlInformation.getGroupEnrollStop());
        message.setMessageType(DrJmsMessageType.UNENROLLMENT);

        log.debug("Unenrollment message pushed to jms queue: " + message);
        enrollmentNotificationJmsTemplate.convertAndSend(message);
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
        optOutInNotificationJmsTemplate.convertAndSend(message);
    }

    @Override
    public void publishStopOptOutNotice(Integer inventoryId, Instant stopDate) {
        OptOutOptInJmsMessage message = new OptOutOptInJmsMessage();

        message.setStopDate(stopDate);
        message.setInventoryId(inventoryId);
        message.setMessageType(DrJmsMessageType.STOPOPTOUT);

        log.debug("Stop OptOut message pushed to jms queue: " + message);
        optOutInNotificationJmsTemplate.convertAndSend(message);

    }

    @Override
    public void publishProgramStatusNotice(DrProgramStatusJmsMessage message) {

        log.debug("Program Status Message pushed to jms queue: " + message);
        programStatusNotificationJmsTemplate.convertAndSend(message);
    }

}
