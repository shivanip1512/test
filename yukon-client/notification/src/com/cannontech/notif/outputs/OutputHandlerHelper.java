package com.cannontech.notif.outputs;

import java.util.*;

import com.cannontech.database.data.lite.*;

public class OutputHandlerHelper {
    /**
     * contains OutputHandler objects
     */
    private List _handlers = new LinkedList();
    
    public void handleNotification(Notification notif, LiteNotificationGroup lng) {
        List contactables = Contactable.getContactablesForGroup(lng);
        for (Iterator iter = contactables.iterator(); iter.hasNext();) {
            Contactable contact = (Contactable) iter.next();
            for (Iterator i = _handlers.iterator(); i.hasNext();) {
                OutputHandler handler = (OutputHandler) i.next();
                if (contact.hasNotificationMethod(handler.getType())) {
                    handler.handleNotification(notif, contact);
                }
            }
        }
    }
    
    public void addOutputHandler(OutputHandler handler) {
        _handlers.add(handler);
    }
    
    public void startup() {
        for (Iterator i = _handlers.iterator(); i.hasNext();) {
            OutputHandler handler = (OutputHandler) i.next();
            handler.startup();
        }
    }

    public void shutdown() {
        for (Iterator i = _handlers.iterator(); i.hasNext();) {
            OutputHandler handler = (OutputHandler) i.next();
            handler.shutdown();
        }
    }

}
