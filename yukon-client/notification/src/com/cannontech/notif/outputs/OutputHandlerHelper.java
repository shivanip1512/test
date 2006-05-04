package com.cannontech.notif.outputs;

import java.util.*;

public class OutputHandlerHelper {
    /**
     * contains OutputHandler objects
     */
    private List<OutputHandler> _handlers = new LinkedList<OutputHandler>();
    
    public OutputHandlerHelper(List<OutputHandler> handlers) {
        _handlers = handlers;
    }

    public void handleNotification(NotificationBuilder notif, List<Contactable> contactableList) {
        for (Iterator<Contactable> iter = contactableList.iterator(); iter.hasNext();) {
            Contactable contact = iter.next();
            for (Iterator<OutputHandler> i = _handlers.iterator(); i.hasNext();) {
                OutputHandler handler = i.next();
                if (contact.supportsNotificationMethod(handler.getNotificationMethod())) {
                    handler.handleNotification(notif, contact);
                }
            }
        }
    }
    
    public void handleNotification(NotificationBuilder notif, Contactable contactable) {
        handleNotification(notif, Collections.singletonList(contactable));
    }
    
    public void addOutputHandler(OutputHandler handler) {
        _handlers.add(handler);
    }
    
    public void startup() {
        for (Iterator<OutputHandler> i = _handlers.iterator(); i.hasNext();) {
            OutputHandler handler = i.next();
            handler.startup();
        }
    }

    public void shutdown() {
        for (Iterator<OutputHandler> i = _handlers.iterator(); i.hasNext();) {
            OutputHandler handler = i.next();
            handler.shutdown();
        }
    }

}
