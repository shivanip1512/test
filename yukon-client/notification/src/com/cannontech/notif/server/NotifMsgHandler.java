package com.cannontech.notif.server;

import java.util.Observable;
import java.util.Observer;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.cache.functions.NotificationGroupFuncs;
import com.cannontech.database.data.lite.LiteNotificationGroup;
import com.cannontech.message.dispatch.message.Multi;
import com.cannontech.message.util.MessageEvent;
import com.cannontech.message.util.MessageListener;
import com.cannontech.notif.message.NotifEmailMsg;
import com.cannontech.notif.message.NotifResultMsg;
import com.cannontech.tools.email.EmailMessage;

/**
 * @author rneuharth
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class NotifMsgHandler implements Observer, MessageListener
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

			return doEmail( (NotifEmailMsg)msg_ );
		}
		else
		{
			CTILogger.debug(
				" ERROR message received that is of an unknown type: " + msg_.getClass().getName() );

			return new NotifResultMsg(
				"ERROR: The message was an invalid notification mesage", NotifResultMsg.RESULT_FAILED);
		}

	}


	private NotifResultMsg doEmail( NotifEmailMsg msg_ )
	{
		try
		{
			EmailMessage outMsg = new EmailMessage( msg_.getTo(), msg_.getSubject(), msg_.getBody() );
			
			//if we hav a NotificationGroupID, use its values instead
			if( msg_.getNotifGroupID() != CtiUtilities.NONE_ID )
			{
				LiteNotificationGroup lGrp =
						NotificationGroupFuncs.getLiteNotificationGroup(msg_.getNotifGroupID());
						
				if( lGrp == null )
					return new NotifResultMsg(
							"FAILURE: Email attempt to a a non-existant notification, GroupID= " + 
							msg_.getNotifGroupID(), 
							NotifResultMsg.RESULT_FAILED);

				if( lGrp.isDisabled() )
					return new NotifResultMsg(
							"FAILURE: Email attempt to a DISABLED notification group has been blocked (" + 
							lGrp.getNotificationGroupName() + ")", 
							NotifResultMsg.RESULT_FAILED);

				outMsg.addTo_Array( NotificationGroupFuncs.getNotifEmailsByLiteGroup(lGrp) );
				outMsg.setSubject( lGrp.getEmailSubject() );
				outMsg.setFrom( lGrp.getEmailFrom() );
				outMsg.setBody( lGrp.getEmailBody() );
			}


			outMsg.setTo_CC( msg_.getTo_CC() );
			outMsg.setTo_BCC( msg_.getTo_BCC() );
			
			for( int i = 0; i < msg_.getAttachments().size(); i++ )
			{
				outMsg.getAttachmentNames().add( msg_.getAttachmentAt( i ).getFileName() );
				outMsg.getAttachments().add( msg_.getAttachmentAt( i ).getFileContents() );
			}
			
			outMsg.send();
			
			return new NotifResultMsg("SUCCESS: Email message sent", NotifResultMsg.RESULT_SUCCESS);
		}
		catch( Exception e )
		{
			CTILogger.error( e.getMessage(), e );

			return new NotifResultMsg(
				"FAILURE: Email message was not sent for the following reason: " + 
				e.getMessage(), 
				NotifResultMsg.RESULT_FAILED);
		}
		
	}

	public void update( Observable obs_, Object obj_ )
	{
		//do nothing

	}

}
