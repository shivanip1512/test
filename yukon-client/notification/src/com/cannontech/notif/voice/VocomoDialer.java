package com.cannontech.notif.voice;

import java.io.IOException;
import java.lang.reflect.Field;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.notif.voice.callstates.*;
import com.cannontech.notif.voice.callstates.UnknownError;
import com.vocomo.vxml.tools.MakeCall;

/**
 *  
 */
public class VocomoDialer extends Dialer {
    
    private String _voiceHost;
    private String _voiceApp;
    private int _callTimeout;

    public VocomoDialer(String voiceHost, String voiceApp) {
        _voiceHost = voiceHost;
        _voiceApp = voiceApp;
        _callTimeout = 120; // I think this is seconds, but it isn't well documented
    }

    public void dialCall(Call call) {
        call.changeState(new Connecting());
        String phoneNumber = getPhonePrefix()
                             + call.getNumber().getPhoneNumber();
        MakeCall mc = new MakeCall(_voiceHost, true);

        int rInt;
        try {
            String queryString = generateQueryString(call.getCallParameters());
            
            CTILogger.debug("Dialing " + phoneNumber + " on " + _voiceHost + " with " + _voiceApp);
            rInt = mc.makeCall(phoneNumber, _voiceApp, _callTimeout, queryString);
            switch (rInt) {
            case MakeCall.CONNECTED:
                // there is nothing we do at this point, we must wait to hear from the JSP
                break;
            case MakeCall.NO_ANSWER:
            case MakeCall.NO_RINGBACK:
            case MakeCall.TIMEOUT:
            case MakeCall.BUSY:
                call.changeState(new NoConnection(decodeStatus(rInt)));
                break;
            case MakeCall.NO_CHANNEL:
                call.changeState(new Retry());
                break;

            default:
                CTILogger.error("An unknown error code (" + rInt 
                                + "[" + decodeStatus(rInt) + "]) was received while making call " + call);
                // We'll leave the call state alone. If it happens to go 
                // through, great; otherwise, it will just timeout.
                break;
            }
        } catch (IOException e) {
            CTILogger.error("Unable to complete call " + call, e);
            call.changeState(new UnknownError(e));
        }

    }
    
    private static String decodeStatus(int status) {
        Field[] fields = MakeCall.class.getFields();
        for (int i = 0; i < fields.length; i++) {
            try {
                Field field = fields[i];
                if (field.getType().equals(int.class)) {
                    if (field.getInt(null) == status) {
                        return field.getName();
                    }
                }
            } catch (IllegalArgumentException e) {
            } catch (IllegalAccessException e) {
            }
        }
        return "unknown status";
    }

    public String toString() {
        return "Vocomo Dialer";
    }

    /**
     * @return Number of seconds to wait for connection
     */
    public int getCallTimeout() {
        return _callTimeout;
    }

    /**
     * @param callTimeout Number of seconds to wait for connection
     */
    public void setCallTimeout(int callTimeout) {
        _callTimeout = callTimeout;
    }

}
