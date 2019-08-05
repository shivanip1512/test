package com.cannontech.services.smartNotification.service.impl.email;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.mail.MessagingException;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.smartNotification.model.SmartNotificationEventType;
import com.cannontech.common.smartNotification.model.SmartNotificationMedia;
import com.cannontech.common.smartNotification.model.SmartNotificationMessageParameters;
import com.cannontech.common.smartNotification.model.SmartNotificationMessageParametersMulti;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.services.smartNotification.service.SmartNotificationMessageParametersHandler;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.cannontech.tools.email.EmailMessage;
import com.cannontech.user.YukonUserContext;
import com.cannontech.yukon.INotifConnection;
import com.google.common.collect.ImmutableMap;

/**
 * Handler for emails. Builds an email and sends it via the notification service.
 */
public class SmartNotificationEmailMessageParametersHandler implements SmartNotificationMessageParametersHandler {
    private static final Logger log = YukonLogManager.getLogger(SmartNotificationEmailMessageParametersHandler.class);
    private Map<SmartNotificationEventType, SmartNotificationEmailBuilder> emailBuilders;
    private INotifConnection notifClientConnection;
    private MessageSourceAccessor messageSourceAccessor;
    private GlobalSettingDao globalSettingDao;
    
    @Autowired
    public SmartNotificationEmailMessageParametersHandler(List<SmartNotificationEmailBuilder> emailBuilderList,
                                                          INotifConnection notifClientConnection,
                                                          YukonUserContextMessageSourceResolver messageSourceResolver,
                                                          GlobalSettingDao globalSettingDao) {
        // Map of notification types to their email builders
        ImmutableMap.Builder<SmartNotificationEventType, SmartNotificationEmailBuilder> mapBuilder = ImmutableMap.builder();
        for (SmartNotificationEmailBuilder emailBuilder : emailBuilderList) {
            mapBuilder.put(emailBuilder.getSupportedType(), emailBuilder);
        }
        emailBuilders = mapBuilder.build();
        
        messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(YukonUserContext.system);
        this.notifClientConnection = notifClientConnection;
        this.globalSettingDao = globalSettingDao;
    }
    
    @Override
    public SmartNotificationMedia getSupportedMedia() {
        return SmartNotificationMedia.EMAIL;
    }
    
    @Override
    public void buildAndSend(SmartNotificationMessageParameters parameters, int intervalMinutes) {
        try {
            EmailMessage message = emailBuilders.get(parameters.getType())
                                                .buildEmail(parameters, intervalMinutes);
            notifClientConnection.sendEmail(message);
        } catch (Exception e) {
            log.debug(parameters);
            log.error("Unable to send Smart Notification email.", e);
        }
    }
    
    @Override
    public void buildMultiAndSend(SmartNotificationMessageParametersMulti parametersMulti) {
        try {
            int intervalMinutes = parametersMulti.getIntervalMinutes();
            String body = parametersMulti.getMessageParameters()
                                         .stream()
                                         .map(parameters -> {
                                             try {
                                                 return emailBuilders.get(parameters.getType())
                                                                     .buildEmail(parameters, intervalMinutes);
                                             } catch (MessagingException e) {
                                                 log.error("Error building message for notification parameters: " 
                                                           + parameters, e);
                                                 return null;
                                             }
                                         })
                                         .filter(Objects::nonNull)
                                         .map(message -> message.getBody() + "\n")
                                         .reduce("", (s1, s2) -> s1 + s2);
            
            int totalEvents = parametersMulti.getTotalEvents();
            String subject = messageSourceAccessor.getMessage("yukon.web.modules.smartNotifications.combinedDigest.subject", totalEvents);
            
            List<String> recipients = parametersMulti.getMessageParameters().get(0).getRecipients();
            
            String sender = globalSettingDao.getString(GlobalSettingType.MAIL_FROM_ADDRESS);
            EmailMessage message = EmailMessage.newMessageBccOnly(subject, body, sender, recipients);

            notifClientConnection.sendEmail(message);
        } catch (Exception e) {
            log.debug(parametersMulti);
            log.error("Unable to send Smart Notification email.", e);
        }
    }
}
