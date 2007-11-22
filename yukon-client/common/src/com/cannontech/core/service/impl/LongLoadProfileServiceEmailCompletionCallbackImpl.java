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

    private String base_template_plain = "{statusMsg}\n\n" + "Device Summary\n" + "Device Name: {deviceName}\n" + "Meter Number: {meterNumber}\n" + "Physical Address: {physAddress}\n\n" + "Request Range: {startDate} to {stopDate}\n" + "Total Requested Days: {totalDays} \n\n";
    private String base_template_html  = "{statusMsg}<br/><br/>" + "Device Summary<br/>" + "Device Name: {deviceName}<br/>" + "Meter Number: {meterNumber}<br/>" + "Physical Address: {physAddress}<br/><br/>" + "Request Range: {startDate} to {stopDate}<br/>" + "Total Requested Days: {totalDays}<br/><br/>";
    
    private final String success_template_plain = base_template_plain + "Data is now available online.\n\nHTML\n{reportHtmlUrl}\n\nCSV\n{reportCsvUrl}\n\nPDF\n{reportPdfUrl}\n\n";
    private final String failure_template_plain = base_template_plain;
    private final String cancel_template_plain  = base_template_plain + "Partial data may be available online.\n\nHTML\n{reportHtmlUrl}\n\nCSV\n{reportCsvUrl}\n\nPDF\n{reportPdfUrl}\n\n";
    
    private final String success_template_html = base_template_html + "Data is now available online.<br/>View Report: <a href=\"{reportHtmlUrl}\">HTML</a> | <a href=\"{reportCsvUrl}\">CSV</a> | <a href=\"{reportPdfUrl}\">PDF</a><br/><br/>";
    private final String failure_template_html = base_template_html;
    private final String cancel_template_html  = base_template_html + "Partial data may be available online.<br/>View Report: <a href=\"{reportHtmlUrl}\">HTML</a> | <a href=\"{reportCsvUrl}\">CSV</a> | <a href=\"{reportPdfUrl}\">PDF</a><br/><br/>";

    private DefaultEmailMessage getEmailer(String email, String subject, String body, String htmlBody){
        
        DefaultEmailMessage emailer = new DefaultEmailMessage();
        emailer.setRecipient(email);
        emailer.setSubject(subject);
        emailer.setBody(body);
        emailer.setHtmlBody(htmlBody);
        
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
        String body = tp.process(success_template_plain, msgData);
        String htmlBody = tp.process(success_template_html, msgData);
        
        this.successMessage = getEmailer((String)msgData.get("email"), subject, body, htmlBody);
    }
    
    public void setFailureMessage(Map<String, Object> msgData) {
        
        msgData.put("status", "failed");
        msgData.put("statusMsg", "Your profile data collection request has encountered an error.");

        String subject = tp.process(baseSubjectFormat, msgData);
        String body = tp.process(failure_template_plain, msgData);
        String htmlBody = tp.process(failure_template_html, msgData);
        
        this.failureMessage = getEmailer((String)msgData.get("email"), subject, body, htmlBody);
        
    }
    
    public void setCancelMessage(Map<String, Object> msgData) {
        
        msgData.put("status", "canceled");
        msgData.put("statusMsg", "Your profile data collection was canceled.");

        String subject = tp.process(baseSubjectFormat, msgData);
        String body = tp.process(cancel_template_plain, msgData);
        String htmlBody = tp.process(cancel_template_html, msgData);
        
        this.cancelMessage = getEmailer((String)msgData.get("email"), subject, body, htmlBody);
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
            
            emailService.sendHTMLMessage(cancelMessage);
            
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
    
    
    
    