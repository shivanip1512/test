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
     * Returns log message.
     */
    String getLogMsg(String text, SmartNotificationDecider decider);

    /**
     * Returns first interval.
     */
    int getFirstInterval();

    void putMessagesOnAssemblerQueue(List<SmartNotificationMessageParameters> messages, int interval, 
                                     boolean sendAllInOneEmail);
}
