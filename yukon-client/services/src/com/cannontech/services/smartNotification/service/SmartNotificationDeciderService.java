package com.cannontech.services.smartNotification.service;

import java.util.List;

import com.cannontech.common.smartNotification.model.SmartNotificationMessageParameters;

public interface SmartNotificationDeciderService {

   
    void putMessagesOnAssemblerQueue(List<SmartNotificationMessageParameters> messages, int interval, 
                                     boolean sendAllInOneEmail, String digestTime);
}
