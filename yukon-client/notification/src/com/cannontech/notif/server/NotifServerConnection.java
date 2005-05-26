package com.cannontech.notif.server;

import java.net.Socket;

import com.cannontech.message.dispatch.message.DefineCollectableMulti;
import com.cannontech.message.util.ClientConnection;
import com.cannontech.notif.message.*;
import com.roguewave.vsj.CollectableStreamer;
import com.roguewave.vsj.DefineCollectable;
import com.roguewave.vsj.streamer.CollectableMappings;

public class NotifServerConnection extends ClientConnection
{
	//the rwav messages we listen for
	private static final DefineCollectable[] DEFAULT_MAPPINGS =
	{
		CollectableMappings.OrderedVector,
		
		//new DefColl_NotifEmailAttchMsg(),
		//new DefColl_NotifEmailMsg(),
		new DefineCollectableMulti(),
        //new DefColl_NotifVoiceMsg()
        
        new DefColl_NotifAlarmMsg(),
        new DefColl_NotifLMControlMsg(),
        new DefColl_VoiceDataRequestMsg(),
        new DefColl_VoiceDataResponseMsg() //TODO does this belong here?

	};
	
	public NotifServerConnection( Socket socket_, NotifMsgHandler handler ) 
	{
		super( socket_ );
		addMessageListener( handler );
	}

	protected void registerMappings(CollectableStreamer polystreamer) 
	{
		super.registerMappings(polystreamer);
	
		for( int i = 0; i < DEFAULT_MAPPINGS.length; i++ )
		{
			polystreamer.register( DEFAULT_MAPPINGS[i] );
		}
	}


}
