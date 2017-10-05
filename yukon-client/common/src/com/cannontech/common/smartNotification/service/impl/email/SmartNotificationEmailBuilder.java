package com.cannontech.common.smartNotification.service.impl.email;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigString;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.smartNotification.model.SmartNotificationEvent;
import com.cannontech.common.smartNotification.model.SmartNotificationEventType;
import com.cannontech.common.smartNotification.model.SmartNotificationMessageParameters;
import com.cannontech.common.smartNotification.model.SmartNotificationVerbosity;
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
    @Autowired private ConfigurationSource configurationSource;
    @Autowired private GlobalSettingDao globalSettingDao;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    protected MessageSourceAccessor messageSourceAccessor;
    
    @PostConstruct
    public void init() {
        messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(YukonUserContext.system);
    }
    
    public abstract SmartNotificationEventType getSupportedType();
    
    protected abstract Object[] getBodyArguments(List<SmartNotificationEvent> events, SmartNotificationVerbosity verbosity, 
                                                 int eventPeriodMinutes);
    
    protected abstract Object[] getSubjectArguments(List<SmartNotificationEvent> events, SmartNotificationVerbosity verbosity);
    
    public EmailMessage buildEmail(SmartNotificationMessageParameters parameters) throws MessagingException {
        SmartNotificationVerbosity verbosity = parameters.getVerbosity();
        SmartNotificationEventType type = parameters.getType();
        List<SmartNotificationEvent> events = parameters.getEvents();
        String quantity = events.size() > 1 ? "multi" : "single";
        
        String emailBodyKeyBase = "yukon.web.modules.smartNotifications." + type + "." + quantity;;
        
        Object[] bodyArguments = getBodyArguments(events, verbosity, parameters.getEventPeriodMinutes());
        String emailBody = messageSourceAccessor.getMessage(emailBodyKeyBase + "." + verbosity + ".text", bodyArguments);
        
        Object[] subjectArguments = getSubjectArguments(events, verbosity);
        String emailSubject = messageSourceAccessor.getMessage("yukon.web.modules.smartNotifications." + type + "." +
                                                               quantity + ".subject", subjectArguments);
        
        return buildMessage(emailSubject, emailBody, parameters.getRecipients());
    }
    
    private EmailMessage buildMessage(String emailSubject, String emailBody, List<String> recipientStrings) 
                                          throws MessagingException {
        
        InternetAddress sender = new InternetAddress();
        sender.setAddress(globalSettingDao.getString(GlobalSettingType.MAIL_FROM_ADDRESS));
        
        InternetAddress[] recipients = recipientStrings.stream()
                                                       .map(recipient -> {
                                                           InternetAddress address = new InternetAddress();
                                                           address.setAddress(recipient);
                                                           return address;
                                                       })
                                                       .toArray(InternetAddress[]::new);
        return new EmailMessage(sender, recipients, emailSubject, emailBody);
    }
    
    protected String getUrl(String postfix) {
        String base = configurationSource.getString(MasterConfigString.YUKON_EXTERNAL_URL);
        return base + "/notifications/events/" + postfix;
    }
}
