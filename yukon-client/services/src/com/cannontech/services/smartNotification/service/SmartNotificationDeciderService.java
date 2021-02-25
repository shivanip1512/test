package com.cannontech.services.smartNotification.service;

import java.util.List;

import com.cannontech.common.smartNotification.model.SmartNotificationMessageParameters;
import com.cannontech.services.smartNotification.service.SmartNotificationDecider.ProcessorResult;

public interface SmartNotificationDeciderService {

    /**
     * Puts messages on message assembler queue, reschedules if needed
     */
    void send(ProcessorResult result);

    /**
     * Logs comms info message.
     */
    
    void logInfo(String text, Object obj);

    /**
     * Returns first interval.
     */
    int getFirstInterval();

    void putMessagesOnAssemblerQueue(List<SmartNotificationMessageParameters> messages, int interval, 
                                     boolean sendAllInOneEmail, String digestTime);


}
