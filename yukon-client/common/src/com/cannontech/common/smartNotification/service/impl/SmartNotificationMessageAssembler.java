package com.cannontech.common.smartNotification.service.impl;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.smartNotification.model.SmartNotificationEventType;
import com.cannontech.common.smartNotification.model.SmartNotificationMedia;
import com.cannontech.common.smartNotification.model.SmartNotificationMessageParameters;
import com.cannontech.common.smartNotification.model.SmartNotificationMessageParametersMulti;
import com.cannontech.common.smartNotification.service.impl.email.SmartNotificationEmailBuilder;
import com.cannontech.tools.email.EmailMessage;
import com.cannontech.yukon.conns.NotifClientConnection;
import com.google.common.collect.ImmutableMap;

public class SmartNotificationMessageAssembler implements MessageListener {
    private static final Logger log = YukonLogManager.getLogger(SmartNotificationMessageAssembler.class);
    
    private NotifClientConnection notifClientConnection;
    private Map<SmartNotificationMedia, Consumer<SmartNotificationMessageParameters>> mediaBuilderFunctions;
    private Map<SmartNotificationEventType, SmartNotificationEmailBuilder> emailBuilders;
    
    @Autowired
    public SmartNotificationMessageAssembler(List<SmartNotificationEmailBuilder> emailBuilderList, 
                                             NotifClientConnection notifClientConnection) {
        
        this.notifClientConnection = notifClientConnection;
        
        // Map of media to their handler methods
        mediaBuilderFunctions = ImmutableMap.of(
            SmartNotificationMedia.EMAIL, params -> handleEmail(params)
        );
        
        // Map of notification types to their email builders
        ImmutableMap.Builder<SmartNotificationEventType, SmartNotificationEmailBuilder> mapBuilder = ImmutableMap.builder();
        for (SmartNotificationEmailBuilder emailBuilder : emailBuilderList) {
            mapBuilder.put(emailBuilder.getSupportedType(), emailBuilder);
        }
        emailBuilders = mapBuilder.build();
    }
    
    @Override
    public void onMessage(Message message) {
        ObjectMessage objMessage = (ObjectMessage) message;
        Serializable object;
        try {
            if (message instanceof ObjectMessage) {
                object = objMessage.getObject();
                if (object instanceof SmartNotificationMessageParametersMulti) {
                    SmartNotificationMessageParametersMulti multi = (SmartNotificationMessageParametersMulti) object;
                    log.debug("Processing message: " + multi);
                    handle(multi);
                }
            }
        } catch (JMSException e) {
            log.error("Unable to extract message", e);
        } catch (Exception e) {
            log.error("Unable to process message", e);
        }
    }
    
    /**
     * Receives a message parameters object and passes it to the appropriate handler method for processing.
     */
    public void handle(SmartNotificationMessageParametersMulti parametersMulti) {
        if (parametersMulti.isSendAllInOneEmail()) {
            // Build all these parameter objects into a single super-digest
            //TODO
            //Build all the body texts, maybe use subject text as the section header?
            //Build a special digest subject line
            //Get all recipients
            //Compile everything into an email
            //Send the email: notifClientConnection.sendEmail(message)
        } else {
            // Process each parameters object individually
            for(SmartNotificationMessageParameters parameters : parametersMulti.getMessageParameters()) {
                Consumer<SmartNotificationMessageParameters> builderFunction = mediaBuilderFunctions.get(parameters.getMedia());
                if (builderFunction == null) {
                    log.error("Unable to send notification - unsupported media type: " + parameters.getMedia());
                    log.debug(parameters);
                }
                builderFunction.accept(parameters);
            }
        }
    }
    
    /**
     * Handler method for emails. Builds an email and sends it via the notification service.
     */
    private void handleEmail(SmartNotificationMessageParameters parameters) {
        try {
            EmailMessage message = emailBuilders.get(parameters.getType())
                                                .buildEmail(parameters);
            notifClientConnection.sendEmail(message);
        } catch (Exception e) {
            log.debug(parameters);
            log.error("Unable to send Smart Notification email.", e);
        }
    }
}
