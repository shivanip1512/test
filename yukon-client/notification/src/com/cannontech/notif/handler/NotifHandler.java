package com.cannontech.notif.handler;

import com.cannontech.message.util.Message;
import com.cannontech.notif.message.NotifEmailMsg;
import com.cannontech.notif.message.NotifVoiceMsg;

/**
 * @author rneuharth
 *
 * Decides which instance to use to handle this object
 *
 */
public class NotifHandler
{
    /**
     * 
     */
    private NotifHandler()
    {
        super();
    }

    /**
     * 
     */
    public static INotifHandler findHandler( Message msg )
    {
        if( msg instanceof NotifVoiceMsg )
        {
            return new VoiceHandler( (NotifVoiceMsg)msg );
        }
        else if( msg instanceof NotifEmailMsg )
        {
            return new EmailHandler( (NotifEmailMsg)msg );
        }                
        else
            return null;
    }
    
}
