package com.cannontech.core.service.impl;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;

import com.cannontech.amr.errors.dao.DeviceErrorTranslatorDao;
import com.cannontech.amr.errors.model.DeviceErrorDescription;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.FormattingTemplateProcessor;
import com.cannontech.common.util.TemplateProcessorFactory;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.LoadProfileService;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.tools.email.EmailHtmlMessage;
import com.cannontech.tools.email.EmailService;
import com.cannontech.user.YukonUserContext;


public class LoadProfileServiceEmailCompletionCallbackImpl implements LoadProfileService.CompletionCallback {
        
    private Logger log = YukonLogManager.getLogger(LoadProfileServiceEmailCompletionCallbackImpl.class);
    private EmailService emailService = null;
    private DateFormattingService dateFormattingService = null;
    private TemplateProcessorFactory templateProcessorFactory = null;
    private DeviceErrorTranslatorDao deviceErrorTranslatorDao = null;
    private Map<String, Object> msgData;
    private String email;
    private YukonUserContext userContext;
    
    private final String baseSubjectFormat = "Profile data collection for {formattedDeviceName} from {startDate|DATE} - {stopDate|DATE} {status}";

    private String base_template_plain = "{statusMsg}\n\n" + "Device Summary\n" + "Device Name: {deviceName}\n" + "Meter Number: {meterNumber}\n" + "Address/Serial Number: {physAddress/serialNumber}\n\n" + "Channel: {channelName}\n" +  "Request Range: {startDate|DATE} to {stopDate|DATE}\n" + "Total Requested Days: {totalDays} \n\n";
    private String base_template_html  = "{statusMsg}<br/><br/>" + "Device Summary<br/>" + "Device Name: {deviceName}<br/>" + "Meter Number: {meterNumber}<br/>" + "Address/Serial Number: {physAddress/serialNumber}<br/><br/>" + "Channel: {channelName}<br/>" +  "Request Range: {startDate|DATE} to {stopDate|DATE}<br/>" + "Total Requested Days: {totalDays}<br/><br/>";
    
    private final String success_template_plain = base_template_plain + "Data is now available online.\n\nHTML\n{reportHtmlUrl}\n\nCSV\n{reportCsvUrl}\n\nPDF\n{reportPdfUrl}\n\n";
    private final String failure_template_plain = base_template_plain + "Partial data may be available online.\n\nHTML\n{reportHtmlUrl}\n\nCSV\n{reportCsvUrl}\n\nPDF\n{reportPdfUrl}\n\n";
    private final String cancel_template_plain  = base_template_plain + "Partial data may be available online.\n\nHTML\n{reportHtmlUrl}\n\nCSV\n{reportCsvUrl}\n\nPDF\n{reportPdfUrl}\n\n";
    
    private final String success_template_html = base_template_html + "Data is now available online.<br/>View Report: <a href=\"{reportHtmlUrl}\">HTML</a> | <a href=\"{reportCsvUrl}\">CSV</a> | <a href=\"{reportPdfUrl}\">PDF</a><br/><br/>";
    private final String failure_template_html = base_template_html + "Partial data may be available online.<br/>View Report: <a href=\"{reportHtmlUrl}\">HTML</a> | <a href=\"{reportCsvUrl}\">CSV</a> | <a href=\"{reportPdfUrl}\">PDF</a><br/><br/>";
    private final String cancel_template_html  = base_template_html + "Partial data may be available online.<br/>View Report: <a href=\"{reportHtmlUrl}\">HTML</a> | <a href=\"{reportCsvUrl}\">CSV</a> | <a href=\"{reportPdfUrl}\">PDF</a><br/><br/>";
    
    private EmailHtmlMessage getEmailer(String bodyTemplate, String htmlBodyTemplate, Map<String, Object> extraData) throws AddressException, MessagingException {
        Map<String, Object> data = new HashMap<String, Object>(msgData);
        data.putAll(extraData);
        
        FormattingTemplateProcessor tp = templateProcessorFactory.getFormattingTemplateProcessor(userContext);
        String subject = tp.process(baseSubjectFormat, data);
        String body = tp.process(bodyTemplate, data);
        String htmlBody = tp.process(htmlBodyTemplate, data);

        EmailHtmlMessage emailer = new EmailHtmlMessage(InternetAddress.parse(email),
                                                                      subject, body, htmlBody);
        return emailer;
    }
    
    public LoadProfileServiceEmailCompletionCallbackImpl(
            EmailService emailService, DateFormattingService dateFormattingeService, TemplateProcessorFactory templateProcessorFactory,DeviceErrorTranslatorDao deviceErrorTranslatorDao) {
        this.emailService = emailService;
        this.dateFormattingService = dateFormattingeService;
        this.templateProcessorFactory = templateProcessorFactory;
        this.deviceErrorTranslatorDao = deviceErrorTranslatorDao;
    }
    
    //  onFailure
    public String onFailure(int returnStatus, String resultString) {
        String errorReason = "";
        String errorHtmlReason = "";
        String description = null;
        try {
            boolean haveSpecificReason = false;
            final String errorReasonStr = "Error Reason (" + returnStatus + "):";
            
            if (returnStatus > 0) {
                
                DeviceErrorDescription deviceErrorDescription = deviceErrorTranslatorDao.translateErrorCode(returnStatus);
                if (deviceErrorDescription != null) {
                    
                    haveSpecificReason = true;
                    // Try to use the actual resultString if not empty, otherwise default to deviceErrorTranslator.porter message
                    String porter = StringUtils.defaultIfEmpty(resultString, deviceErrorDescription.getPorter());
                    description = deviceErrorDescription.getDescription();

                    errorReason += errorReasonStr + "\n" + porter + ": " + description;
                    errorHtmlReason += errorReasonStr + "<br/>" + porter + ": " + description;
                }
            }
            
            if (!haveSpecificReason && !StringUtils.isBlank(resultString)){
                errorReason += errorReasonStr + "\n" + resultString;
                errorHtmlReason += errorReasonStr + "<br/>" + resultString;
            }
            
            Map<String, Object> extraData = new HashMap<String, Object>();
            extraData.put("status", "failed");
            extraData.put("statusMsg", "Your profile data collection request has encountered an error.");
            
            EmailHtmlMessage failureMessage = getEmailer(failure_template_plain, failure_template_html, extraData);
            if (!StringUtils.isBlank(errorReason)) {
                String originalBody = failureMessage .getBody();
                String newBody = originalBody + "\n\n" + errorReason;
                failureMessage.setBody(newBody);
                String originalHtmlBody = failureMessage.getHtmlBody();
                String newHtmlBody = originalHtmlBody + "<br/><br/>" + errorHtmlReason;
                failureMessage.setHtmlBody(newHtmlBody);
            }
            
            emailService.sendMessage(failureMessage);
            
        } catch (MessagingException e) {
            log.error("Unable to send onFailure message",e);
        }
        return description;
    }

    // onSuccess
    public void onSuccess(String successInfo) {
        try {
            
            Map<String, Object> extraData = new HashMap<String, Object>();
            extraData.put("status", "completed");
            extraData.put("statusMsg", "Your profile data collection request has completed.");
            
            EmailHtmlMessage successMessage = getEmailer(success_template_plain, success_template_html, extraData);
            if (!StringUtils.isBlank(successInfo)) {
                String originalBody = successMessage .getBody();
                String newBody = originalBody + "\n\n" + successInfo;
                successMessage.setBody(newBody);
                
                String originalHtmlBody = successMessage.getHtmlBody();
                String newHtmlBody = originalHtmlBody + "<br/><br/>" + successInfo;
                successMessage.setHtmlBody(newHtmlBody);
            }
            
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
            if (cancelUser != null) {
                Date now = new Date();
                cancelInfo = "Canceled by " + cancelUserName + " (" + cancelUserID + ")" 
                            + " on " + dateFormattingService.format(now, DateFormattingService.DateFormatEnum.DATE, userContext) 
                            + " at " + dateFormattingService.format(now, DateFormattingService.DateFormatEnum.TIME, userContext) + ".";
            }
            
            Map<String, Object> extraData = new HashMap<String, Object>();
            extraData.put("status", "canceled");
            extraData.put("statusMsg", "Your profile data collection was canceled.");
            
            EmailHtmlMessage cancelMessage = getEmailer(cancel_template_plain, cancel_template_html, extraData);
            if (!StringUtils.isBlank(cancelInfo)) {
                String originalBody = cancelMessage .getBody();
                String newBody = originalBody + "\n\n" + cancelInfo;
                cancelMessage.setBody(newBody);
                
                String originalHtmlBody = cancelMessage.getHtmlBody();
                String newHtmlBody = originalHtmlBody + "<br/><br/>" + cancelInfo;
                cancelMessage.setHtmlBody(newHtmlBody);
            }
            
            emailService.sendMessage(cancelMessage);
            
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

}
    
    
    
    