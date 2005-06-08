package com.cannontech.notif.server;

import java.net.Socket;

import com.cannontech.message.util.ClientConnection;
import com.cannontech.yukon.conns.NotifClientConnection;
import com.roguewave.vsj.CollectableStreamer;

public class NotifServerConnection extends ClientConnection
{	
	public NotifServerConnection( Socket socket_, NotifMsgHandler handler ) 
	{
		super( socket_ );
		addMessageListener( handler );
	}
	
	/**
	 * Allow us to use the same messages our corresponding client connection
	 * uses.
	 * 
	 */
	protected void registerMappings(CollectableStreamer polystreamer) 
	{
		super.registerMappings(polystreamer);
	
		for( int i = 0; i < NotifClientConnection.DEFAULT_MAPPINGS.length; i++ ) 
		{
			polystreamer.register( NotifClientConnection.DEFAULT_MAPPINGS[i] );
		}
	}


}
