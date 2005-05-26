package com.cannontech.notif.outputs;

import org.jdom.Document;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.notif.outputs.NotificationTransformer.TransformException;
import com.cannontech.notif.test.TestDialer;
import com.cannontech.notif.voice.*;

/**
 * @author rneuharth
 *
 *  Handles all outgoing voice queries and responses.
 * 
 */
public class VoiceHandler extends OutputHandler
{
    private NotificationQueue _queue;
    private WorkerThread _worker;
    private CallPool _callPool;
    private boolean _acceptNewNotifications = false;
    
    public VoiceHandler() {
        super(Contactable.VOICE);
        Dialer dialer = new TestDialer();
        dialer.setPhonePrefix("9");
        _callPool = new CallPool(dialer, 1);
        _queue = new NotificationQueue();
        _worker = new WorkerThread(_queue, _callPool);
    }
    
    public void handleNotification(Notification notif, Contactable contact) {
        if (_acceptNewNotifications) {
            // I could throw an exception here, but what would the caller do???
            CTILogger.error("com.cannontech.notif.outputs.VoiceHandler.handleNotification() " +
                    "called after shutdown (or before startup).");
            return;
        }
        
        NotificationTransformer transform = NotificationTransformer.getInstance();
        try {
            Document voiceXml = transform.transform(notif, getType());
            //TODO should we convert to text right here???
            
            SingleNotification singleNotification = 
                new SingleNotification(contact, voiceXml);
            _queue.add(singleNotification);
            
        } catch (TransformException e) {
            CTILogger.error("Unable to handle voice notification for " + contact, e);
        }
    }

    public void startup() {
        _worker.start();
        _acceptNewNotifications = true;
    }

    public void shutdown() {
        try {
            // First, stop accepting new notifications. 
            _acceptNewNotifications  = false;
            
            // Next, shutdown the worker thread so objects will stop getting 
            // put on the call pool, this method blocks until the thread
            // is fully stopped.
            _worker.shutdown();
            
            // Finally, shutdown the call pool. This call will block until
            // all calls have completed.
            _callPool.shutdown();
            

        } catch (InterruptedException e) {
            CTILogger.warn("Got interrupted while trying to shutdown.", e);
        }
    }
    
    public CallPool getCallPool() {
        return _callPool;
    }
    
}
