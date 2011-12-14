package com.cannontech.notif.outputs;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;


public class OutputHandlerHelper {

    private @Autowired List<OutputHandler> outputHandlers;
    
    public void handleNotification(NotificationBuilder notif, List<Contactable> contactableList) {
        for (Iterator<Contactable> iter = contactableList.iterator(); iter.hasNext();) {
            Contactable contact = iter.next();
            for (OutputHandler handler : outputHandlers) {
                if (contact.supportsNotificationMethod(handler.getNotificationMethod())) {
                    handler.handleNotification(notif, contact);
                }
            }
        }
    }
    
    public void handleNotification(NotificationBuilder notif, Contactable contactable) {
        handleNotification(notif, Collections.singletonList(contactable));
    }
    
    public void startup() {
        for (OutputHandler handler : outputHandlers) {
            handler.startup();
        }
    }

    public void shutdown() {
        for (OutputHandler handler : outputHandlers) {
            handler.shutdown();
        }
    }
}
