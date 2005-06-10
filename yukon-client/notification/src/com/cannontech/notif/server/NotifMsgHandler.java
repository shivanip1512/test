package com.cannontech.notif.server;

import java.util.Iterator;
import java.util.LinkedList;

import com.cannontech.message.dispatch.message.Multi;
import com.cannontech.message.util.*;
import com.cannontech.notif.handler.MessageHandler;

/**
 * Handles messages for the Notifcation Server.
 * @author rneuharth
 */
public class NotifMsgHandler implements MessageListener
{
    private LinkedList _handlers = new LinkedList();
    
	public NotifMsgHandler()
	{
	}
    
	public void messageReceived( MessageEvent e )
	{
		//do the stuff here
		handleMessage( (NotifServerConnection)e.getSource(), e.getMessage() );
        
	}
    
    public void registerHandler(MessageHandler handler) {
       _handlers.add(handler);
    }

	private void handleMessage( NotifServerConnection connection, Message msg_ )
	{
		if( msg_ instanceof Multi )
		{
            Multi multiMsg = (Multi)msg_;
			for( int i = 0; i < ((Multi)msg_).getVector().size(); i++ )
			{
				handleMessage( connection, (Message)multiMsg.getVector().get(i) );
			}
		}
		else 
		{
            for (Iterator iter = _handlers.iterator(); iter.hasNext();) {
                MessageHandler handler = (MessageHandler) iter.next();
                if (handler.canHandle(msg_)) {
                    handler.handleMessage(connection, msg_);
                }
            }
		}
	}

    public void testHanldeMessage(Message msg) {
        handleMessage(null, msg);
    }
}
