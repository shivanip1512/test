package com.cannontech.services.smartNotification.service.impl;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.smartNotification.model.SmartNotificationMedia;
import com.cannontech.common.smartNotification.model.SmartNotificationMessageParameters;
import com.cannontech.common.smartNotification.model.SmartNotificationMessageParametersMulti;
import com.cannontech.common.stream.StreamUtils;
import com.cannontech.services.smartNotification.service.SmartNotificationMessageParametersHandler;

public class SmartNotificationMessageAssembler implements MessageListener {
    private static final Logger log = YukonLogManager.getLogger(SmartNotificationMessageAssembler.class);
    private Map<SmartNotificationMedia, SmartNotificationMessageParametersHandler> mediaHandlers;
    
    @Autowired
    public SmartNotificationMessageAssembler(List<SmartNotificationMessageParametersHandler> messageParametersHandlers) {
        // Map of media to their handler methods
        mediaHandlers = messageParametersHandlers.stream()
                                                 .collect(StreamUtils.mapToSelf(handler -> handler.getSupportedMedia()));
    }
    
    @Override
    public void onMessage(Message message) {
        ObjectMessage objMessage = (ObjectMessage) message;
        try {
            if (message instanceof ObjectMessage) {
                Serializable object = objMessage.getObject();
                if (object instanceof SmartNotificationMessageParametersMulti) {
                    handle((SmartNotificationMessageParametersMulti) object);
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
        log.debug("Processing message: " + parametersMulti);
        if (parametersMulti.isSendAllInOneEmail()) {
            // This could be coalesced notifications of a single type, or a combined digest of different types.
            // Build all these parameter objects into a single email
            SmartNotificationMessageParametersHandler builder = mediaHandlers.get(parametersMulti.getMedia());
            if (builder == null) {
                log.error("Unable to send notification - unsupported media type: " + parametersMulti.getMedia());
                log.debug(parametersMulti);
            }
            builder.buildMultiAndSend(parametersMulti);
        } else {
            // Process each parameters object individually
            int intervalMinutes = parametersMulti.getIntervalMinutes();
            for(SmartNotificationMessageParameters parameters : parametersMulti.getMessageParameters()) {
                SmartNotificationMessageParametersHandler builder = mediaHandlers.get(parameters.getMedia());
                if (builder == null) {
                    log.error("Unable to send notification - unsupported media type: " + parameters.getMedia());
                    log.debug(parameters);
                }
                builder.buildAndSend(parameters, intervalMinutes);
            }
        }
    }
}
