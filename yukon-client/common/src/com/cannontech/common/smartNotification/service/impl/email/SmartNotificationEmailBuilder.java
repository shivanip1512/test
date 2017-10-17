package com.cannontech.common.smartNotification.service.impl.email;

import java.util.List;
import java.util.Objects;

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
import com.cannontech.common.smartNotification.model.SmartNotificationMessageParametersMulti;
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
        String emailBody = messageSourceAccessor.getMessage(emailBodyKeyBase + "." + verbosity + ".text", bodyArguments);
        
        Object[] subjectArguments = getSubjectArguments(events, verbosity);
        String emailSubject = messageSourceAccessor.getMessage("yukon.web.modules.smartNotifications." + type + "." +
                                                               quantity + ".subject", subjectArguments);
        
        return buildMessage(emailSubject, emailBody, parameters.getRecipients());
    }
    
    /**
     * Builds a complete combined digest email notification, based on a parameters object for each subscription being
     * combined.
     * @throws MessagingException if an error occurs while building the email message.
     */
    public EmailMessage buildEmail(SmartNotificationMessageParametersMulti parametersMulti) throws MessagingException {

        int intervalMinutes = parametersMulti.getIntervalMinutes();
        String body = parametersMulti.getMessageParameters()
                                     .stream()
                                     .map(parameters -> {
                                         try {
                                             return buildEmail(parameters, intervalMinutes);
                                         } catch (MessagingException e) {
                                             return null;
                                         }
                                     })
                                     .filter(Objects::nonNull)
                                     .map(message -> message.getSubject() + "/n" + message.getBody())
                                     .reduce("", (s1, s2) -> s1 + s2);
        
        int totalEvents = parametersMulti.getTotalEvents();
        String subject = messageSourceAccessor.getMessage("yukon.web.modules.smartNotifications.combinedDigest.subject", totalEvents);
        
        List<String> recipients = parametersMulti.getMessageParameters().get(0).getRecipients();
        return buildMessage(subject, body, recipients);
    }
    
    /**
     * Builds a complete email notification message.
     * @throws MessagingException if an error occurs while building the EmailMessage.
     */
    private EmailMessage buildMessage(String emailSubject, String emailBody, List<String> recipientEmailAddresses) 
                                          throws MessagingException {
        
        InternetAddress sender = new InternetAddress();
        sender.setAddress(globalSettingDao.getString(GlobalSettingType.MAIL_FROM_ADDRESS));
        
        InternetAddress[] recipients = recipientEmailAddresses.stream()
                                                              .map(recipient -> {
                                                                  InternetAddress address = new InternetAddress();
                                                                  address.setAddress(recipient);
                                                                  return address;
                                                              })
                                                              .toArray(InternetAddress[]::new);
        return new EmailMessage(sender, recipients, emailSubject, emailBody);
    }
    
    /**
     * Get the appropriate URL for the relevent Yukon smart notification events page. The postfix is appended to the
     * base smart notification events URL.
     */
    protected String getUrl(String postfix) {
        String base = configurationSource.getString(MasterConfigString.YUKON_EXTERNAL_URL);
        return base + "/notifications/events/" + postfix;
    }
}
