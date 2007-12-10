package com.cannontech.amr.moveInMoveOut.service.impl;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.mail.MessagingException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.core.io.Resource;

import com.cannontech.amr.errors.model.DeviceErrorDescription;
import com.cannontech.amr.moveInMoveOut.bean.MoveInResult;
import com.cannontech.amr.moveInMoveOut.bean.MoveOutResult;
import com.cannontech.amr.moveInMoveOut.service.MoveInMoveOutEmailService;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.util.SimpleTemplateProcessor;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.PointFormattingService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.core.service.PointFormattingService.Format;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.tools.email.DefaultEmailMessage;
import com.cannontech.tools.email.EmailService;

public class MoveInMoveOutEmailServiceImpl implements MoveInMoveOutEmailService {

    private Logger logger = YukonLogManager.getLogger(MoveInMoveOutEmailServiceImpl.class);

    private DateFormattingService dateFormattingService;
    private EmailService emailService;
    private PointFormattingService pointFormattingService;
    
    String emailSubject = "Email notification from Energy Services Operations Center";

    Resource moveInFailedEmail = null;
    Resource moveInScheduledEmail = null;
    Resource moveInSuccessEmail = null;
    Resource moveOutFailedEmail = null;
    Resource moveOutScheduledEmail = null;
    Resource moveOutSuccessEmail = null;

    private final String baseSubjectFormat = "Moving information for {prevMeterName} from {startDate} - {stopDate} {status}.";
    private final String moveInSuccessSubjectFormat = "Moving information for {newMeterName} from {startDate} - {stopDate} {status}.";
    private final String scheduledMsgSub = "Scheduled meter reading for {prevMeterName}.";

    public void createMoveInEmail(MoveInResult moveInResult,
            LiteYukonUser liteYukonUser) {
        if (!StringUtils.isBlank(moveInResult.getEmailAddress())) {
            if (moveInResult.isScheduled()) {
                createMoveInScheduleEmail(moveInResult, liteYukonUser);
            } else {
                if (moveInResult.getErrors().isEmpty()) {
                    createMoveInSuccessEmail(moveInResult, liteYukonUser);
                } else {
                    createMoveInFailureEmail(moveInResult, liteYukonUser);
                }
            }
        }
    }

    public void createMoveOutEmail(MoveOutResult moveOutResult,
            LiteYukonUser liteYukonUser) {
        if (!StringUtils.isBlank(moveOutResult.getEmailAddress())) {
            if (moveOutResult.isScheduled()) {
                createMoveOutScheduleEmail(moveOutResult, liteYukonUser);
            } else {
                if (moveOutResult.getErrors().isEmpty()) {
                    createMoveOutSuccessEmail(moveOutResult, liteYukonUser);
                } else {
                    createMoveOutFailureEmail(moveOutResult, liteYukonUser);
                }
            }
        }
    }

    private void createMoveInSuccessEmail(MoveInResult moveInResult,
            LiteYukonUser liteYukonUser) {
        SimpleTemplateProcessor tp = new SimpleTemplateProcessor();
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
        
        setDatesMoveIn(moveInResult, msgData, liteYukonUser);

        msgData.put("currentUsage",
                    pointFormattingService.getValueString(moveInResult.getCurrentReading(),
                                                          Format.FULL,
                                                          liteYukonUser));
        msgData.put("calculatedUsage",
                    pointFormattingService.getValueString(moveInResult.getCalculatedDifference(),
                                                          Format.SHORT));
        msgData.put("calculatedTotalUsage",
                    pointFormattingService.getValueString(moveInResult.getCalculatedPreviousReading(),
                                                          Format.SHORTDATE));

        msgData.put("user", liteYukonUser.getUsername());
        msgData.put("processingDate", Calendar.getInstance()
                                              .getTime()
                                              .toString());

        String subject = tp.process(moveInSuccessSubjectFormat, msgData);
        String body = null;
        try{
            body = tp.process(moveInSuccessEmail, msgData);
        } catch (IOException ioe) {
            logger.error("Resource template could not be opened for the move in move out email service ",ioe);
        }
        
        DefaultEmailMessage emailMessage = new DefaultEmailMessage(moveInResult.getEmailAddress(),
                                                                   subject,
                                                                   body);
        sendEmail(emailMessage);
    }

    private void createMoveInScheduleEmail(MoveInResult moveInResult,
            LiteYukonUser liteYukonUser) {
        SimpleTemplateProcessor tp = new SimpleTemplateProcessor();
        Map<String, Object> msgData = new TreeMap<String, Object>();
        msgData.put("status", "scheduled");

        msgData.put("prevMeterNumber", moveInResult.getPreviousMeter()
                                                 .getMeterNumber());
        msgData.put("prevMeterName", moveInResult.getPreviousMeter().getName());

        Date moveInDate = moveInResult.getMoveInDate();
        String moveInDateStr = dateFormattingService.formatDate(moveInDate,
                                                                DateFormatEnum.DATE,
                                                                liteYukonUser);

        msgData.put("startDate", moveInDateStr);

        String subject = tp.process(scheduledMsgSub, msgData);
        String body = null;
        try{
            body = tp.process(moveInScheduledEmail, msgData);
        } catch (IOException ioe) {
            logger.error("Resource template could not be opened for the move in move out email service ",ioe);
        }
        
        DefaultEmailMessage emailMessage = new DefaultEmailMessage(moveInResult.getEmailAddress(),
                                                                   subject,
                                                                   body);
        sendEmail(emailMessage);
    }

    private void createMoveInFailureEmail(MoveInResult moveInResult,
            LiteYukonUser liteYukonUser) {
        SimpleTemplateProcessor tp = new SimpleTemplateProcessor();
        Map<String, Object> msgData = new TreeMap<String, Object>();
        msgData.put("status", "failed");
        msgData.put("statusMsg",
                    "The system was not able to process the move in request for " + moveInResult.getPreviousMeter()
                                                                                                .getName() + " for the following reasons.");

        msgData.put("prevMeterNumber", moveInResult.getPreviousMeter()
                                                   .getMeterNumber());
        msgData.put("prevMeterName", moveInResult.getPreviousMeter().getName());

        setDatesMoveIn(moveInResult, msgData, liteYukonUser);
        buildErrorStr(moveInResult.getErrors(), msgData);

        String subject = tp.process(baseSubjectFormat, msgData);
        String body = null;
        try{
            body = tp.process(moveInFailedEmail, msgData);
        } catch (IOException ioe) {
            logger.error("Resource template could not be opened for the move in move out email service ",ioe);
        }
        
        DefaultEmailMessage emailMessage = new DefaultEmailMessage(moveInResult.getEmailAddress(),
                                                                   subject,
                                                                   body);
        sendEmail(emailMessage);
    }

    private void createMoveOutSuccessEmail(MoveOutResult moveOutResult,
            LiteYukonUser liteYukonUser) {
        SimpleTemplateProcessor tp = new SimpleTemplateProcessor();
        Map<String, Object> msgData = new HashMap<String, Object>();
        msgData.put("status", "completed");
        msgData.put("statusMsg",
                    "Move out request for " + moveOutResult.getPreviousMeter()
                                                           .getName() + " is complete.");
        msgData.put("prevMeterName", moveOutResult.getPreviousMeter().getName());

        setDatesMoveOut(moveOutResult, msgData, liteYukonUser);

        Set<String> deviceGroupList = new TreeSet<String>();
        for(DeviceGroup deviceGroup : moveOutResult.getDeviceGroupsAdded()){
           deviceGroupList.add(deviceGroup.getName()); 
        }
        msgData.put("deviceGroups", deviceGroupList);
        
        msgData.put("currentUsage",
                    pointFormattingService.getValueString(moveOutResult.getCurrentReading(),
                                                          Format.FULL,
                                                          liteYukonUser));
        msgData.put("calculatedUsage",
                    pointFormattingService.getValueString(moveOutResult.getCalculatedDifference(),
                                                          Format.SHORT));
        msgData.put("calculatedTotalUsage",
                    pointFormattingService.getValueString(moveOutResult.getCalculatedReading(),
                                                          Format.SHORTDATE));

        msgData.put("user", liteYukonUser.getUsername());
        msgData.put("processingDate", Calendar.getInstance()
                                              .getTime()
                                              .toString());

        String subject = tp.process(baseSubjectFormat, msgData);
        String body = null;
        try{
            body = tp.process(moveOutSuccessEmail, msgData);
        } catch (IOException ioe) {
            logger.error("Resource template could not be opened for the move in move out email service ",ioe);
        }
        
        DefaultEmailMessage emailMessage = new DefaultEmailMessage(moveOutResult.getEmailAddress(),
                                                                   subject,
                                                                   body);
        sendEmail(emailMessage);
    }

    private void createMoveOutScheduleEmail(MoveOutResult moveOutResult,
            LiteYukonUser liteYukonUser) {
        SimpleTemplateProcessor tp = new SimpleTemplateProcessor();
        Map<String, Object> msgData = new TreeMap<String, Object>();
        msgData.put("status", "scheduled");

        msgData.put("prevMeterNumber", moveOutResult.getPreviousMeter().getMeterNumber());
        msgData.put("prevMeterName", moveOutResult.getPreviousMeter().getName());

        Date moveOutDate = new Date(moveOutResult.getMoveOutDate().getTime() - 1);
        String moveOutDateStr = dateFormattingService.formatDate(moveOutDate,
                                                                 DateFormatEnum.DATE,
                                                                 liteYukonUser);

        msgData.put("startDate", moveOutDateStr);

        String subject = tp.process(scheduledMsgSub, msgData);
        String body = null;
        try{
            body = tp.process(moveOutScheduledEmail, msgData);
        } catch (IOException ioe) {
            logger.error("Resource template could not be opened for the move in move out email service ",ioe);
        }
        
        DefaultEmailMessage emailMessage = new DefaultEmailMessage(moveOutResult.getEmailAddress(),
                                                                   subject,
                                                                   body);
        sendEmail(emailMessage);
    }

    private void createMoveOutFailureEmail(MoveOutResult moveOutResult,
            LiteYukonUser liteYukonUser) {
        SimpleTemplateProcessor tp = new SimpleTemplateProcessor();
        Map<String, Object> msgData = new TreeMap<String, Object>();
        msgData.put("status", "failed");
        msgData.put("statusMsg",
                    "The system was not able to process the move out request for " + moveOutResult.getPreviousMeter()
                                                                                                  .getName() + " for the following reasons.");

        msgData.put("prevMeterNumber", moveOutResult.getPreviousMeter()
                                                    .getMeterNumber());
        msgData.put("prevMeterName", moveOutResult.getPreviousMeter().getName());

        setDatesMoveOut(moveOutResult, msgData, liteYukonUser);
        buildErrorStr(moveOutResult.getErrors(), msgData);

        String subject = tp.process(baseSubjectFormat, msgData);
        String body = null;
        try{
            body = tp.process(moveOutFailedEmail, msgData);
        } catch (IOException ioe) {
            logger.error("Resource template could not be opened for the move in move out email service ",ioe);
        }
        
        DefaultEmailMessage emailMessage = new DefaultEmailMessage(moveOutResult.getEmailAddress(),
                                                                   subject,
                                                                   body);
        sendEmail(emailMessage);
    }

    /**
     * @param defaultEmailMessage
     */
    private void sendEmail(DefaultEmailMessage defaultEmailMessage) {
        try {
            emailService.sendMessage(defaultEmailMessage);
        } catch (MessagingException me) {
            logger.error(me.getStackTrace());
        }
    }

    private void setDatesMoveOut(MoveOutResult moveOutResult,
            Map<String, Object> msgData, LiteYukonUser liteYukonUser) {
        Date moveOutDate = new Date(moveOutResult.getMoveOutDate().getTime() - 1);
        String moveOutDateStr = dateFormattingService.formatDate(moveOutDate,
                                                                 DateFormatEnum.DATE,
                                                                 liteYukonUser);

        Date currentReadDate = new Date();
        String currentReadDateStr = dateFormattingService.formatDate(currentReadDate,
                                                                     DateFormatEnum.BOTH,
                                                                     liteYukonUser);

        msgData.put("startDate", moveOutDateStr);
        msgData.put("stopDate", currentReadDateStr);
    }

    private void setDatesMoveIn(MoveInResult moveInResult,
            Map<String, Object> msgData, LiteYukonUser liteYukonUser) {
        Date moveInDate = moveInResult.getMoveInDate();
        String moveInDateStr = dateFormattingService.formatDate(moveInDate,
                                                                DateFormatEnum.DATE,
                                                                liteYukonUser);

        Date currentDate = new Date();
        String currentDateStr = dateFormattingService.formatDate(currentDate,
                                                                 DateFormatEnum.BOTH,
                                                                 liteYukonUser);

        msgData.put("startDate", moveInDateStr);
        msgData.put("stopDate", currentDateStr);
    }

    private void buildErrorStr(List<DeviceErrorDescription> errors,
            Map<String, Object> msgData) {
        String errorsStr = "";
        String tab = "    ";
        for (DeviceErrorDescription description : errors) {
            errorsStr += description.getDescription() + " " + description.getErrorCode() + "\r\n" + tab + description.getPorter() + " \r\n" + tab + description.getTroubleshooting() + " \r\n";
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

    @Required
    public void setDateFormattingService(
            DateFormattingService dateFormattingService) {
        this.dateFormattingService = dateFormattingService;
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

}
