package com.cannontech.services.smartNotification.service;

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
}
