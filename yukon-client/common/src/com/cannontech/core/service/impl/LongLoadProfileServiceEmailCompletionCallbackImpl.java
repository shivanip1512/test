package com.cannontech.core.service.impl;

import java.util.Date;
import java.util.Map;

import javax.mail.MessagingException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.cannontech.amr.errors.dao.DeviceErrorTranslatorDao;
import com.cannontech.amr.errors.model.DeviceErrorDescription;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.SimpleTemplateProcessor;
import com.cannontech.common.util.TemplateProcessor;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.LongLoadProfileService;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.tools.email.DefaultEmailMessage;
import com.cannontech.tools.email.EmailService;


public class LongLoadProfileServiceEmailCompletionCallbackImpl implements LongLoadProfileService.CompletionCallback {
        
    private Logger log = YukonLogManager.getLogger(LongLoadProfileServiceEmailCompletionCallbackImpl.class);
    private EmailService emailService = null;
    private DateFormattingService dateFormattingService = null;
    private DeviceErrorTranslatorDao deviceErrorTranslatorDao = null;
    private DefaultEmailMessage successMessage = new DefaultEmailMessage();
    private DefaultEmailMessage failureMessage = new DefaultEmailMessage();
    private DefaultEmailMessage cancelMessage = new DefaultEmailMessage();
    
    private TemplateProcessor tp = new SimpleTemplateProcessor();
    
    private final String baseSubjectFormat = "Profile data collection for {formattedDeviceName} from {startDate} - {stopDate} {status}";

    private final String lpNotificationFormat_Success = "{statusMsg}\n\n" + "Device Summary\n" + "Device Name: {deviceName}    \n" + "Meter Number: {meterNumber}    \n" + "Physical Address: {physAddress}\n\n" + "Request Range: {startDate} to {stopDate}    \n" + "Total Requested Days: {totalDays}\n\n" + "Data is now available online.";
    private final String lpNotificationFormat_Failure = "{statusMsg}\n\n" + "Device Summary\n" + "Device Name: {deviceName}    \n" + "Meter Number: {meterNumber}    \n" + "Physical Address: {physAddress}\n\n" + "Request Range: {startDate} to {stopDate}    \n" + "Total Requested Days: {totalDays}";
    private final String lpNotificationFormat_Cancel = "{statusMsg}\n\n" + "Device Summary\n" + "Device Name: {deviceName}    \n" + "Meter Number: {meterNumber}    \n" + "Physical Address: {physAddress}\n\n" + "Request Range: {startDate} to {stopDate}    \n" + "Total Requested Days: {totalDays}";

    private DefaultEmailMessage getEmailer(String email, String subject, String body){
        
        DefaultEmailMessage emailer = new DefaultEmailMessage();
        emailer.setRecipient(email);
        emailer.setSubject(subject);
        emailer.setBody(body);
        
        return emailer;
    }
    
    public LongLoadProfileServiceEmailCompletionCallbackImpl(
            EmailService emailService, DateFormattingService dateFormattingeService, DeviceErrorTranslatorDao deviceErrorTranslatorDao) {
        this.emailService = emailService;
        this.dateFormattingService = dateFormattingeService;
        this.deviceErrorTranslatorDao = deviceErrorTranslatorDao;
    }
    
    public void setSuccessMessage(Map<String, Object> msgData) {
        
        msgData.put("status", "completed");
        msgData.put("statusMsg", "Your profile data collection request has completed.");

        String subject = tp.process(baseSubjectFormat, msgData);
        String body = tp.process(lpNotificationFormat_Success, msgData);
        
        this.successMessage = getEmailer((String)msgData.get("email"), subject, body);
    }
    
    public void setFailureMessage(Map<String, Object> msgData) {
        
        msgData.put("status", "failed");
        msgData.put("statusMsg", "Your profile data collection request has encountered an error.");

        String subject = tp.process(baseSubjectFormat, msgData);
        String body = tp.process(lpNotificationFormat_Failure, msgData);
        
        this.failureMessage = getEmailer((String)msgData.get("email"), subject, body);
        
    }
    
    public void setCancelMessage(Map<String, Object> msgData) {
        
        msgData.put("status", "canceled");
        msgData.put("statusMsg", "Your profile data collection was canceled.");

        String subject = tp.process(baseSubjectFormat, msgData);
        String body = tp.process(lpNotificationFormat_Cancel, msgData);
        
        this.cancelMessage = getEmailer((String)msgData.get("email"), subject, body);
    }

    //  onFailure
    public void onFailure(int returnStatus, String resultString) {
        try {
           
            String errorReason = "Error Reason:\n";
            
            boolean haveSpecificReason = false;
            if(returnStatus > 0){
                
                DeviceErrorDescription deviceErrorDescription = deviceErrorTranslatorDao.translateErrorCode(returnStatus);
                if(deviceErrorDescription != null){
                    
                    haveSpecificReason = true;
                    String porter = deviceErrorDescription.getPorter();
                    String description = deviceErrorDescription.getDescription();
                    
                    errorReason += porter + ": " + description;
                }
            }
            
            if(!haveSpecificReason){
                errorReason +=resultString;
            }
            
            
            String originalBody = failureMessage.getBody();
            String newBody = originalBody + "\n\n" + errorReason;
            failureMessage.setBody(newBody);
            
            emailService.sendMessage(failureMessage);
        } catch (MessagingException e) {
            log.error("Unable to send onFailure message",e);
        }
    }

    // onSuccess
    public void onSuccess(String successInfo) {
        try {
            String originalBody = successMessage.getBody();
            String newBody = originalBody + "\n\n" + successInfo;
            successMessage.setBody(newBody);
            
            emailService.sendMessage(successMessage);
        } catch (MessagingException e) {
            log.error("Unable to send onSuccess message",e);
        }
    }
    
    //  onCancel
    public void onCancel(LiteYukonUser cancelUser) {
        try {
            
            String cancelInfo = "";
            String cancelUserName = cancelUser.getUsername();
            int cancelUserID = cancelUser.getUserID();
            if(cancelUser != null){
                Date now = new Date();
                cancelInfo = "Canceled by " + cancelUserName + " (" + cancelUserID + ")" 
                            + " on " + dateFormattingService.formatDate(now, DateFormattingService.DateFormatEnum.DATE, cancelUser) 
                            + " at " + dateFormattingService.formatDate(now, DateFormattingService.DateFormatEnum.TIME, cancelUser) + ".";
            }
            
            String originalBody = cancelMessage.getBody();
            String newBody = originalBody + "\n\n" + cancelInfo;
            cancelMessage.setBody(newBody);
            
            emailService.sendMessage(cancelMessage);
            
        } catch (MessagingException e) {
            log.error("Unable to send onCancel message",e);
        }
    }
    
    public String toString(){
        return successMessage.getRecipient();
    }

    public DateFormattingService getDateFormattingService() {
        return dateFormattingService;
    }

    @Required
    public void setDateFormattingService(DateFormattingService dateFormattingService) {
        this.dateFormattingService = dateFormattingService;
    }

    

}
    
    
    
    