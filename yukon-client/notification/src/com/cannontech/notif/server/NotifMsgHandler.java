package com.cannontech.notif.server;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.message.dispatch.message.Multi;
import com.cannontech.message.util.MessageEvent;
import com.cannontech.message.util.MessageListener;
import com.cannontech.notif.handler.NotifHandler;
import com.cannontech.notif.message.NotifEmailMsg;
import com.cannontech.notif.message.NotifResultMsg;
import com.cannontech.notif.message.NotifVoiceMsg;

/**
 * @author rneuharth
 *
 * Handles messages for the Notifcation Server.
 *
 */
public class NotifMsgHandler implements MessageListener
{
	public NotifMsgHandler()
	{
		super();
	}
	
	public void messageReceived( MessageEvent e )
	{
		//do the stuff here
		handleMessage( e.getMessage() );
        
	}


	private NotifResultMsg handleMessage( Object msg_ )
	{
		if( msg_ instanceof Multi )
		{
			for( int i = 0; i < ((Multi)msg_).getVector().size(); i++ )
			{
				handleMessage( ((Multi)msg_).getVector().get(i) );
			}
			
			return new NotifResultMsg("SUCCESS: Multi message handled", NotifResultMsg.RESULT_SUCCESS);
		}
		else if( msg_ instanceof NotifEmailMsg )
		{
			CTILogger.debug(
				" Notification server got an email message from: " + ((NotifEmailMsg)msg_).getSource() );

            NotifHandler.findHandler( (NotifEmailMsg)msg_ ).start();
            return null;
		}
        else if( msg_ instanceof NotifVoiceMsg )
        {
            CTILogger.debug(
                " Notification server got an outbound voice message request from: " +
                ((NotifVoiceMsg)msg_).getSource() );

            
            NotifHandler.findHandler( (NotifVoiceMsg)msg_ ).start();
            return null;
        }
		else
		{
			CTILogger.debug(
				" ERROR message received that is of an unknown type: " + msg_.getClass().getName() );

			return new NotifResultMsg(
				"ERROR: The message was an invalid notification mesage", NotifResultMsg.RESULT_FAILED);
		}

	}

}
