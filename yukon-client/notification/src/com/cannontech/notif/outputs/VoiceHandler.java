package com.cannontech.notif.outputs;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.jdom.Document;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.data.lite.LiteContactNotification;
import com.cannontech.database.data.notification.NotifType;
import com.cannontech.message.dispatch.ClientConnection;
import com.cannontech.notif.voice.*;
import com.cannontech.notif.voice.callstates.Confirmed;
import com.cannontech.notif.voice.callstates.Unconfirmed;

/**
 * @author rneuharth
 *
 *  Handles all outgoing voice queries and responses.
 * 
 */
public class VoiceHandler extends OutputHandler
{
    private NotificationQueue _queue;
    private boolean _acceptNewNotifications = false;
    private NotificationTransformer _transformer;
    
    public VoiceHandler(ClientConnection dispatchConnection) {
        super("voice");
                
        _queue = new NotificationQueue();
        
    }
    
    public void handleNotification(final NotificationBuilder notifBuilder, final Contactable contact) {
        if (!_acceptNewNotifications) {
            // I could throw an exception here, but what would the caller do???
            CTILogger.error("com.cannontech.notif.outputs.VoiceHandler.handleNotification() " +
                    "called after shutdown (or before startup).");
            return;
        }
        
        try {
            Notification notif = notifBuilder.buildNotification(contact);
            
            _transformer = new NotificationTransformer(contact.getEnergyCompany(), getType());
            Document voiceXml = _transformer.transform(notif);
            
            SingleNotification singleNotification = 
                new SingleNotification(contact, voiceXml);
            
            // Add a listener to do system logging.
            singleNotification.addPropertyChangeListener(new PropertyChangeListener() {
               public void propertyChange(PropertyChangeEvent evt) {
                   String newState = (String) evt.getNewValue();
                   if (newState.equals(SingleNotification.STATE_COMPLETE)) {
                       notifBuilder.notificationComplete(contact, getNotificationMethod(), true);
                   } else if (newState.equals(SingleNotification.STATE_FAILED)) {
                       notifBuilder.notificationComplete(contact, getNotificationMethod(), false);
                   }
                } 
            });
            
            singleNotification.setNotificationLogger(new NotificationStatusLogger() {
                public void logIndividualNotification(LiteContactNotification destination, boolean success) {
                    notifBuilder.logIndividualNotification(destination, contact, getNotificationMethod(), success);
                }
            });
            
            // Add the notification to the queue.
            _queue.add(singleNotification);
            
        } catch (Exception e) {
            CTILogger.error("Unable to handle voice notification for " + contact, e);
            notifBuilder.notificationComplete(contact, getNotificationMethod(), false);
        }
    }

    public void startup() {
        _acceptNewNotifications = true;
    }

    public void shutdown() {
        // First, stop accepting new notifications. 
        _acceptNewNotifications  = false;
        
        // Finally, shutdown the call pool. This call will block until
        // all calls have completed.
        _queue.shutdown();
            
    }
    
    public void completeCall(String token, boolean gotConfirmation) {
        try {
            Call call = _queue.getCall(token);
            if (gotConfirmation) {
                call.changeState(new Confirmed());
            } else {
                call.changeState(new Unconfirmed());
            }
        } catch (Exception e) {
            CTILogger.warn("Unable to complete call (token=" + token + ", confiration=" + gotConfirmation + ")", e);
        }
    }

    public Document getCallData(String token) throws UnknownCallTokenException {
        Call call = _queue.getCall(token);
        return (Document)call.getMessage();
    }

    public NotifType getNotificationMethod() {
        return NotifType.VOICE;
    }
    
}
