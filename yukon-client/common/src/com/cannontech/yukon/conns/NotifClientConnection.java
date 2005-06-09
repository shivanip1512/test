package com.cannontech.yukon.conns;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.cache.functions.RoleFuncs;
import com.cannontech.message.dispatch.message.DefineCollectableMulti;
import com.cannontech.message.server.ServerResponseMsg;
import com.cannontech.message.util.ClientConnection;
import com.cannontech.message.util.ServerRequest;
import com.cannontech.message.notif.*;
import com.cannontech.roles.yukon.SystemRole;
import com.roguewave.vsj.CollectableStreamer;
import com.roguewave.vsj.DefineCollectable;
import com.roguewave.vsj.streamer.CollectableMappings;

public class NotifClientConnection extends ClientConnection
{
	//the rwav messages we listen for
	public static final DefineCollectable[] DEFAULT_MAPPINGS =
	{
		CollectableMappings.OrderedVector,
		
		//new DefColl_NotifEmailAttchMsg(),
		//new DefColl_NotifEmailMsg(),
		//new DefColl_NotifVoiceMsg()
		new DefineCollectableMulti(),
        
        new DefColl_NotifAlarmMsg(),
        new DefColl_NotifLMControlMsg(),
        new DefColl_VoiceDataRequestMsg(),
        new DefColl_VoiceDataResponseMsg()
	};
	
	public NotifClientConnection() 
	{
		super();
		initialize();
		getResources();
	}

	/**
	 * Get our properties for this connection from the cache
	 *
	 */
	private void getResources() 
	{
		setHost( RoleFuncs.getGlobalPropertyValue( SystemRole.NOTIFICATION_HOST ) );

		setPort( Integer.parseInt(
			RoleFuncs.getGlobalPropertyValue( SystemRole.NOTIFICATION_PORT ) ) );	
	}

	/**
	 * Any special initing should be done here.
	 * 
	 */
	private void initialize() 
	{	
		setAutoReconnect( true );
	}	


	protected void registerMappings(CollectableStreamer polystreamer) 
	{
		super.registerMappings(polystreamer);
	
		for( int i = 0; i < DEFAULT_MAPPINGS.length; i++ )
		{
			polystreamer.register( DEFAULT_MAPPINGS[i] );
		}
	}



	/**
	 * Reuests the String message from the server using the
	 * given token.
	 * 
	 */
	public synchronized String requestMessage( String token ) throws NotifRequestException {

		String retStr = null;
		if( token == null )
			return retStr;

		ServerResponseMsg responseMsg = null;
		VoiceDataRequestMsg vdReqMsg = new VoiceDataRequestMsg();
		vdReqMsg.token = token;
		ServerRequest srvrReq =
			ServerRequest.makeServerRequest( this, vdReqMsg );

		try {
			//request the object from the server
			responseMsg = srvrReq.execute();
		}
		catch(Exception e) {
			CTILogger.error( "No response received from server", e );
		}

		if( responseMsg != null 
			&& responseMsg.getStatus() == ServerResponseMsg.STATUS_OK )  {

			VoiceDataResponseMsg vdRespMsg = (VoiceDataResponseMsg)responseMsg.getPayload();
			retStr = vdRespMsg.xmlData;
		}
		else {
			retStr = "Unable to get the requested voice " + 
					"message, responseCode= " +  responseMsg.getStatus() +
					", token= " + vdReqMsg.token;
			CTILogger.info(retStr);
			throw new NotifRequestException(retStr);
		}

		return retStr;
	}

}
