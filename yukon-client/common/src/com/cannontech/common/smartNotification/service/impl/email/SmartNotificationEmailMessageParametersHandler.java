package com.cannontech.common.smartNotification.service.impl.email;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.smartNotification.model.SmartNotificationEventType;
import com.cannontech.common.smartNotification.model.SmartNotificationMedia;
import com.cannontech.common.smartNotification.model.SmartNotificationMessageParameters;
import com.cannontech.common.smartNotification.model.SmartNotificationMessageParametersMulti;
import com.cannontech.common.smartNotification.service.SmartNotificationMessageParametersHandler;
import com.cannontech.tools.email.EmailMessage;
import com.cannontech.yukon.INotifConnection;
import com.google.common.collect.ImmutableMap;

/**
 * Handler for emails. Builds an email and sends it via the notification service.
 */
public class SmartNotificationEmailMessageParametersHandler implements SmartNotificationMessageParametersHandler {
    private static final Logger log = YukonLogManager.getLogger(SmartNotificationEmailMessageParametersHandler.class);
    private Map<SmartNotificationEventType, SmartNotificationEmailBuilder> emailBuilders;
    @Autowired private INotifConnection notifClientConnection;
    
    @Autowired
    public SmartNotificationEmailMessageParametersHandler(List<SmartNotificationEmailBuilder> emailBuilderList) {
        // Map of notification types to their email builders
        ImmutableMap.Builder<SmartNotificationEventType, SmartNotificationEmailBuilder> mapBuilder = ImmutableMap.builder();
        for (SmartNotificationEmailBuilder emailBuilder : emailBuilderList) {
            mapBuilder.put(emailBuilder.getSupportedType(), emailBuilder);
        }
        emailBuilders = mapBuilder.build();
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
            EmailMessage message = emailBuilders.get(parametersMulti.getType())
                                                .buildEmail(parametersMulti);
        
            notifClientConnection.sendEmail(message);
        } catch (Exception e) {
            log.debug(parametersMulti);
            log.error("Unable to send Smart Notification email.", e);
        }
    }
}
