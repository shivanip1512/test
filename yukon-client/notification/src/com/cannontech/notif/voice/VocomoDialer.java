package com.cannontech.notif.voice;

import java.io.IOException;
import java.lang.reflect.Field;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.cache.functions.RoleFuncs;
import com.cannontech.notif.voice.callstates.*;
import com.cannontech.notif.voice.callstates.UnknownError;
import com.cannontech.roles.yukon.SystemRole;
import com.cannontech.roles.yukon.VoiceServerRole;
import com.vocomo.vxml.tools.MakeCall;

/**
 *  
 */
public class VocomoDialer extends Dialer {

    public void dialCall(Call call) {
        call.changeState(new Connecting());
        String phoneNumber = getPhonePrefix()
                             + call.getNumber().getPhoneNumber();
        String voiceHost =
            RoleFuncs.getGlobalPropertyValue(SystemRole.VOICE_HOST);
        MakeCall mc = new MakeCall(voiceHost, true);

        int rInt;
        try {
            String queryString = generateQueryString(call.getCallParameters());
            
            String voiceApp =
                RoleFuncs.getGlobalPropertyValue(VoiceServerRole.VOICE_APP);
            int callTimeout =
                Integer.parseInt(RoleFuncs.getGlobalPropertyValue(VoiceServerRole.CALL_TIMEOUT));

            rInt = mc.makeCall(phoneNumber, voiceApp, callTimeout, queryString);
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
                CTILogger.error("An unknown error code (" + rInt + ") was received while making call " + call);
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

}
