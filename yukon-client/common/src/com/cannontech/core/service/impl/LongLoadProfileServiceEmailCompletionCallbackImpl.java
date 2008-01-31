package com.cannontech.core.service.impl;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.mail.MessagingException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.errors.dao.DeviceErrorTranslatorDao;
import com.cannontech.amr.errors.model.DeviceErrorDescription;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.FormattingTemplateProcessor;
import com.cannontech.common.util.TemplateProcessorFactory;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.LongLoadProfileService;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.tools.email.DefaultEmailMessage;
import com.cannontech.tools.email.EmailService;
import com.cannontech.user.YukonUserContext;


public class LongLoadProfileServiceEmailCompletionCallbackImpl implements LongLoadProfileService.CompletionCallback {
        
    private Logger log = YukonLogManager.getLogger(LongLoadProfileServiceEmailCompletionCallbackImpl.class);
    private EmailService emailService = null;
    private DateFormattingService dateFormattingService = null;
    private TemplateProcessorFactory templateProcessorFactory = null;
    private DeviceErrorTranslatorDao deviceErrorTranslatorDao = null;
    private Map<String, Object> msgData;
    private String email;
    private YukonUserContext userContext;
    
    private final String baseSubjectFormat = "Profile data collection for {formattedDeviceName} from {startDate} - {stopDate} {status}";

    private String base_template_plain = "{statusMsg}\n\n" + "Device Summary\n" + "Device Name: {deviceName}\n" + "Meter Number: {meterNumber}\n" + "Physical Address: {physAddress}\n\n" + "Request Range: {startDate} to {stopDate}\n" + "Total Requested Days: {totalDays} \n\n";
    private String base_template_html  = "{statusMsg}<br/><br/>" + "Device Summary<br/>" + "Device Name: {deviceName}<br/>" + "Meter Number: {meterNumber}<br/>" + "Physical Address: {physAddress}<br/><br/>" + "Request Range: {startDate} to {stopDate}<br/>" + "Total Requested Days: {totalDays}<br/><br/>";
    
    private final String success_template_plain = base_template_plain + "Data is now available online.\n\nHTML\n{reportHtmlUrl}\n\nCSV\n{reportCsvUrl}\n\nPDF\n{reportPdfUrl}\n\n";
    private final String failure_template_plain = base_template_plain + "Partial data may be available online.\n\nHTML\n{reportHtmlUrl}\n\nCSV\n{reportCsvUrl}\n\nPDF\n{reportPdfUrl}\n\n";
    private final String cancel_template_plain  = base_template_plain + "Partial data may be available online.\n\nHTML\n{reportHtmlUrl}\n\nCSV\n{reportCsvUrl}\n\nPDF\n{reportPdfUrl}\n\n";
    
    private final String success_template_html = base_template_html + "Data is now available online.<br/>View Report: <a href=\"{reportHtmlUrl}\">HTML</a> | <a href=\"{reportCsvUrl}\">CSV</a> | <a href=\"{reportPdfUrl}\">PDF</a><br/><br/>";
    private final String failure_template_html = base_template_html + "Partial data may be available online.<br/>View Report: <a href=\"{reportHtmlUrl}\">HTML</a> | <a href=\"{reportCsvUrl}\">CSV</a> | <a href=\"{reportPdfUrl}\">PDF</a><br/><br/>";
    private final String cancel_template_html  = base_template_html + "Partial data may be available online.<br/>View Report: <a href=\"{reportHtmlUrl}\">HTML</a> | <a href=\"{reportCsvUrl}\">CSV</a> | <a href=\"{reportPdfUrl}\">PDF</a><br/><br/>";
    
    private DefaultEmailMessage getEmailer(String bodyTemplate, String htmlBodyTemplate, Map<String, Object> extraData) {
        Map<String, Object> data = new HashMap<String, Object>(msgData);
        data.putAll(extraData);
        
        FormattingTemplateProcessor tp = templateProcessorFactory.getFormattingTemplateProcessor(userContext);
        String subject = tp.process(baseSubjectFormat, data);
        String body = tp.process(bodyTemplate, data);
        String htmlBody = tp.process(htmlBodyTemplate, data);

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
    
    //  onFailure
    public void onFailure(int returnStatus, String resultString) {
        try {
           
            String errorReason = "";
            String errorHtmlReason = "";
            
            boolean haveSpecificReason = false;
            if (returnStatus > 0) {
                
                DeviceErrorDescription deviceErrorDescription = deviceErrorTranslatorDao.translateErrorCode(returnStatus);
                if (deviceErrorDescription != null) {
                    
                    haveSpecificReason = true;
                    String porter = deviceErrorDescription.getPorter();
                    String description = deviceErrorDescription.getDescription();
                    
                    errorReason += "Error Reason:\n" + porter + ": " + description;
                    errorHtmlReason += "Error Reason:<br/>" + porter + ": " + description;
                }
            }
            
            if (!haveSpecificReason && !StringUtils.isBlank(resultString)){
                errorReason += "Error Reason:\n" + resultString;
                errorHtmlReason += "Error Reason:<br/>" + resultString;
            }
            
            Map<String, Object> extraData = new HashMap<String, Object>();
            extraData.put("status", "failed");
            extraData.put("statusMsg", "Your profile data collection request has encountered an error.");
            
            DefaultEmailMessage failureMessage = getEmailer(failure_template_plain, failure_template_html, extraData);
            if (!StringUtils.isBlank(errorReason)) {
                String originalBody = failureMessage .getBody();
                String newBody = originalBody + "\n\n" + errorReason;
                failureMessage.setBody(newBody);

                String originalHtmlBody = failureMessage.getHtmlBody();
                String newHtmlBody = originalHtmlBody + "<br/><br/>" + errorHtmlReason;
                failureMessage.setHtmlBody(newHtmlBody);
            }
            
            emailService.sendHTMLMessage(failureMessage);
            
        } catch (MessagingException e) {
            log.error("Unable to send onFailure message",e);
        }
    }

    // onSuccess
    public void onSuccess(String successInfo) {
        try {
            
            Map<String, Object> extraData = new HashMap<String, Object>();
            extraData.put("status", "completed");
            extraData.put("statusMsg", "Your profile data collection request has completed.");
            
            DefaultEmailMessage successMessage = getEmailer(success_template_plain, success_template_html, extraData);
            if (!StringUtils.isBlank(successInfo)) {
                String originalBody = successMessage .getBody();
                String newBody = originalBody + "\n\n" + successInfo;
                successMessage.setBody(newBody);
                
                String originalHtmlBody = successMessage.getHtmlBody();
                String newHtmlBody = originalHtmlBody + "<br/><br/>" + successInfo;
                successMessage.setHtmlBody(newHtmlBody);
            }
            
            emailService.sendHTMLMessage(successMessage);
            
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
            if (cancelUser != null) {
                Date now = new Date();
                cancelInfo = "Canceled by " + cancelUserName + " (" + cancelUserID + ")" 
                            + " on " + dateFormattingService.formatDate(now, DateFormattingService.DateFormatEnum.DATE, userContext) 
                            + " at " + dateFormattingService.formatDate(now, DateFormattingService.DateFormatEnum.TIME, userContext) + ".";
            }
            
            Map<String, Object> extraData = new HashMap<String, Object>();
            extraData.put("status", "canceled");
            extraData.put("statusMsg", "Your profile data collection was canceled.");
            
            DefaultEmailMessage cancelMessage = getEmailer(cancel_template_plain, cancel_template_html, extraData);
            if (!StringUtils.isBlank(cancelInfo)) {
                String originalBody = cancelMessage .getBody();
                String newBody = originalBody + "\n\n" + cancelInfo;
                cancelMessage.setBody(newBody);
                
                String originalHtmlBody = cancelMessage.getHtmlBody();
                String newHtmlBody = originalHtmlBody + "<br/><br/>" + cancelInfo;
                cancelMessage.setHtmlBody(newHtmlBody);
            }
            
            emailService.sendHTMLMessage(cancelMessage);
            
        } catch (MessagingException e) {
            log.error("Unable to send onCancel message",e);
        }
    }
    
    public String toString(){
        return email;
    }

    public void setMessageData(Map<String, Object> msgData) {
        this.msgData = Collections.unmodifiableMap(msgData);
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUserContext(YukonUserContext userContext) {
        this.userContext = userContext;
    }

    @Autowired
    public void setTemplateProcessorFactory(
            TemplateProcessorFactory templateProcessorFactory) {
        this.templateProcessorFactory = templateProcessorFactory;
    }

}
    
    
    
    