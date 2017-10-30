package com.cannontech.services.smartNotification.service;

import com.cannontech.common.smartNotification.model.SmartNotificationMedia;
import com.cannontech.common.smartNotification.model.SmartNotificationMessageParameters;
import com.cannontech.common.smartNotification.model.SmartNotificationMessageParametersMulti;

/**
 * Builds and sends notification messages via a particular media, based on the specified 
 * SmartNotificationMessageParameters objects.
 */
public interface SmartNotificationMessageParametersHandler {
    
    public SmartNotificationMedia getSupportedMedia();
    
    /**
     * Builds a message, based on the specified parameters, and sends it via the supported media.
     * @param parameters The parameters of the message.
     * @param intvervalMinutes The interval (in minutes) that the parameters apply to. For example, if the parameters
     * apply to the past hour, this would be 60.
     */
    public void buildAndSend(SmartNotificationMessageParameters parameters, int intvervalMinutes);
    
    /**
     * Builds a message, based on a set of parameters.
     */
    public void buildMultiAndSend(SmartNotificationMessageParametersMulti parametersMulti);
}
