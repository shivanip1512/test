package com.cannontech.notif.outputs;

import org.jdom.Document;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.cache.functions.RoleFuncs;
import com.cannontech.notif.outputs.NotificationTransformer.TransformException;
import com.cannontech.notif.voice.*;
import com.cannontech.notif.voice.callstates.Confirmed;
import com.cannontech.notif.voice.callstates.Unconfirmed;
import com.cannontech.roles.yukon.SystemRole;
import com.cannontech.roles.yukon.VoiceServerRole;

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
    private NotificationTransformer _transformer;
    
    public VoiceHandler() {
        super(Contactable.VOICE);
        
        String voiceHost = RoleFuncs.getGlobalPropertyValue(SystemRole.VOICE_HOST);
        String voiceApp = RoleFuncs.getGlobalPropertyValue(VoiceServerRole.VOICE_APP);
        VocomoDialer dialer = new VocomoDialer(voiceHost, voiceApp);
        
        dialer.setPhonePrefix("9"); //TODO use role property
        int callTimeout = Integer.parseInt(RoleFuncs.getGlobalPropertyValue(VoiceServerRole.CALL_TIMEOUT));
        int numberOfChannels = 1; //TODO use role property
        _callPool = new CallPool(dialer, numberOfChannels, callTimeout);
        _queue = new NotificationQueue();
        _worker = new WorkerThread(_queue, _callPool);
        _transformer = new NotificationTransformer("file://blah/blah/");
    }
    
    public void handleNotification(Notification notif, Contactable contact) {
        if (_acceptNewNotifications) {
            // I could throw an exception here, but what would the caller do???
            CTILogger.error("com.cannontech.notif.outputs.VoiceHandler.handleNotification() " +
                    "called after shutdown (or before startup).");
            return;
        }
        
        try {
            Document voiceXml = _transformer.transform(notif, getType());
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

    public void completeCall(String token, boolean gotConfirmation) {
        Call call = _callPool.getCall(token);
        if (gotConfirmation) {
            call.changeState(new Confirmed());
        } else {
            call.changeState(new Unconfirmed());
        }
    }

    public Object getCallData(String token) {
        Call call = _callPool.getCall(token);
        return call.getMessage();
    }
    
}
