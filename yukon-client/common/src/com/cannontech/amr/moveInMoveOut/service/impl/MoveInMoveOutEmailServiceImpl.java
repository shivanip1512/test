package com.cannontech.amr.moveInMoveOut.service.impl;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.core.io.Resource;

import com.cannontech.amr.errors.model.SpecificDeviceErrorDescription;
import com.cannontech.amr.moveInMoveOut.bean.MoveInResult;
import com.cannontech.amr.moveInMoveOut.bean.MoveOutResult;
import com.cannontech.amr.moveInMoveOut.service.MoveInMoveOutEmailService;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.util.FormattingTemplateProcessor;
import com.cannontech.common.util.TemplateProcessorFactory;
import com.cannontech.core.service.PointFormattingService;
import com.cannontech.core.service.PointFormattingService.Format;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.tools.email.EmailHtmlMessage;
import com.cannontech.tools.email.EmailMessage;
import com.cannontech.tools.email.EmailService;
import com.cannontech.user.YukonUserContext;

public class MoveInMoveOutEmailServiceImpl implements MoveInMoveOutEmailService {

    private final Logger logger = YukonLogManager.getLogger(MoveInMoveOutEmailServiceImpl.class);

    private TemplateProcessorFactory templateProcessorFactory;
    private EmailService emailService;
    private PointFormattingService pointFormattingService;
    @Autowired private YukonUserContextMessageSourceResolver resolver;
    
    String emailSubject = "Email notification from Energy Services Operations Center";

    Resource moveInFailedEmail = null;
    Resource moveInScheduledEmail = null;
    Resource moveInSuccessEmail = null;
    Resource moveOutFailedEmail = null;
    Resource moveOutScheduledEmail = null;
    Resource moveOutSuccessEmail = null;

    private final String baseSubjectFormatMoveIn = "Move in for {prevMeterName} from {startDate|BOTH} - {stopDate|BOTH} {status}.";
    private final String baseSubjectFormatMoveOut = "Move out for {prevMeterName} from {startDate|BOTH} - {stopDate|BOTH} {status}.";
    private final String moveInSuccessSubjectFormat = "Move in for {newMeterName} from {startDate|BOTH} - {stopDate|BOTH} {status}.";
    private final String scheduledMsgSub = "Scheduled meter reading for {prevMeterName}.";

    @Override
    public void createMoveInEmail(MoveInResult moveInResult,
                                  YukonUserContext userContext) {
        if (!StringUtils.isBlank(moveInResult.getEmailAddress())) {
            if (moveInResult.isScheduled()) {
                createMoveInScheduleEmail(moveInResult, userContext);
            } else {
                if (moveInResult.getErrors().isEmpty() && StringUtils.isBlank(moveInResult.getErrorMessage()) ) {
                    createMoveInSuccessEmail(moveInResult, userContext);
                } else {
                    createMoveInFailureEmail(moveInResult, userContext);
                }
            }
        }
    }

    @Override
    public void createMoveOutEmail(MoveOutResult moveOutResult,
                                   YukonUserContext userContext) {
        if (!StringUtils.isBlank(moveOutResult.getEmailAddress())) {
            if (moveOutResult.isScheduled()) {
                createMoveOutScheduleEmail(moveOutResult, userContext);
            } else {
                if (moveOutResult.getErrors().isEmpty() && StringUtils.isBlank(moveOutResult.getErrorMessage())) {
                    createMoveOutSuccessEmail(moveOutResult, userContext);
                } else {
                    createMoveOutFailureEmail(moveOutResult, userContext);
                }
            }
        }
    }

    private void createMoveInSuccessEmail(MoveInResult moveInResult,
                                          YukonUserContext userContext) {
        FormattingTemplateProcessor tp = templateProcessorFactory.getFormattingTemplateProcessor(userContext);
        Map<String, Object> msgData = new HashMap<String, Object>();
        msgData.put("status", "completed");
        msgData.put("statusMsg",
                    "Move in request for " + moveInResult.getNewMeter()
                                                         .getName() + " is complete.");

        msgData.put("prevMeterName", moveInResult.getPreviousMeter().getName());
        msgData.put("prevMeterNumber", moveInResult.getPreviousMeter()
                                                   .getMeterNumber());
        msgData.put("newMeterName", moveInResult.getNewMeter().getName());
        msgData.put("newMeterNumber", moveInResult.getNewMeter()
                                                  .getMeterNumber());

        if (moveInResult.getPreviousMeter()
                        .getName()
                        .equals(moveInResult.getNewMeter().getName())) {
            msgData.put("meterNameChange", false);
        } else {
            msgData.put("meterNameChange", true);
        }
        if (moveInResult.getPreviousMeter()
                        .getMeterNumber()
                        .equals(moveInResult.getNewMeter().getMeterNumber())) {
            msgData.put("meterNumberChange", false);
        } else {
            msgData.put("meterNumberChange", true);
        }
        
        Set<String> deviceGroupList = new TreeSet<String>();
        for(DeviceGroup deviceGroup : moveInResult.getDeviceGroupsRemoved()){
           deviceGroupList.add(deviceGroup.getName()); 
        }
        msgData.put("deviceGroups", deviceGroupList);
        
        setDatesMoveIn(moveInResult, msgData, userContext);

        msgData.put("currentUsage",
                    pointFormattingService.getValueString(moveInResult.getCurrentReading(),
                                                          Format.FULL,
                                                          userContext));
        msgData.put("calculatedUsage",
                    pointFormattingService.getValueString(moveInResult.getCalculatedDifference(),
                                                          Format.SHORT,
                                                          userContext));
        msgData.put("calculatedTotalUsage",
                    pointFormattingService.getValueString(moveInResult.getCalculatedPreviousReading(),
                                                          Format.FULL,
                                                          userContext));

        msgData.put("user", userContext.getYukonUser().getUsername());
        msgData.put("processingDate", new Date());

        String subject = tp.process(moveInSuccessSubjectFormat, msgData);
        String body = null;
        try{
            body = tp.process(moveInSuccessEmail, msgData);
        } catch (IOException ioe) {
            logger.error("Resource template could not be opened for the move in move out email service ",ioe);
        }
        
        sendEmail(moveInResult.getEmailAddress(), subject, body);
    }

    private void createMoveInScheduleEmail(MoveInResult moveInResult,
                                           YukonUserContext userContext) {
        FormattingTemplateProcessor tp = templateProcessorFactory.getFormattingTemplateProcessor(userContext);
        Map<String, Object> msgData = new TreeMap<String, Object>();
        msgData.put("status", "scheduled");

        msgData.put("prevMeterNumber", moveInResult.getPreviousMeter()
                                                 .getMeterNumber());
        msgData.put("prevMeterName", moveInResult.getPreviousMeter().getName());
        msgData.put("startDate", moveInResult.getMoveInDate());

        String subject = tp.process(scheduledMsgSub, msgData);
        String body = null;
        try{
            body = tp.process(moveInScheduledEmail, msgData);
        } catch (IOException ioe) {
            logger.error("Resource template could not be opened for the move in move out email service ",ioe);
        }
        
        sendEmail(moveInResult.getEmailAddress(), subject, body);
    }

    private void createMoveInFailureEmail(MoveInResult moveInResult,
                                          YukonUserContext userContext) {
        FormattingTemplateProcessor tp = templateProcessorFactory.getFormattingTemplateProcessor(userContext);
        Map<String, Object> msgData = new TreeMap<String, Object>();
        msgData.put("status", "failed");
        msgData.put("statusMsg",
                    "The system was not able to process the move in request for " + moveInResult.getPreviousMeter()
                                                                                                .getName() + " for the following reasons.");

        msgData.put("prevMeterNumber", moveInResult.getPreviousMeter()
                                                   .getMeterNumber());
        msgData.put("prevMeterName", moveInResult.getPreviousMeter().getName());

        setDatesMoveIn(moveInResult, msgData, userContext);
        buildErrorStr(moveInResult.getErrors(), moveInResult.getErrorMessage(), msgData, userContext);
        
        String subject = tp.process(baseSubjectFormatMoveIn, msgData);
        String body = null;
        try{
            body = tp.process(moveInFailedEmail, msgData);
        } catch (IOException ioe) {
            logger.error("Resource template could not be opened for the move in move out email service ",ioe);
        }
        
        sendEmail(moveInResult.getEmailAddress(), subject, body, body);
    }

    private void createMoveOutSuccessEmail(MoveOutResult moveOutResult,
                                           YukonUserContext userContext) {
        FormattingTemplateProcessor tp = templateProcessorFactory.getFormattingTemplateProcessor(userContext);
        Map<String, Object> msgData = new HashMap<String, Object>();
        msgData.put("status", "completed");
        msgData.put("statusMsg",
                    "Move out request for " + moveOutResult.getPreviousMeter()
                                                           .getName() + " is complete.");
        msgData.put("prevMeterName", moveOutResult.getPreviousMeter().getName());

        setDatesMoveOut(moveOutResult, msgData, userContext);

        Set<String> deviceGroupList = new TreeSet<String>();
        for(DeviceGroup deviceGroup : moveOutResult.getDeviceGroupsAdded()){
           deviceGroupList.add(deviceGroup.getName()); 
        }
        msgData.put("deviceGroups", deviceGroupList);
        
        msgData.put("currentUsage",
                    pointFormattingService.getValueString(moveOutResult.getCurrentReading(),
                                                          Format.FULL,
                                                          userContext));
        msgData.put("calculatedUsage",
                    pointFormattingService.getValueString(moveOutResult.getCalculatedDifference(),
                                                          Format.SHORT,
                                                          userContext));
        msgData.put("calculatedTotalUsage",
                    pointFormattingService.getValueString(moveOutResult.getCalculatedReading(),
                                                          Format.FULL,
                                                          userContext));

        msgData.put("user", userContext.getYukonUser().getUsername());
        msgData.put("processingDate", new Date());

        String subject = tp.process(baseSubjectFormatMoveOut, msgData);
        String body = null;
        try{
            body = tp.process(moveOutSuccessEmail, msgData);
        } catch (IOException ioe) {
            logger.error("Resource template could not be opened for the move in move out email service ",ioe);
        }
        
        sendEmail(moveOutResult.getEmailAddress(), subject, body);
    }

    private void createMoveOutScheduleEmail(MoveOutResult moveOutResult,
                                            YukonUserContext userContext) {
        FormattingTemplateProcessor tp = templateProcessorFactory.getFormattingTemplateProcessor(userContext);
        Map<String, Object> msgData = new TreeMap<String, Object>();
        msgData.put("status", "scheduled");

        msgData.put("prevMeterNumber", moveOutResult.getPreviousMeter().getMeterNumber());
        msgData.put("prevMeterName", moveOutResult.getPreviousMeter().getName());
        msgData.put("startDate", moveOutResult.getMoveOutDate());

        String subject = tp.process(scheduledMsgSub, msgData);
        String body = null;
        try{
            body = tp.process(moveOutScheduledEmail, msgData);
        } catch (IOException ioe) {
            logger.error("Resource template could not be opened for the move in move out email service ",ioe);
        }
        
        sendEmail(moveOutResult.getEmailAddress(), subject, body);
    }

    private void createMoveOutFailureEmail(MoveOutResult moveOutResult,
                                           YukonUserContext userContext) {
        FormattingTemplateProcessor tp = templateProcessorFactory.getFormattingTemplateProcessor(userContext);
        Map<String, Object> msgData = new TreeMap<String, Object>();
        msgData.put("status", "failed");
        msgData.put("statusMsg",
                    "The system was not able to process the move out request for " + moveOutResult.getPreviousMeter()
                                                                                                  .getName() + " for the following reasons.");

        msgData.put("prevMeterNumber", moveOutResult.getPreviousMeter()
                                                    .getMeterNumber());
        msgData.put("prevMeterName", moveOutResult.getPreviousMeter().getName());

        setDatesMoveOut(moveOutResult, msgData, userContext);
        buildErrorStr(moveOutResult.getErrors(), moveOutResult.getErrorMessage(), msgData, userContext);

        String subject = tp.process(baseSubjectFormatMoveOut, msgData);
        String body = null;
        try{
            body = tp.process(moveOutFailedEmail, msgData);
        } catch (IOException ioe) {
            logger.error("Resource template could not be opened for the move in move out email service ",ioe);
        }
        
        sendEmail(moveOutResult.getEmailAddress(), subject, body, body);
    }

    private void sendEmail(String toEmailAddress, String subject, String body, String htmlBody) {
        try {
        	EmailHtmlMessage emailMessage = new EmailHtmlMessage(InternetAddress.parse(toEmailAddress), subject, body, htmlBody);
            emailService.sendMessage(emailMessage);
        } catch (MessagingException e) {
            logger.warn("Unable to email message to address " + toEmailAddress + ".", e);
        }
    }
    
    private void sendEmail(String toEmailAddress, String subject, String body) {
        try {
        	EmailMessage emailMessage = new EmailMessage(InternetAddress.parse(toEmailAddress), subject, body);
            emailService.sendMessage(emailMessage);
        } catch (MessagingException e) {
            logger.warn("Unable to email message to address " + toEmailAddress + ".", e);
        }
    }


    
    private void setDatesMoveOut(MoveOutResult moveOutResult,
            Map<String, Object> msgData, YukonUserContext userContext) {

        Date currentReadDate = new Date();

        msgData.put("startDate", moveOutResult.getMoveOutDate());
        msgData.put("stopDate", currentReadDate);
    }

    private void setDatesMoveIn(MoveInResult moveInResult,
            Map<String, Object> msgData, YukonUserContext userContext) {

        Date moveInDate = moveInResult.getMoveInDate();
        Date currentDate = new Date();

        msgData.put("startDate", moveInDate);
        msgData.put("stopDate", currentDate);
    }

    private void buildErrorStr(Set<SpecificDeviceErrorDescription> errors,
            String errorMessage, Map<String, Object> msgData, YukonUserContext userContext) {
    	
    	MessageSourceAccessor messageSourceAccessor = resolver.getMessageSourceAccessor(userContext);
        StringBuilder errorsStr = new StringBuilder();

        if (errorMessage != null){
            msgData.put("errorMessage", errorMessage);
        } else {
            msgData.put("errorMessage", " ");
        }
        
        for (SpecificDeviceErrorDescription error : errors) {
        	errorsStr.append("<br>");
        	errorsStr.append("<br>");
        	errorsStr.append(messageSourceAccessor.getMessage(error.getSummary()));
        	errorsStr.append("<br>");
			if (error.getDetail() != null) {
			  	errorsStr.append("<br>");
				errorsStr.append(messageSourceAccessor.getMessage(error.getDetail()));
			}
        }   
        msgData.put("errors", errorsStr);
        
    }
    
    @Required
    public void setEmailService(EmailService emailService) {
        this.emailService = emailService;
    }

    @Required
    public void setPointFormattingService(
            PointFormattingService pointFormattingService) {
        this.pointFormattingService = pointFormattingService;
    }

    /* Injection of all the templates used for this service*/
    @Required
    public void setMoveInFailedEmail(Resource moveInFailedEmail) {
        this.moveInFailedEmail = moveInFailedEmail;
    }

    @Required
    public void setMoveInScheduledEmail(Resource moveInScheduledEmail) {
        this.moveInScheduledEmail = moveInScheduledEmail;
    }

    @Required
    public void setMoveInSuccessEmail(Resource moveInSuccessEmail) {
        this.moveInSuccessEmail = moveInSuccessEmail;
    }

    @Required
    public void setMoveOutFailedEmail(Resource moveOutFailedEmail) {
        this.moveOutFailedEmail = moveOutFailedEmail;
    }

    @Required
    public void setMoveOutScheduledEmail(Resource moveOutScheduledEmail) {
        this.moveOutScheduledEmail = moveOutScheduledEmail;
    }

    @Required
    public void setMoveOutSuccessEmail(Resource moveOutSuccessEmail) {
        this.moveOutSuccessEmail = moveOutSuccessEmail;
    }
    
    @Autowired
    public void setTemplateProcessorFactory(
            TemplateProcessorFactory templateProcessorFactory) {
        this.templateProcessorFactory = templateProcessorFactory;
    }

}
