package com.cannontech.notif.handler;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.cache.functions.NotificationGroupFuncs;
import com.cannontech.database.data.lite.LiteNotificationGroup;
import com.cannontech.message.util.Message;
import com.cannontech.notif.message.NotifEmailMsg;
import com.cannontech.tools.email.EmailMessage;


/**
 * @author rneuharth
 *
 *  Handles all outgoing voice queries and responses.
 * 
 */
class EmailHandler implements INotifHandler
{
    private NotifEmailMsg _emailMsg = null;

    EmailHandler( NotifEmailMsg msg )
    {
        super();

        _emailMsg = msg;
    }

    /**
     * Starts the handling of the given message
     * 
     */
    public void start()
    {
        if( isValid(_emailMsg) )
        {
            CTILogger.error("Invalid message found in " + this.getClass().getName() + ", ,message= " + _emailMsg );
            return;
        }


        try
        {
            EmailMessage outMsg = new EmailMessage( _emailMsg.getTo(), _emailMsg.getSubject(), _emailMsg.getBody() );
            
            //if we hav a NotificationGroupID, use its values instead
            if( _emailMsg.getNotifGroupID() != CtiUtilities.NONE_ID )
            {
                LiteNotificationGroup lGrp =
                        NotificationGroupFuncs.getLiteNotificationGroup(_emailMsg.getNotifGroupID());
                        
                if( lGrp == null )
                {
                    CTILogger.info(
                            "FAILURE: Email attempt to a a non-existant notification, GroupID= " + _emailMsg.getNotifGroupID() );
                    return;
                }

                if( lGrp.isDisabled() )
                {
                    CTILogger.info(
                            "FAILURE: Email attempt to a DISABLED notification group has been blocked (" + lGrp.getNotificationGroupName() + ")" ); 
                    return;
                }

                outMsg.addTo_Array( NotificationGroupFuncs.getNotifEmailsByLiteGroup(lGrp) );
                outMsg.setSubject( lGrp.getEmailSubject() );
                outMsg.setFrom( lGrp.getEmailFrom() );
                outMsg.setBody( lGrp.getEmailBody() );
            }


            outMsg.setTo_CC( _emailMsg.getTo_CC() );
            outMsg.setTo_BCC( _emailMsg.getTo_BCC() );
            
            for( int i = 0; i < _emailMsg.getAttachments().size(); i++ )
            {
                outMsg.getAttachmentNames().add( _emailMsg.getAttachmentAt( i ).getFileName() );
                outMsg.getAttachments().add( _emailMsg.getAttachmentAt( i ).getFileContents() );
            }
            
            outMsg.send();
        }
        catch( Exception e )
        {
            CTILogger.error( e.getMessage(), e );
        }

    }
    
    /**
     * Checks for validity of the given message
     * 
     */
    public boolean isValid( Message msg )
    {
        return msg instanceof NotifEmailMsg;
    }
    
    
}
