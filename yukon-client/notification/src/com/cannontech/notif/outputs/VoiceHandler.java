package com.cannontech.notif.outputs;

import org.jdom.Document;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

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
        _queue = new NotificationQueue(_callPool);
        _transformer = new NotificationTransformer("file:/C:/Documents and Settings/tmack/Desktop/"); //TODO use role property
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
            XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
            //TODO this is a waste of memory
            String voiceXmlString = outputter.outputString(voiceXml);
            
            SingleNotification singleNotification = 
                new SingleNotification(contact, voiceXmlString);
            _queue.add(singleNotification);
            
        } catch (TransformException e) {
            CTILogger.error("Unable to handle voice notification for " + contact, e);
        }
    }

    public void startup() {
        _acceptNewNotifications = true;
    }

    public void shutdown() {
        try {
            // First, stop accepting new notifications. 
            _acceptNewNotifications  = false;
            
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
