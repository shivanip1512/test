package com.cannontech.notif.server;

/**
 * This type was created in VisualAge.
 */
import java.net.Socket;

import com.cannontech.message.dispatch.message.DefineCollectableMulti;
import com.cannontech.message.util.ClientConnection;
import com.cannontech.notif.message.DefColl_NotifEmailAttchMsg;
import com.cannontech.notif.message.DefColl_NotifEmailMsg;
import com.cannontech.notif.message.DefColl_NotifVoiceMsg;
import com.roguewave.vsj.CollectableStreamer;
import com.roguewave.vsj.DefineCollectable;
import com.roguewave.vsj.streamer.CollectableMappings;

public class NotifServerConnection extends ClientConnection
{
	private NotifMsgHandler handlr = new NotifMsgHandler();


	//the rwav messages we listen for
	private static final DefineCollectable[] DEFAULT_MAPPINGS =
	{
		CollectableMappings.OrderedVector,
		
		new DefColl_NotifEmailAttchMsg(),
		new DefColl_NotifEmailMsg(),
		new DefineCollectableMulti(),
        new DefColl_NotifVoiceMsg()

	};
	
	/**
	 * ClientConnection constructor comment.
	 * @param host java.lang.String
	 * @param port int
	 */
	public NotifServerConnection( Socket socket_ ) 
	{
		super( socket_ );
		addMessageListener( handlr );
	}

	/**
	 * This method was created in VisualAge.
	 */
	protected void registerMappings(CollectableStreamer polystreamer) 
	{
		super.registerMappings(polystreamer);
	
		for( int i = 0; i < DEFAULT_MAPPINGS.length; i++ )
		{
			polystreamer.register( DEFAULT_MAPPINGS[i] );
		}
	}


}
