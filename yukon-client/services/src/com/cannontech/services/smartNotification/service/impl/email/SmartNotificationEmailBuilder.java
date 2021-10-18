package com.cannontech.services.smartNotification.service.impl.email;

import java.util.List;

import javax.annotation.PostConstruct;
import jakarta.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.smartNotification.dao.SmartNotificationEventDao;
import com.cannontech.common.smartNotification.model.SmartNotificationEvent;
import com.cannontech.common.smartNotification.model.SmartNotificationEventType;
import com.cannontech.common.smartNotification.model.SmartNotificationMessageParameters;
import com.cannontech.common.smartNotification.model.SmartNotificationVerbosity;
import com.cannontech.common.util.WebserverUrlResolver;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.cannontech.tools.email.EmailMessage;
import com.cannontech.user.YukonUserContext;

/**
 * Builds notification messages based on a set of i18n keys, and depending on the notification parameters. Child classes
 * specify the arguments to be passed into the i18ned notification message.
 */
public abstract class SmartNotificationEmailBuilder {
    @Autowired private GlobalSettingDao globalSettingDao;
    @Autowired private WebserverUrlResolver webserverUrlResolver;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    @Autowired private SmartNotificationEventDao smartNotificationEventDao;
    protected MessageSourceAccessor messageSourceAccessor;
    
    @PostConstruct
    public void init() {
        messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(YukonUserContext.system);
    }
    
    /**
     * Get the notification type that the builder can handle.
     */
    public abstract SmartNotificationEventType getSupportedType();
    
    /**
     * Get argument objects to be used with the i18n key for the message body.
     */
    protected abstract Object[] getBodyArguments(List<SmartNotificationEvent> events, SmartNotificationVerbosity verbosity, 
                                                 int eventPeriodMinutes);
    
    /**
     * Get argument objects to be used with the i18n key for the message subject line.
     */
    protected abstract Object[] getSubjectArguments(List<SmartNotificationEvent> events, SmartNotificationVerbosity verbosity);
    
    /**
     * Builds a complete email notification message based on the specified parameters.
     * @throws MessagingException if an error occurs while building the email message.
     */
    public EmailMessage buildEmail(SmartNotificationMessageParameters parameters, int intervalMinutes) throws MessagingException {
        SmartNotificationVerbosity verbosity = parameters.getVerbosity();
        SmartNotificationEventType type = parameters.getType();
        List<SmartNotificationEvent> events = parameters.getEvents();
        String quantity = events.size() > 1 ? "multi" : "single";
        
        String emailBodyKeyBase = "yukon.web.modules.smartNotifications." + type + "." + quantity;;
        
        Object[] bodyArguments = getBodyArguments(events, verbosity, intervalMinutes);
        String bodyKey = emailBodyKeyBase + "." + verbosity + ".text";
        String emailBody = messageSourceAccessor.getMessage(bodyKey, bodyArguments);
        
        Object[] subjectArguments = getSubjectArguments(events, verbosity);
        String emailSubject = messageSourceAccessor.getMessage("yukon.web.modules.smartNotifications." + type + "." +
                                                               quantity + ".subject", subjectArguments);
        
        String sender = globalSettingDao.getString(GlobalSettingType.MAIL_FROM_ADDRESS);
        smartNotificationEventDao.createHistory(parameters, intervalMinutes);
        return EmailMessage.newMessageBccOnly(emailSubject, emailBody, sender, parameters.getRecipients());
    }
    
    /**
     * Get the appropriate URL for the relevent Yukon smart notification events page. The postfix is appended to the
     * base smart notification events URL.
     */
    protected String getUrl(String postfix) {
        return webserverUrlResolver.getUrl("/notifications/events/" + postfix);
    }
}
